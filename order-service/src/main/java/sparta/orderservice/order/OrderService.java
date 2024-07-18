package sparta.orderservice.order;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparta.common.messages.CreateOrderDto;
import sparta.common.messages.ProductPriceRequest;
import sparta.orderservice.client.ProductServiceClient;
import sparta.orderservice.config.RabbitMQConfig;
import sparta.orderservice.domain.Order;
import sparta.orderservice.domain.OrderItem;
import sparta.orderservice.domain.Payment;
import sparta.orderservice.domain.Shipment;
import sparta.orderservice.dto.OrderItemDto;
import sparta.orderservice.listener.ProductPriceResponseListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor()
@Log4j2
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ProductPriceResponseListener responseListener;


    // 유저 아이디로 주문 목록 조회
    @Transactional
    public List<Order> getOrdersByUserId(int userId) {

        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public List<OrderItemDto> getOrderItems(int orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream().map(orderItem -> {
            Shipment shipment = shipmentRepository.findById(orderItem.getShipmentId())
                    .orElseThrow(() -> new RuntimeException("Shipment not found"));
            return new OrderItemDto(
                    orderItem.getId(),
                    orderItem.getOrderId(),
                    orderItem.getProductId(),
                    orderItem.getShipmentId(),
                    orderItem.getQuantity(),
                    shipment.getStatus().name()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public String getShipmentStatus(int orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
        Shipment shipment = shipmentRepository.findById(orderItem.getShipmentId())
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
        return shipment.getStatus().name();
    }


    // @Transactional 어노테이션을 통해 트랜잭션을 관리하여 하나의 트랜잭션 내에서 모든 작업을 처리하도록 함
    @Transactional
    public void createOrder(int userId, List<CreateOrderDto> orderItems) {
        log.info("Starting createOrder");

        // 새로운 주문을 생성하고 데이터베이스에 저장
        Order order = createNewOrder(userId);

        // 전체 주문의 총 가격을 계산
        int totalPrice = calculateTotalPrice(order, orderItems);

        // 계산된 총 가격으로 주문의 총 가격을 업데이트
        updateOrderTotalPrice(order, totalPrice);

        // 결제 정보를 생성하고 데이터베이스에 저장
        createPayment(order.getId(), totalPrice);

        log.info("Finished createOrder");
    }

    // 새로운 주문을 생성하고 데이터베이스에 저장하는 메서드
    private Order createNewOrder(int userId) {
        log.info("Starting createNewOrder");

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(new Date());
        order.setTotalPrice(0);

        try {
            orderRepository.save(order);
            log.info("Order created with ID: " + order.getId());
        } catch (Exception e) {
            log.error("Order creation failed", e);
            throw new RuntimeException("Order creation failed", e);
        }

        log.info("Finished createNewOrder");
        return order;
    }

    // 주문 항목 목록을 기반으로 전체 주문의 총 가격을 계산하는 메서드
    private int calculateTotalPrice(Order order, List<CreateOrderDto> orderItems) {
        log.info("Starting calculateTotalPrice");

        List<CompletableFuture<Integer>> futureList = orderItems.stream()
                .map(item -> CompletableFuture.supplyAsync(() -> {
                    Shipment shipment = createShipment();
                    OrderItem orderItem = createOrderItem(order.getId(), item, shipment.getId());
                    int productPrice = requestProductPrice(item);
                    updateProductStock(item);
                    return productPrice * item.getQuantity();
                })).collect(Collectors.toList());

        int totalPrice;
        try {
            List<Integer> prices = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futureList.stream().map(CompletableFuture::join).collect(Collectors.toList()))
                    .get(30, TimeUnit.SECONDS);
            totalPrice = prices.stream().mapToInt(Integer::intValue).sum();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Failed to complete order price calculations", e);
            throw new RuntimeException("Failed to complete order price calculations", e);
        }

        log.info("Finished calculateTotalPrice");
        return totalPrice;
    }

    // 새로운 배송 정보를 생성하고 데이터베이스에 저장하는 메서드
    private Shipment createShipment() {
        log.info("Starting createShipment");

        Shipment shipment = new Shipment();
        shipment.setStatus(Shipment.ShipmentStatus.ORDERED);

        try {
            shipmentRepository.save(shipment);
            log.info("Shipment created with ID: " + shipment.getId());
        } catch (Exception e) {
            log.error("Shipment creation failed", e);
            throw new RuntimeException("Shipment creation failed", e);
        }

        log.info("Finished createShipment");
        return shipment;
    }

    // 새로운 주문 항목을 생성하고 데이터베이스에 저장하는 메서드
    private OrderItem createOrderItem(int orderId, CreateOrderDto item, int shipmentId) {
        log.info("Starting createOrderItem");

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setProductId(item.getProductId());
        orderItem.setShipmentId(shipmentId);
        orderItem.setQuantity(item.getQuantity());

        try {
            orderItemRepository.save(orderItem);
            log.info("OrderItem created with ID: " + orderItem.getId());
        } catch (Exception e) {
            log.error("OrderItem creation failed", e);
            throw new RuntimeException("OrderItem creation failed", e);
        }

        log.info("Finished createOrderItem");
        return orderItem;
    }

    // 제품의 가격을 요청하는 메서드
    private int requestProductPrice(CreateOrderDto item) {
        log.info("Starting requestProductPrice");

        // ProductPriceRequest 객체 생성
        ProductPriceRequest request = new ProductPriceRequest();
        request.setProductId(item.getProductId());

        // 메시지 큐에 요청 전송
        rabbitTemplate.convertAndSend(RabbitMQConfig.PRODUCT_PRICE_REQUEST_QUEUE, request);

        int productPrice;
        try {
            // 응답 대기
            productPrice = responseListener.getPrice(20000);
            log.info("Received product price: " + productPrice);
        } catch (InterruptedException | TimeoutException e) {
            log.error("Failed to get product price", e);
            throw new RuntimeException("Failed to get product price", e);
        }

        log.info("Finished requestProductPrice");
        return productPrice;
    }
    // 제품의 재고를 업데이트하는 메서드
    private void updateProductStock(CreateOrderDto item) {
        log.info("Starting updateProductStock");

        // 메시지 큐에 재고 업데이트 요청 전송
        rabbitTemplate.convertAndSend(RabbitMQConfig.PRODUCT_UPDATE_QUEUE, item);

        log.info("Finished updateProductStock");
    }

    // 주문의 총 가격을 업데이트하는 메서드
    private void updateOrderTotalPrice(Order order, int totalPrice) {
        log.info("Starting updateOrderTotalPrice");

        order.setTotalPrice(totalPrice);

        try {
            orderRepository.save(order);
            log.info("Order total price updated");
        } catch (Exception e) {
            log.error("Order update failed", e);
            throw new RuntimeException("Order update failed", e);
        }

        log.info("Finished updateOrderTotalPrice");
    }

    // 결제 정보를 생성하고 데이터베이스에 저장하는 메서드
    private void createPayment(int orderId, int totalPrice) {
        log.info("Starting createPayment");

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentDate(new Date());
        payment.setPrice(totalPrice);
        payment.setPaymentMethod("card");

        try {
            paymentRepository.save(payment);
            log.info("Payment created with ID: " + payment.getId());
        } catch (Exception e) {
            log.error("Payment creation failed", e);
            throw new RuntimeException("Payment creation failed", e);
        }

        log.info("Finished createPayment");
    }

    @Transactional
    public void cancelOrder(int orderId) {
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 주문 항목 조회
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        // 모든 주문 항목의 상태가 ORDERED인지 확인
        for (OrderItem orderItem : orderItems) {
            LocalDateTime orderTime = Instant.ofEpochMilli(order.getOrderDate().getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            long hoursSinceOrder = ChronoUnit.HOURS.between(orderTime, now);

            // 배송 정보 조회
            Shipment shipment = shipmentRepository.findById(orderItem.getShipmentId())
                    .orElseThrow(() -> new RuntimeException("Shipment not found"));

            if (shipment.getStatus() != Shipment.ShipmentStatus.ORDERED || hoursSinceOrder >= 24) {
                throw new RuntimeException("Cannot cancel order that is already shipped, delivered, or ordered more than 24 hours ago");
            }
        }

        // 주문 항목의 상태를 취소완료로 변경
        for (OrderItem orderItem : orderItems) {
            Shipment shipment = shipmentRepository.findById(orderItem.getShipmentId())
                    .orElseThrow(() -> new RuntimeException("Shipment not found"));

            shipment.setStatus(Shipment.ShipmentStatus.CANCELLED);
            shipmentRepository.save(shipment);

            // 재고 복구
            CreateOrderDto createOrderDto = new CreateOrderDto(orderItem.getProductId(), orderItem.getQuantity());
            productServiceClient.restoreStock(createOrderDto);
        }
    }

    // 반품하기
    @Transactional
    public void returnOrderItem(int orderItemId) {
        // 주문 항목 조회
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        // 배송 정보 조회
        Shipment shipment = shipmentRepository.findById(orderItem.getShipmentId())
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        // 반품 가능 여부 확인 (배송 완료 후 D+1일까지만 가능)
        LocalDateTime deliveredTime = Instant.ofEpochMilli(orderItem.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long daysSinceDelivered = ChronoUnit.DAYS.between(deliveredTime, now);

        if (shipment.getStatus() != Shipment.ShipmentStatus.DELIVERED || daysSinceDelivered > 1) {
            throw new RuntimeException("Cannot return order item that is not delivered or delivered more than a day ago");
        }

        // 배송 상태를 반품완료로 변경
        shipment.setStatus(Shipment.ShipmentStatus.RETURNED);

        try {
            shipmentRepository.save(shipment);
        } catch (Exception e) {
//            logger.error("Error saving dailyVideoViews for videoId: " + shipment, e);
            throw e;

        }

        // 재고 원복
        CreateOrderDto createOrderDto = new CreateOrderDto(orderItem.getProductId(), orderItem.getQuantity());
        productServiceClient.restoreStock(createOrderDto);

        // 결제 정보 업데이트
        Payment payment = paymentRepository.findByOrderId(orderItem.getOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        payment.setDltYsno("Y");
        paymentRepository.save(payment);
    }

}

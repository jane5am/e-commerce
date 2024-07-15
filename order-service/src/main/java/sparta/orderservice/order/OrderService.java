package sparta.orderservice.order;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparta.orderservice.client.ProductServiceClient;
import sparta.orderservice.domain.Order;
import sparta.orderservice.domain.OrderItem;
import sparta.orderservice.domain.Payment;
import sparta.orderservice.domain.Shipment;
import sparta.orderservice.dto.OrderItemDto;
import sparta.orderservice.dto.CreateOrderDto;

import java.beans.Transient;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor()
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

    @Transactional
    public void createOrder(int userId, List<CreateOrderDto> orderItems) {
        // 주문 생성
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalPrice(0000);
        order.setOrderDate(new Date());
        order.setTotalPrice(orderItems.stream().mapToInt(CreateOrderDto::getQuantity).sum());
        try {
            orderRepository.save(order);
        } catch (Exception e) {
            throw e;
        }

        // 결제 금액 계산
        int totalPrice = 0;

        for (CreateOrderDto item : orderItems) {
            // 배송 정보 생성
            Shipment shipment = new Shipment();
            shipment.setStatus(Shipment.ShipmentStatus.ORDERED);
            try {
                shipmentRepository.save(shipment);
            } catch (Exception e) {
                throw e;
            }

            // 주문 항목 생성
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setShipmentId(shipment.getId());
            orderItem.setQuantity(item.getQuantity());
            try {
                orderItemRepository.save(orderItem);
            } catch (Exception e) {
                throw e;
            }


            // 재고 업데이트
            productServiceClient.updateStock(item);

            // 각 상품의 가격을 가져와 총 가격 계산
            int productPrice = productServiceClient.getProductPrice(item.getProductId());
            totalPrice += productPrice * item.getQuantity();
        }

        // 결제 정보 생성
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setPaymentDate(new Date());
        payment.setPrice(totalPrice); // 총 가격
        payment.setPaymentMethod("card");
        try {
            paymentRepository.save(payment);
        } catch (Exception e) {
            throw e;
        }
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

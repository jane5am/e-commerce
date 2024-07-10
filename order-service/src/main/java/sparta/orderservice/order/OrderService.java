package sparta.orderservice.order;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparta.orderservice.client.ProductServiceClient;
import sparta.orderservice.domain.Order;
import sparta.orderservice.domain.OrderItem;
import sparta.orderservice.domain.Payment;
import sparta.orderservice.domain.Shipment;
import sparta.orderservice.dto.OrderItemDto;
import sparta.orderservice.dto.ProductDto;

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
    private ProductDto productDto;

    // 유저 아이디로 주문 목록 조회
    public List<Order> getOrdersByUserId(int userId) {

        return orderRepository.findByUserId(userId);
    }

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

    public String getShipmentStatus(int orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
        Shipment shipment = shipmentRepository.findById(orderItem.getShipmentId())
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
        return shipment.getStatus().name();
    }


    public void createOrder(int userId, int productId, int quantity) {

        // 주문 생성
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(new Date());
        order.setTotalAmount(quantity);
        orderRepository.save(order);

        // 배송 정보 생성
        Shipment shipment = new Shipment();
        shipment.setStatus(Shipment.ShipmentStatus.ORDERED);
        shipmentRepository.save(shipment);

        // 주문 항목 생성
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(productId);
        orderItem.setShipmentId(shipment.getId());
        orderItem.setQuantity(quantity);
        orderItemRepository.save(orderItem);

        // 재고 업데이트
        ProductDto productDto = new ProductDto(productId, quantity);
        productServiceClient.updateStock(productDto);

        // 결제 정보 생성
        int productPrice = productServiceClient.getProductPrice(productId);

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setPaymentDate(new Date());
        payment.setPrice(quantity * productPrice); // 상품 가격 * 수량
        payment.setPaymentMethod("card");
        paymentRepository.save(payment);
    }

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
            ProductDto productDto = new ProductDto(orderItem.getProductId(), orderItem.getQuantity());
            productServiceClient.restoreStock(productDto);
        }
    }

}

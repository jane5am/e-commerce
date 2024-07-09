package sparta.orderservice.order;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparta.orderservice.client.ProductServiceClient;
import sparta.orderservice.domain.Order;
import sparta.orderservice.domain.OrderItem;
import sparta.orderservice.domain.Payment;
import sparta.orderservice.domain.Shipment;
import sparta.orderservice.dto.OrderItemDTO;
import sparta.orderservice.dto.UpdateStockRequest;

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
    public List<Order> getOrdersByUserId(int userId) {

        return orderRepository.findByUserId(userId);
    }

    public List<OrderItemDTO> getOrderItems(int orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream().map(orderItem -> {
            Shipment shipment = shipmentRepository.findById(orderItem.getShipmentId())
                    .orElseThrow(() -> new RuntimeException("Shipment not found"));
            return new OrderItemDTO(
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


    public void placeOrder(int userId, int productId, int quantity) {
        System.out.println("userId : " + userId);
        System.out.println("productId : " + productId);
        System.out.println("quantity : " + quantity);
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

        System.out.println("여기!");
        // 재고 업데이트
        UpdateStockRequest updateStockRequest = new UpdateStockRequest(productId, quantity);
        productServiceClient.updateStock(updateStockRequest);

        System.out.println("여기!22222");

        // 결제 정보 생성
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setPaymentDate(new Date());
        payment.setPrice(quantity * getProductPrice(productId)); // 상품 가격 * 수량
        payment.setPaymentMethod("card");
        paymentRepository.save(payment);
    }

    private int getProductPrice(int productId) {
        // 여기서는 상품의 가격을 가져오는 로직을 간단히 구현합니다.
        // 실제로는 ProductService를 통해 가격을 가져와야 합니다.
        // ProductServiceClient에서 상품의 가격을 가져오는 메서드를 추가할 수 있습니다.
        // 이 예제에서는 임시로 1000을 반환합니다.
        return 1000;
    }

}

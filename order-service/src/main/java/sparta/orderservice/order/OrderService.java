package sparta.orderservice.order;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparta.orderservice.domain.Order;
import sparta.orderservice.domain.OrderItem;
import sparta.orderservice.domain.Shipment;
import sparta.orderservice.dto.OrderItemDTO;

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


}

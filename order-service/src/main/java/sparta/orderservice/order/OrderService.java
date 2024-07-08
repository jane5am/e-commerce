package sparta.orderservice.order;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparta.orderservice.domain.Order;

import java.util.List;

@Service
@RequiredArgsConstructor()
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // 유저 아이디로 주문 목록 조회
    public List<Order> getOrdersByUserId(int userId) {
        return orderRepository.findByUserId(userId);
    }


    // 취소하기
    public Order cancelOrder(int orderId) throws Exception {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new Exception("Order not found"));

        if (order.getStatus() == Order.OrderStatus.SHIPPED) {
            throw new Exception("Cannot cancel order, it is already shipped");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        // restore stock logic here

        return orderRepository.save(order);
    }


}

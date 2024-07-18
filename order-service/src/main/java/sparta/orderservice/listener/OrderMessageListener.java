package sparta.orderservice.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparta.orderservice.message.OrderMessage;
import sparta.orderservice.order.OrderService;

@Component
public class OrderMessageListener {

    @Autowired
    private OrderService orderService;

    public void receiveOrderMessage(OrderMessage orderMessage) {
        try {
            orderService.createOrder(orderMessage.getUserId(), orderMessage.getOrderItems());
        } catch (Exception e) {
            // 예외 로그 출력
            System.err.println("Failed to create order: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
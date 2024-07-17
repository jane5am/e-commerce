package sparta.orderservice.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparta.orderservice.message.OrderMessage;
import sparta.orderservice.order.OrderService;

@Service
public class OrderMessageListener {

    @Autowired
    private OrderService orderService;

    public void receiveMessage(OrderMessage orderMessage) {
        System.out.println("rabbitMQ Listener");
        orderService.createOrder(orderMessage.getUserId(), orderMessage.getOrderItems());
    }
}
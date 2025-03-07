package sparta.orderservice.order;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sparta.common.messages.CreateOrderDto;
import sparta.orderservice.ResponseMessage;
import sparta.orderservice.config.RabbitMQConfig;
import sparta.orderservice.domain.Order;
import sparta.orderservice.dto.OrderItemDto;
import sparta.orderservice.message.OrderMessage;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // userId로 주문목록 조회
    @GetMapping("/getOrderList")
    public List<Order> getUserOrders(HttpServletRequest request) {
        int userId = extractUserIdFromRequest(request);
        return orderService.getOrdersByUserId(userId);
    }

    private int extractUserIdFromRequest(HttpServletRequest request) {
        String userIdHeader = request.getHeader("x-claim-userid");
        if (userIdHeader == null) {
            throw new RuntimeException("Missing user ID in headers");
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdHeader);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format in headers");
        }

        return userId;
    }

    // orderId로 상태 조회
    @GetMapping("/items/{orderId}")
    public List<OrderItemDto> getOrderItems(@PathVariable("orderId") int orderId) {
        return orderService.getOrderItems(orderId);
    }

    // orderItemId로 상태 조회
    @GetMapping("/status/{orderItemId}")
    public String getShipmentStatus(@PathVariable("orderItemId") int orderItemId) {
        return orderService.getShipmentStatus(orderItemId);
    }


    // 주문 하기 ( 메시지 큐 적용 안된 코드 )
//    @PostMapping("/createOrder")
//    public ResponseEntity<ResponseMessage> createOrder(HttpServletRequest request, @RequestBody List<CreateOrderDto> orderItems) {
//        int userId = extractUserIdFromRequest(request);
//        orderService.createOrder(userId, orderItems);
//
//        ResponseMessage response = ResponseMessage.builder()
//                .data(orderItems)
//                .statusCode(200)
//                .resultMessage("Success")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/createOrder")
    public ResponseEntity<ResponseMessage> createOrder(HttpServletRequest request, @RequestBody List<CreateOrderDto> orderItems) {
        int userId = extractUserIdFromRequest(request);

        OrderMessage orderMessage = new OrderMessage(userId, orderItems);
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_QUEUE, orderMessage);

        ResponseMessage response = ResponseMessage.builder()
                .data(orderItems)
                .statusCode(200)
                .resultMessage("Success")
                .build();

        return ResponseEntity.ok(response);
    }



    // 주문 취소
    @GetMapping("/cancelOrder/{orderId}")
    public ResponseEntity<ResponseMessage> cancelOrder(@PathVariable("orderId") int orderId) {
        orderService.cancelOrder(orderId);

        ResponseMessage response = ResponseMessage.builder()
                .data(orderId)
                .statusCode(200)
                .resultMessage("Order cancelled successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    // 주문 아이템 반품
    @PostMapping("/returnOrderItem/{orderItemId}")
    public ResponseEntity<ResponseMessage> returnOrderItem(@PathVariable("orderItemId") int orderItemId) {

        System.out.println("orderItemId : " + orderItemId);
        orderService.returnOrderItem(orderItemId);

        ResponseMessage response = ResponseMessage.builder()
                .data("")
                .statusCode(200)
                .resultMessage("Order item returned successfully")
                .build();

        return ResponseEntity.ok(response);
    }

}

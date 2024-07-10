package sparta.orderservice.order;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sparta.orderservice.ResponseMessage;
import sparta.orderservice.domain.Order;
import sparta.orderservice.dto.OrderItemDto;
import sparta.orderservice.dto.ProductDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;


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


    // 주문 하기
    @PostMapping("/createOrder")
    public ResponseEntity<ResponseMessage> createOrder(HttpServletRequest request, @RequestBody ProductDto productDto) {
        int userId = extractUserIdFromRequest(request);
        orderService.createOrder(userId, productDto.getProductId(), productDto.getQuantity());

        ResponseMessage response = ResponseMessage.builder()
                .data("")
                .statusCode(200)
                .resultMessage("Success")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cancelOrder/{orderId}")
    public ResponseEntity<ResponseMessage> cancelOrder(@PathVariable("orderId") int orderId) {
        orderService.cancelOrder(orderId);

        ResponseMessage response = ResponseMessage.builder()
                .data("")
                .statusCode(200)
                .resultMessage("Order cancelled successfully")
                .build();

        return ResponseEntity.ok(response);
    }

}

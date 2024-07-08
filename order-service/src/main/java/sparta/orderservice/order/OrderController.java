package sparta.orderservice.order;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sparta.orderservice.domain.Order;
import sparta.orderservice.dto.OrderItemDTO;

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

    // orderId로 상태 조회
    @GetMapping("/items/{orderId}")
    public List<OrderItemDTO> getOrderItems(@PathVariable("orderId") int orderId) {
        return orderService.getOrderItems(orderId);
    }

    // orderItemId로 상태 조회
    @GetMapping("/status/{orderItemId}")
    public String getShipmentStatus(@PathVariable("orderItemId") int orderItemId) {
        return orderService.getShipmentStatus(orderItemId);
    }


//24시전 && 주문완료인 상태일때
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
}

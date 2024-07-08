package sparta.orderservice.order;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sparta.orderservice.domain.Order;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 유저 아이디로 주문 목록 조회
    @GetMapping("/getOrderList")
    public List<Order> getUserOrders( HttpServletRequest request) {
        String userIdHeader = request.getHeader("x-claim-userid");
        System.out.println("userIdHeader = " + userIdHeader);
        if (userIdHeader == null) {
            throw new RuntimeException("Missing user ID in headers");
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdHeader);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format in headers");
        }

        return orderService.getOrdersByUserId(userId);
    }


    // 취소하기
    @PostMapping("/cancel")
    public Order cancelOrder( HttpServletRequest request) {

        String userIdHeader = request.getHeader("x-claim-userid");
        System.out.println("userIdHeader = " + userIdHeader);

        if (userIdHeader == null) {
            throw new RuntimeException("Missing user ID in headers");
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdHeader);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format in headers");
        }

        try {
            return orderService.cancelOrder(userId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}

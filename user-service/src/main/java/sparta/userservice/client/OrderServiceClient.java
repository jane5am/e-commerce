package sparta.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sparta.userservice.dto.order.OrderDto;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/api/v1/order")
    List<OrderDto> getOrdersByUserId(@RequestParam("userId") int userId);
}

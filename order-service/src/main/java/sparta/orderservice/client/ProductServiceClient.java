package sparta.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sparta.common.messages.CreateOrderDto;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    // rabbitMQ로 대체
    // @PutMapping("/api/v1/product/updateStock")
    // void updateStock(@RequestBody CreateOrderDto createOrderDto);

    @PutMapping("/api/v1/product/restoreStock")
    void restoreStock(@RequestBody CreateOrderDto createOrderDto);

    // rabbitMQ로 대체
    // @GetMapping("/api/v1/product/getPrice")
    // int getProductPrice(@RequestParam("productId") int productId);
}

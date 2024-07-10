package sparta.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sparta.orderservice.dto.CreateOrderDto;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @PutMapping("/api/v1/product/updateStock")
    void updateStock(@RequestBody CreateOrderDto createOrderDto);

    @PutMapping("/api/v1/product/restoreStock")
    void restoreStock(@RequestBody CreateOrderDto createOrderDto);

    @GetMapping("/api/v1/product/getPrice")
    int getProductPrice(@RequestParam("productId") int productId);
}

package sparta.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sparta.orderservice.dto.ProductDto;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @PutMapping("/api/v1/product/update-stock")
    void updateStock(@RequestBody ProductDto productDto);

    @GetMapping("/api/v1/product/getPrice")
    int getProductPrice(@RequestParam("productId") int productId);
}

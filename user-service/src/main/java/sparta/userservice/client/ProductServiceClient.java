package sparta.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sparta.userservice.dto.product.ProductDto;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/api/v1/product")
    List<ProductDto> getProductsByIds(@RequestParam("ids") List<Integer> productIds);
}

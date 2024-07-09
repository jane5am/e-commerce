package sparta.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sparta.orderservice.dto.UpdateStockRequest;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @PutMapping("/api/v1/product/update-stock")
    void updateStock(@RequestBody UpdateStockRequest updateStockRequest);

}

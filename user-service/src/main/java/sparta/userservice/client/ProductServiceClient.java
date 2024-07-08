package sparta.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sparta.userservice.dto.product.ProductDto;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    /**
     * 특정 상품 ID 목록으로 상품 정보 조회
     *
     * @param productIds 상품 ID 목록
     * @return 상품 정보 목록(ProductDto(마이크로 서비스 간 사용하는 DTO))
     */
    @GetMapping("/api/v1/product")
    List<ProductDto> getProductsByIds(@RequestParam("ids") List<Integer> productIds);
}

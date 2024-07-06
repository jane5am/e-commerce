package sparta.productservice.product;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sparta.productservice.domain.Product;
import sparta.productservice.dto.ResponseMessage;
import sparta.productservice.dto.product.ProductDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final Environment env;

    @GetMapping("/health-check")
    public String status() {
        return String.format("It's Working in Product Service in PORT %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/product")
    public ResponseEntity<ResponseMessage> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        ResponseMessage response = ResponseMessage.builder()
                .data(products)
                .statusCode(200)
                .resultMessage("Success")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProductsByIds(@RequestParam("ids") List<Integer> productIds) {
        System.out.println("productIds = " + productIds);
        List<Product> products = productService.getProductsByIds(productIds);
        List<ProductDto> productDtos = products.stream()
                .map(product -> new ProductDto(product.getProductId(), product.getName(), product.getPrice(), product.getDescription(), product.getExposeYsno()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }
}

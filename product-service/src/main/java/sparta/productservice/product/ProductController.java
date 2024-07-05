package sparta.productservice.product;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparta.productservice.domain.Product;
import sparta.productservice.dto.ResponseMessage;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    private Environment env; //application.yml에 있는 환경변수 사용할 때

    @GetMapping("/health-check")
    public String status() {
        return String.format("It's Working in Product Service in PORT %s",
                env.getProperty("local.server.port"));
    }

    @GetMapping(value="/product")
    public ResponseEntity<ResponseMessage> getAllProducts(){

        List<Product> prodects = productService.getAllProducts();
        ResponseMessage response = ResponseMessage.builder()
                .data(prodects)
                .statusCode(200)
                .resultMessage("Success")
                .build();

        return ResponseEntity.ok(response);
    }
}

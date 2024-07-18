package sparta.productservice.product;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sparta.common.messages.CreateOrderDto;
import sparta.productservice.domain.Product;
import sparta.productservice.dto.ResponseMessage;
import sparta.productservice.dto.product.CreateProductDto;
import sparta.productservice.dto.product.ProductDto;
import sparta.productservice.dto.product.PutProductDto;

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

  // 모든 상품 조회
    @GetMapping("/products")
    public ResponseEntity<ResponseMessage> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        ResponseMessage response = ResponseMessage.builder()
                .data(products)
                .statusCode(200)
                .resultMessage("Success")
                .build();
        return ResponseEntity.ok(response);
    }


    // 제품 생성
    @PostMapping
    public ResponseEntity<ResponseMessage> createProduct(@RequestBody CreateProductDto createProductDto) {
        Product product = productService.createProduct(createProductDto);
        ResponseMessage response = ResponseMessage.builder()
                .data(product)
                .statusCode(201)
                .resultMessage("Product created successfully")
                .build();
        return ResponseEntity.status(201).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage> updateProduct(@PathVariable("id") int id, @RequestBody PutProductDto putProductDto) {
        Product updatedProduct = productService.updateProduct(id, putProductDto);
        ResponseMessage response = ResponseMessage.builder()
                .data(updatedProduct)
                .statusCode(200)
                .resultMessage("Product updated successfully")
                .build();
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteProduct(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        ResponseMessage response = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("Product deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }


    /**
     * 특정 상품 ID 목록으로 상품 정보 조회
     *
     * @param productIds 상품 ID 목록
     * @return 상품 정보 목록
     */
    @GetMapping
    public ResponseEntity<List<ProductDto>> getProductsByIds(@RequestParam("ids") List<Integer> productIds) {
        // ProductService를 통해 상품 정보 조회
        List<Product> products = productService.getProductsByIds(productIds);
        // Product 엔티티를 ProductDto(마이크로 서비스 간 사용하는 DTO)로 변환
        List<ProductDto> productDtos = products.stream()
                .map(product -> new ProductDto(product.getProductId(), product.getName(), product.getPrice(), product.getDescription(), product.getExposeYsno()))
                .collect(Collectors.toList());
        // userservice에 응답
        return ResponseEntity.ok(productDtos);
    }


    // Order-Service : 재고 업데이트
    // rabbitMQ로 대체
//    @PutMapping("/updateStock")
//    public void updateStock(@RequestBody CreateOrderDto createOrderDto) {
//        productService.updateStock(createOrderDto);
//    }


    // Order-Service : 재고 되돌리기
    @PutMapping("/restoreStock")
    public void restoreStock(@RequestBody CreateOrderDto createOrderDto) {
        productService.restoreStock(createOrderDto);
    }


    // Order-Service : 가격 가져오기
    // rabbitMQ로 대체
//    @GetMapping("/getPrice")
//    public int getProductPrice(@RequestParam("productId") int productId) {
//        return productService.getProductPrice(productId);
//    }

}

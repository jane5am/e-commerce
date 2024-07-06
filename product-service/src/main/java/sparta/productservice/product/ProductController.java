package sparta.productservice.product;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProductsByIds(@RequestParam("ids") List<Integer> productIds) {
        List<Product> products = productService.getProductsByIds(productIds);
        List<ProductDto> productDtos = products.stream()
                .map(product -> new ProductDto(product.getProductId(), product.getName(), product.getPrice(), product.getDescription(), product.getExposeYsno()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> createProduct(@RequestBody ProductDto productDto) {
        Product product = productService.createProduct(productDto);
        ResponseMessage response = ResponseMessage.builder()
                .data(product)
                .statusCode(201)
                .resultMessage("Product created successfully")
                .build();
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage> updateProduct(@PathVariable("id") int id, @RequestBody ProductDto productDto) {
        Product updatedProduct = productService.updateProduct(id, productDto);
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
}

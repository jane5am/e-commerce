package sparta.productservice.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.orderservice.dto.ProductDto;
import sparta.productservice.domain.Product;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor()
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public void updateStock(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        int newStock = product.getQuantity() - productDto.getQuantity();
        if (newStock < 0) {
            throw new RuntimeException("Not enough stock available");
        }
        product.setQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    public int getProductPrice(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getPrice();
    }

}

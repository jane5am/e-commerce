package sparta.productservice.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.orderservice.domain.OrderItem;
import sparta.orderservice.dto.UpdateStockRequest;
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

    public void updateStock(UpdateStockRequest updateStockRequest) {
        Product product = productRepository.findById(updateStockRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        int newStock = product.getQuantity() - updateStockRequest.getQuantity();
        if (newStock < 0) {
            throw new RuntimeException("Not enough stock available");
        }
        product.setQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }
}

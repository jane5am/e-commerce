package sparta.productservice.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.orderservice.dto.ProductDto;
import sparta.productservice.domain.Product;
import sparta.productservice.dto.product.ProductDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
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

    public void restoreStock(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        int newStock = product.getQuantity() + productDto.getQuantity();
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

    public List<Product> getProductsByIds(List<Integer> productIds) {
        return productRepository.findAllById(productIds);
    }

    public Product createProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setExposeYsno(productDto.getExposeYsno());
        return productRepository.save(product);
    }

    public Product updateProduct(int id, ProductDto productDto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setExposeYsno(productDto.getExposeYsno());
        return productRepository.save(product);
    }

    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }
}

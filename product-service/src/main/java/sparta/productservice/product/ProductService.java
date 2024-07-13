package sparta.productservice.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.productservice.domain.Product;
import sparta.productservice.dto.product.CreateProductDto;
import sparta.productservice.dto.product.ProductDto;
import sparta.orderservice.dto.CreateOrderDto;
import sparta.productservice.dto.product.PutProductDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public void updateStock(CreateOrderDto createOrderDto) {
        Product product = productRepository.findById(createOrderDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        int newStock = product.getQuantity() - createOrderDto.getQuantity();
        if (newStock < 0) {
            throw new RuntimeException("Not enough stock available");
        }
        product.setQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    public void restoreStock(CreateOrderDto createOrderDto) {
        Product product = productRepository.findById(createOrderDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        int newStock = product.getQuantity() + createOrderDto.getQuantity();
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

    public Product createProduct(CreateProductDto createProductDto) {
        Product product = new Product();
        product.setName(createProductDto.getName());
        product.setPrice(createProductDto.getPrice());
        product.setQuantity(createProductDto.getQuantity());
        product.setDescription(createProductDto.getDescription());
        return productRepository.save(product);
    }

    public Product updateProduct(int id, PutProductDto putProductDto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(putProductDto.getName());
        product.setPrice(putProductDto.getPrice());
        product.setDescription(putProductDto.getDescription());
        product.setExposeYsno(putProductDto.getExposeYsno());
        return productRepository.save(product);
    }

    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }
}

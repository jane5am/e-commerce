package sparta.productservice.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.productservice.domain.Product;

import java.util.List;

@Service
@RequiredArgsConstructor()
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
}

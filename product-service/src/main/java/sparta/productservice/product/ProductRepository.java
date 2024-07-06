package sparta.productservice.product;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.productservice.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
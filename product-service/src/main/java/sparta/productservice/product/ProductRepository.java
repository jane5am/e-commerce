package sparta.productservice.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sparta.productservice.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByProductId(int productId);
}

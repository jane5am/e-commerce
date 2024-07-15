package sparta.userservice.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.userservice.domain.Cart;
import sparta.userservice.domain.WishList;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByUserId(int userId);

    Optional<Cart> findByUserIdAndProductId(int userId, int productId);
}

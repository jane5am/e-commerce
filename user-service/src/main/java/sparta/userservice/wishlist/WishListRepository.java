package sparta.userservice.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.userservice.domain.WishList;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Integer> {
    List<WishList> findByUserId(int userId);

    Optional<WishList> findByUserIdAndProductId(int userId, int productId);
}

package sparta.userservice.wishlist;

import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.userservice.domain.WishList;
import sparta.userservice.dto.wishlist.CreateWishListRequestDto;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor()
public class WishListService {


    private final WishListRepository wishListRepository;

    public List<WishList> getUserWishList(int userId) {
        return wishListRepository.findByUserId(userId);
    }

    public WishList addToWishList(int userId, CreateWishListRequestDto createWishListRequestDto) {
        try {
            Optional<WishList> existingWishList = wishListRepository.findByUserIdAndProductId(userId, Integer.parseInt(createWishListRequestDto.getProductId()));

            WishList wishList;
            if (existingWishList.isPresent()) {
                wishList = existingWishList.get();
                wishList.setQuantity(wishList.getQuantity() + createWishListRequestDto.getQuantity());
            } else {
                wishList = new WishList();
                wishList.setUserId(userId);
                wishList.setProductId(Integer.parseInt(createWishListRequestDto.getProductId()));
                wishList.setQuantity(createWishListRequestDto.getQuantity());
            }

            return wishListRepository.save(wishList);
        } catch (Exception e) {
            // 예외 발생 시 로그 기록 및 BadRequestException 발생
            throw new RuntimeException("An error occurred while adding to the wish list: " + e.getMessage());
        }
    }

    public WishList updateWishList(WishList wishList) {
        return wishListRepository.save(wishList);
    }

    public void deleteWishListItem(int id) {
        wishListRepository.deleteById(id);
    }
}

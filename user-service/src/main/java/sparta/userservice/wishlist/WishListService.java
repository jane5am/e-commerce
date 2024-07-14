package sparta.userservice.wishlist;

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

    /**
     * 특정 사용자 ID로 WishList 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 WishList 목록
     */
    public List<WishList> getUserWishList(int userId) {
        return wishListRepository.findByUserId(userId);
    }

    // 위시리스트 생성
    public WishList addToWishList(int userId, CreateWishListRequestDto createWishListRequestDto) {
        try {
            Optional<WishList> existingWishList = wishListRepository.findByUserIdAndProductId(userId, createWishListRequestDto.getProductId());

            WishList wishList;
            if (existingWishList.isPresent()) {
                wishList = existingWishList.get();
                wishList.setQuantity(wishList.getQuantity() + createWishListRequestDto.getQuantity());
            } else {
                wishList = new WishList();
                wishList.setUserId(userId);
                wishList.setProductId(createWishListRequestDto.getProductId());
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

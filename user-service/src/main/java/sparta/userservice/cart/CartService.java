package sparta.userservice.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sparta.userservice.client.ProductServiceClient;
import sparta.userservice.domain.Cart;
import sparta.userservice.dto.cart.CreateCartRequestDto;
import sparta.userservice.dto.product.ProductDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor()
public class CartService {


    private final CartRepository cartRepository;

    private final ProductServiceClient productServiceClient;
    /**
     * 특정 사용자 ID로 WishList 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 WishList 목록
     */
    public List<ProductDto> getUserCart(int userId) {
        List<Cart> cart = cartRepository.findByUserId(userId);
        List<Integer> productIds = cart.stream().map(Cart::getProductId).collect(Collectors.toList());
        List<ProductDto> products = productServiceClient.getProductsByIds(productIds);

        // 각 상품에 수량 추가
        for (ProductDto product : products) {
            for (Cart cartItem : cart) {
                if (product.getProductId() == cartItem.getProductId()) {
                    product.setQuantity(cartItem.getQuantity());
                    break;
                }
            }
        }

        return products;
    }

    // 장바구니 생성
    public Cart addToCart(int userId, CreateCartRequestDto createCartRequestDto) {
        try {
            Optional<Cart> existingCart = cartRepository.findByUserIdAndProductId(userId, createCartRequestDto.getProductId());

            Cart cart;
            if (existingCart.isPresent()) {
                cart = existingCart.get();
                cart.setQuantity(cart.getQuantity() + createCartRequestDto.getQuantity());
            } else {
                cart = new Cart();
                cart.setUserId(userId);
                cart.setProductId(createCartRequestDto.getProductId());
                cart.setQuantity(createCartRequestDto.getQuantity());
            }

            return cartRepository.save(cart);
        } catch (Exception e) {
            // 예외 발생 시 로그 기록 및 BadRequestException 발생
            throw new RuntimeException("An error occurred while adding to the wish list: " + e.getMessage());
        }
    }

//    public WishList updateWishList(WishList wishList) {
//        return cartRepository.save(wishList);
//    }
//
//    public void deleteWishListItem(int id) {
//        cartRepository.deleteById(id);
//    }
}

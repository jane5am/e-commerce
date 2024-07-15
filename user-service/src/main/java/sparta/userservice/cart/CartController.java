package sparta.userservice.cart;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sparta.userservice.client.ProductServiceClient;
import sparta.userservice.domain.Cart;
import sparta.userservice.dto.ResponseMessage;
import sparta.userservice.dto.cart.CreateCartRequestDto;
import sparta.userservice.dto.product.ProductDto;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductServiceClient productServiceClient;

    /**
     * 사용자별로 WishList에 있는 상품 상태 조회
     * 클라이언트에서 요청한 사용자 ID로 WishList를 조회하고,
     * 해당 WishList에 포함된 상품 정보를 productservice를 통해 조회하여 응답합니다.
     *
     * @param headers 클라이언트 요청 헤더 (x-claim-userid: 사용자 ID)
     * @return 사용자 WishList에 포함된 상품 정보
     * @throws BadRequestException 사용자 ID가 요청 헤더에 없는 경우 발생
     */

    // 장바구니에 제품 추가
    @PostMapping
    public ResponseEntity<ResponseMessage> addToCart(@RequestHeader Map<String, String> headers, @RequestBody CreateCartRequestDto createCartRequestDto) throws BadRequestException {
        String userIdStr = headers.get("x-claim-userid");
        if (userIdStr == null) {
            throw new BadRequestException("Missing userId in headers");
        }

        int userId = Integer.parseInt(userIdStr);
        Cart createdCart = cartService.addToCart(userId, createCartRequestDto);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdCart)
                .statusCode(201)
                .resultMessage("Item added to Cart successfully")
                .build();

        return ResponseEntity.status(201).body(response);
    }

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<ResponseMessage> getUserCart(@RequestHeader Map<String, String> headers) throws BadRequestException {
        // 헤더에서 userId 추출
        String userIdStr = headers.get("x-claim-userid");
        if (userIdStr == null) {
            throw new BadRequestException("Missing userId in headers");
        }

        int userId = Integer.parseInt(userIdStr);

        // 사용자별 WishList 조회
        List<ProductDto> products = cartService.getUserCart(userId);

        // 응답 메시지 구성
        ResponseMessage response = ResponseMessage.builder()
                .data(products)
                .statusCode(200)
                .resultMessage("cart retrieved successfully")
                .build();

        return ResponseEntity.ok(response);
    }

//    // WishList 항목 수정
//    @PutMapping("/{id}")
//    public ResponseEntity<ResponseMessage> updateWishList(@PathVariable int id, @RequestBody WishList wishList) {
//        wishList.setId(id);
//        WishList updatedWishList = cartService.updateWishList(wishList);
//
//        ResponseMessage response = ResponseMessage.builder()
//                .data(updatedWishList)
//                .statusCode(200)
//                .resultMessage("WishList item updated successfully")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    // WishList 항목 삭제
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ResponseMessage> deleteWishListItem(@PathVariable int id) {
//        cartService.deleteWishListItem(id);
//
//        ResponseMessage response = ResponseMessage.builder()
//                .statusCode(200)
//                .resultMessage("WishList item deleted successfully")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
}

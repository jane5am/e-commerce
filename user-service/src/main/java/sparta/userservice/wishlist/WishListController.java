package sparta.userservice.wishlist;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sparta.userservice.client.ProductServiceClient;
import sparta.userservice.domain.WishList;
import sparta.userservice.dto.ResponseMessage;
import sparta.userservice.dto.product.ProductDto;
import sparta.userservice.dto.wishlist.CreateWishListRequestDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/wishList")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;
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
    @GetMapping
    public ResponseEntity<ResponseMessage> getUserWishList(@RequestHeader Map<String, String> headers) throws BadRequestException {
        // 헤더에서 userId 추출
        String userIdStr = headers.get("x-claim-userid");
        if (userIdStr == null) {
            throw new BadRequestException("Missing userId in headers");
        }

        int userId = Integer.parseInt(userIdStr);

        // 사용자별 WishList 조회
        List<WishList> wishList = wishListService.getUserWishList(userId);
        // WishList에서 상품 ID 목록 추출
        List<Integer> productIds = wishList.stream().map(WishList::getProductId).collect(Collectors.toList());
        // ProductServiceClient를 통해 상품 정보 조회
        List<ProductDto> products = productServiceClient.getProductsByIds(productIds);

        // 응답 메시지 구성
        ResponseMessage response = ResponseMessage.builder()
                .data(products)
                .statusCode(200)
                .resultMessage("WishList retrieved successfully")
                .build();

        return ResponseEntity.ok(response);
    }


    // WishList에 항목 추가
    @PostMapping
    public ResponseEntity<ResponseMessage> addToWishList(@RequestHeader Map<String, String> headers, @RequestBody CreateWishListRequestDto createWishListRequestDto) throws BadRequestException {
        String userIdStr = headers.get("x-claim-userid");
        if (userIdStr == null) {
            throw new BadRequestException("Missing userId in headers");
        }

        int userId = Integer.parseInt(userIdStr);
        WishList createdWishList = wishListService.addToWishList(userId, createWishListRequestDto);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdWishList)
                .statusCode(201)
                .resultMessage("Item added to WishList successfully")
                .build();

        return ResponseEntity.status(201).body(response);
    }

    // WishList 항목 수정
    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage> updateWishList(@PathVariable int id, @RequestBody WishList wishList) {
        wishList.setId(id);
        WishList updatedWishList = wishListService.updateWishList(wishList);

        ResponseMessage response = ResponseMessage.builder()
                .data(updatedWishList)
                .statusCode(200)
                .resultMessage("WishList item updated successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    // WishList 항목 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteWishListItem(@PathVariable int id) {
        wishListService.deleteWishListItem(id);

        ResponseMessage response = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("WishList item deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}

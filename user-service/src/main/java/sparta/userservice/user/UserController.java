package sparta.userservice.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sparta.userservice.client.ProductServiceClient;
import sparta.userservice.client.OrderServiceClient;
import sparta.userservice.domain.User;
import sparta.userservice.domain.WishList;
import sparta.userservice.dto.ResponseMessage;
import sparta.userservice.dto.order.OrderDto;
import sparta.userservice.dto.user.*;
import sparta.userservice.provider.jwt.JwtBlacklist;
import sparta.userservice.security.UserDetailsImpl;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtBlacklist jwtBlacklist;
    private final ProductServiceClient productServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final EmailService emailService;
    private Environment env;

    @GetMapping("/health-check")
    public String status() {
        return String.format("It's Working in User Service in PORT %s",
                env.getProperty("local.server.port"));
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> createUser(@RequestBody CreateUserRequestDto createUserRequestDto) throws BadRequestException {
        User createdUser = userService.createUser(createUserRequestDto);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdUser)
                .statusCode(201)
                .resultMessage("User created successfully")
                .build();

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> login(@RequestBody UserCommonDto userCommonDto, HttpServletResponse response) throws BadRequestException {
        String token = userService.login(userCommonDto);

        // 쿠키 생성
        String base64Token = Base64.getEncoder().encodeToString(("Bearer " + token).getBytes());
        Cookie cookie = new Cookie("Authorization", base64Token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS를 사용하는 경우에만 설정
        cookie.setPath("/");
        cookie.setMaxAge(3600); // 쿠키 유효 시간 설정 (1시간)

        // 응답에 쿠키 추가
        response.addCookie(cookie);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(token) // 토큰 다시 준거
                .statusCode(200)
                .resultMessage("Login successful")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    // 인증 이메일 전송
    @PostMapping("/send-certification")
    public ResponseEntity<ResponseMessage> sendCertificationEmail(@RequestBody SendEmailRequestDto sendEmailRequestDto) {
        emailService.sendCertificationEmail(sendEmailRequestDto);

        ResponseMessage response = ResponseMessage.builder()
                .data("")
                .statusCode(200)
                .resultMessage("Email sent successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    // 이메일 인증
    @PostMapping("/email-certification")
    public ResponseEntity<ResponseMessage> emailCertification(@RequestBody EmailCheckRequestDto emailCheckRequestDto) throws BadRequestException {
        try {
            userService.checkCertification(emailCheckRequestDto);

            ResponseMessage response = ResponseMessage.builder()
                    .data("")
                    .statusCode(200)
                    .resultMessage("Certification successful")
                    .build();

            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            ResponseMessage response = ResponseMessage.builder()
                    .data("")
                    .statusCode(400)
                    .resultMessage(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(response);
        }
    }


    // 유저 전체 조회
    @GetMapping
    public ResponseEntity<ResponseMessage> getAllUsers() {
        List<User> users = userService.getAllUsers();

        ResponseMessage response = ResponseMessage.builder()
                .data(users)
                .statusCode(200)
                .resultMessage("Success")
                .build();

        return ResponseEntity.ok(response);
    }

    // id로 유저 찾기
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getUserById(@PathVariable("id") int id) throws BadRequestException {
        Optional<User> user = userService.getUserById(id);

        ResponseMessage response = ResponseMessage.builder()
                .data(user.get())
                .statusCode(200)
                .resultMessage("Success")
                .build();

        return ResponseEntity.ok(response);
    }

    // 회원 수정
    @PutMapping("/update-user")
    public ResponseEntity<ResponseMessage> updateUser(@RequestBody PutUserRequestDto putUserRequestDto) throws BadRequestException {
        User updatedUser = userService.updateUser(putUserRequestDto); // exception은 서비스에서 내주기

        ResponseMessage response = ResponseMessage.builder()
                .data(updatedUser)
                .statusCode(200)
                .resultMessage("User updated successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable("id") int id) {
        userService.deleteUser(id);

        ResponseMessage response = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("User deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            ResponseMessage response = ResponseMessage.builder()
                    .statusCode(400)
                    .resultMessage("Invalid token")
                    .build();
            return ResponseEntity.status(400).body(response);
        }
        token = token.substring(7);
        if (jwtBlacklist.contains(token)) {
            ResponseMessage response = ResponseMessage.builder()
                    .statusCode(400)
                    .resultMessage("Token already logged out")
                    .build();
            return ResponseEntity.status(400).body(response);
        }
        jwtBlacklist.add(token);
        ResponseMessage response = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("Logout successful")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user-info")
    public ResponseEntity<ResponseMessage> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 이 어노테이션은 jwt토큰으로 사용자의 정보를 알아내야할때!
        User user = userDetails.getUser();
        ResponseMessage response = ResponseMessage.builder()
                .data(user.getEmail())
                .statusCode(200)
                .resultMessage("User info retrieved successfully")
                .build();
        return ResponseEntity.ok(response);
    }
    
    // 주문한 상품 상태 조회
    @GetMapping("/{id}/orders")
    public ResponseEntity<ResponseMessage> getUserOrders(@PathVariable("id") int id) throws BadRequestException {
        List<OrderDto> orders = orderServiceClient.getOrdersByUserId(id);

        ResponseMessage response = ResponseMessage.builder()
                .data(orders)
                .statusCode(200)
                .resultMessage("Orders retrieved successfully")
                .build();

        return ResponseEntity.ok(response);
    }

}

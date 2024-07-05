package sparta.userservice.user;


import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sparta.userservice.domain.User;
import sparta.userservice.dto.user.CreateUserRequestDto;
import sparta.userservice.dto.user.PutUserRequestDto;
import sparta.userservice.dto.user.UserCommonDto;
import sparta.userservice.utils.JwtUtil;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor()
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    public User createUser(CreateUserRequestDto createUserRequestDto) throws BadRequestException {

        // 아이디 중복체크
        if (idCheck(createUserRequestDto.getEmail()).isPresent()) {
            throw new BadRequestException("Email is already in use");
        }

        // 비밀번호 검증
        String password = createUserRequestDto.getPassword();
        if (!isPasswordValid(password)) {
            throw new BadRequestException("Password does not meet the security requirements");
        }

        User user = new User();
        user.setEmail(createUserRequestDto.getEmail());
        user.setName(createUserRequestDto.getName());  // username 설정
        user.setType("web");
        user.setPassword(passwordEncoder.encode(createUserRequestDto.getPassword())); // 비밀번호 암호화

        return userRepository.save(user);
    }


    // 비밀번호 유효성 검사
    private boolean isPasswordValid(String password) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*?_])[A-Za-z\\d!@#$%^&*?_]{8,16}$";
        return password.matches(passwordPattern);
    }


    // 로그인
    public String login(UserCommonDto userCommonDto) throws BadRequestException { //dto로 받기

        Optional<User> user = idCheck(userCommonDto.getEmail());
        String token = null;

        if (user.isEmpty()) {
            throw new BadRequestException("Invalid email or password");
        }

        // 비밀번호 검증
        if (passwordEncoder.matches(userCommonDto.getPassword(), user.get().getPassword())) {
            token = jwtUtil.createToken(user.get().getUserId(), user.get().getRole().toString());

            return token;
        }
        throw new BadRequestException("Invalid email or password");
    }


    // 이메일 존재 확인
    public Optional<User> idCheck(String email) {
        return userRepository.findByEmail(email);
    }


    // 전체 유저 조회
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    // id로 유저 찾기
    public Optional<User> getUserById(int userId) throws BadRequestException {

        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new BadRequestException("User not found");
        }

        return user;
    }


    // 회원 수정
    public User updateUser(PutUserRequestDto putUserRequestDto) {

        Optional<User> optionalUser = userRepository.findById(putUserRequestDto.getUserId());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEmail(putUserRequestDto.getEmail());

            return userRepository.save(user);

        } else {
            throw new RuntimeException("User not found with id " + putUserRequestDto.getUserId());
        }
    }


    // 회원삭제
    public void deleteUser(int userId) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

}
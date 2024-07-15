package sparta.userservice.user;


import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sparta.userservice.domain.User;
import sparta.userservice.dto.user.*;
import sparta.userservice.provider.email.EmailProvider;
import sparta.userservice.utils.JwtUtil;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor()
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailProvider emailProvider;
    private final RedisService redisService;

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
        user.setAddress(createUserRequestDto.getAddress());
        user.setPhone(createUserRequestDto.getPhone());
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


    // 회원 비밀번호 수정
    public User updatePassword(int userId, PutPasswordRequestDto putPasswordRequestDto) throws BadRequestException {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (putPasswordRequestDto.getPassword() != null && !putPasswordRequestDto.getPassword().isEmpty()) {
                // 비밀번호 검증
                String password = putPasswordRequestDto.getPassword();
                if (!isPasswordValid(password)) {
                    throw new BadRequestException("Password does not meet the security requirements");
                }

                user.setPassword(passwordEncoder.encode(putPasswordRequestDto.getPassword()));  // 비밀번호 암호화
            }

            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id " + userId);
        }
    }

    // 회원 정보 수정
    public User updateUser(int userId, PutUserdRequestDto putUserdRequestDto) throws BadRequestException {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // 이부분: 회원 정보 업데이트
            user.setPhone(putUserdRequestDto.getPhone());
            user.setAddress(putUserdRequestDto.getAddress());

            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id " + userId);
        }
    }


    // 회원삭제
    public void deleteUser(int userId) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }


    public void checkCertification(EmailCheckRequestDto emailCheckRequestDto) throws BadRequestException {

        try {
            int userId = emailCheckRequestDto.getUserId();
            String email = emailCheckRequestDto.getEmail();
            String certificationNumber = emailCheckRequestDto.getCertificationNumber();

            // Redis에서 인증 코드 확인
            String key = "cert:" + email;
            String storedCertificationNumber = (String) redisService.getData(key);

            if (storedCertificationNumber == null || !storedCertificationNumber.equals(certificationNumber)) {
                throw new BadRequestException("인증 번호가 일치하지 않거나 만료되었습니다.");
            }

            // 인증이 완료되면 Redis에서 인증 코드를 삭제
            redisService.deleteData(key);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("데이터 베이스 에러입니다.");
        }
    }

}














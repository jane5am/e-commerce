package sparta.userservice.user;

import jakarta.ws.rs.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparta.userservice.domain.User;
import sparta.userservice.dto.user.SendEmailRequestDto;
import sparta.userservice.provider.email.EmailProvider;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private EmailProvider emailProvider;

    @Autowired
    private UserRepository userRepository;

    public void sendCertificationEmail(SendEmailRequestDto sendEmailRequestDto) throws BadRequestException {
        try {
            String email = sendEmailRequestDto.getEmail();

            Optional<User> existedUser = userRepository.findByEmail(email);
            if (existedUser.isPresent()) {
                throw new BadRequestException("이미 가입된 이메일 입니다.");
            }

            String certificationNumber = generateCertificationNumber();

            // Redis에 인증 코드 저장 (TTL 설정: 5분)
            redisService.saveDataWithTTL("cert:" + email, certificationNumber, 5, TimeUnit.MINUTES);

            // 이메일 전송
            boolean isSuccessed = emailProvider.sendCertificationMail(email, certificationNumber);
            if (!isSuccessed) {
                throw new BadRequestException("이메일 인증 메일 전송에 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("이메일 인증 메일 전송에 실패했습니다.");
        }
    }

    private String generateCertificationNumber() {
        // 인증 번호 생성 로직 (6자리 숫자 예시)
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

}

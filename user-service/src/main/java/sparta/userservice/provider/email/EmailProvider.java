package sparta.userservice.provider.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    private final String title = "[ Ecommerce ] 구글 인증 이메일"; // ap

    // 메일전송 메소드, certificationNumber은 인증번호
    public boolean sendCertificationMail ( String email, String certificationNumber ){

        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getCertificationMessage(certificationNumber);

            messageHelper.setTo(email); // 어디로 보낼건지
            messageHelper.setSubject(title);
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);

        } catch ( Exception e ){
            e.printStackTrace();
            return false;
        }

        return true;
    }


    private String getCertificationMessage (String certificationNumber){

        String certificationMessage = "";
        certificationMessage += "<h1 style= 'text-align: cernter;'> [ Ecommerce ] 구글 인증 이메일</h1>";
        certificationMessage += "<h3 style= 'text-align: center;'> 인증코드 : <strong style='font-size: 32px; letter-spacing:8px;'>" + certificationNumber + "</strong></h3>";
        return certificationMessage;

    }
}

package sparta.userservice.utils;

import org.springframework.stereotype.Component;

@Component
public class GenerateCertificationNumberUtil {

   public static String getCertificationNumber () {

       String certificationNumber = "";

       for (int count = 0; count < 4; count++ ){
           certificationNumber+= (int)(Math.random() *10 );
       }

       return certificationNumber;
   }
}

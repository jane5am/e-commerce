package sparta.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ECommerceRestController {

    @GetMapping("/health_check")
    public String healthCheck() {
        return "OK";
    }
}

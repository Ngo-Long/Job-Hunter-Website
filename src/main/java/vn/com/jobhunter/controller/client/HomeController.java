package vn.com.jobhunter.controller.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String getRunProduct() {
        return "Run product successfully!";
    }
}

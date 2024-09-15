package com.store.Ecommerce.Ecommerce.api.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @RequestMapping("/hello")
    public String hi(){
        System.out.println("nccc");
        return "hello world";
    }
}

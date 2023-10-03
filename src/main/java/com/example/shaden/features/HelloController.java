package com.example.shaden.features;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {
    
    @GetMapping(value = "/hello")
    public String HelloWorld() {
        return "Hello World";
    }

}

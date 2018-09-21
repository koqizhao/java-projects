package io.study.springboot.example;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author koqizhao
 *
 * Sep 21, 2018
 */
@RestController
@EnableAutoConfiguration
public class Example {
    
    @RequestMapping("/")
    public String home() {
        return "Hello World!";
    }

}

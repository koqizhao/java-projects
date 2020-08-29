package io.study.dubbo.starter.springboot;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.study.dubbo.starter.api.HelloService;

@Component
public class TestConsumer {
    
    @Autowired
    private HelloService service;
    
    @PostConstruct
    private void test() {
        service.hello("test");
    }

}

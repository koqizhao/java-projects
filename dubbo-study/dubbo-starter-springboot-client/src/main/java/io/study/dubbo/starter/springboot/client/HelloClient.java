package io.study.dubbo.starter.springboot.client;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import io.study.dubbo.starter.api.HelloService;

@Component
public class HelloClient implements HelloService {

    @DubboReference(version = "1.0.0")
    private HelloService client;
    
    public String hello(String persion) {
        System.out.println("client hi");
        String res = client.hello(persion);
        System.out.println("client hi res: " + res);
        return res;
    }

}

package io.study.dubbo.starter.springboot;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication//(scanBasePackages = {"io.study.dubbo.starter.springboot"})
//@DubboComponentScan//(basePackages =  {"io.study.dubbo.starter.springboot"})
@EnableDubbo//(scanBasePackages =  {"io.study.dubbo.starter.springboot"})
public class App {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }

}

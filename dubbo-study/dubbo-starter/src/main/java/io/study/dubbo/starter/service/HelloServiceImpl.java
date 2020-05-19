package io.study.dubbo.starter.service;

import io.study.dubbo.starter.api.HelloService;

public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String person) {
        return String.format("Hello, %s!", person);
    }

}

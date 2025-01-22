package io.study.dubbo.starter.springboot.service;

import org.apache.dubbo.config.annotation.DubboService;
//import org.dromara.soul.client.dubbo.common.annotation.SoulDubboClient;

import io.study.dubbo.starter.service.HelloServiceImpl;

@DubboService(version = "${hello.service.version}")
public class SpringbootHelloServiceImpl extends HelloServiceImpl {

    @Override
    //@SoulDubboClient(path = "/hello-service/hello", desc = "你好！")
    public String hello(String person) {
        return super.hello(person);
    }

}

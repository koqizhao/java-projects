package io.study.dubbo.starter.server;

import java.util.concurrent.CountDownLatch;

import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

import io.study.dubbo.starter.api.HelloService;
import io.study.dubbo.starter.service.HelloServiceImpl;

public class Server {

    private RegistryConfig _registryConfig;
    
    public Server(RegistryConfig registryConfig) {
        _registryConfig = registryConfig;
    }

    public void start() throws Exception {
        ServiceConfig<HelloService> service = new ServiceConfig<>();
        //service.setApplication(new ApplicationConfig("first-dubbo-provider"));
        service.setRegistry(_registryConfig);
        service.setInterface(HelloService.class);
        service.setRef(new HelloServiceImpl());
        service.export();

        System.out.println("dubbo service started");
        new CountDownLatch(1).await();
    }

}

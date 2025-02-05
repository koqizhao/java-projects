package io.study.bytebuddy.sf;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class SampleService {

    private final Invoker<String, String> invoker;

    private AtomicBoolean running = new AtomicBoolean(false);

    private final String hello(String name) {
        return invoker.invoke(name);
    }

    public void start() {
        running.set(true);
        Thread thread = new Thread(() -> {
            while (running.get()) {
                String resp = hello("world");
                System.out.println(resp);
                System.out.println();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running.set(false);
    }

}

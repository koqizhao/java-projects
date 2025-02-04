package io.study.bytebuddy.agent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TestClass {
    private final String name;

    public void sayHello() {
        System.out.println("hello " + name);
    }

}

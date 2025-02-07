package io.study.bytebuddy.agent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TestClass {
    private String name;

    public void sayHello() {
        System.out.println("hello " + name);
    }

}

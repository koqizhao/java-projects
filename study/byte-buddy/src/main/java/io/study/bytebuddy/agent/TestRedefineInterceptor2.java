package io.study.bytebuddy.agent;

import net.bytebuddy.implementation.bind.annotation.*;

public class TestRedefineInterceptor2 {

    @RuntimeType
    public static void intercept(@This Object obj,
                          @Origin String method,
                          @FieldValue("name") String name,
                          @AllArguments Object[] args) throws Exception {
        System.out.println("Hello, " + name + "(redefined)");
    }

}

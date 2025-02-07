package io.study.bytebuddy.agent;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class TestRebaseInterceptor {

    @RuntimeType
    public static Object intercept(@This Object obj,
                          @Origin Method method,
                          @SuperCall Callable<?> callable,
                          @AllArguments Object[] args) throws Exception {
        try {
            System.out.println("enter: " + method + ", args: " + args);
            return callable.call();
        } catch (Exception e) {
            System.out.println("error: " + e);
            throw e;
        } finally {
            System.out.println("exit: " + method);
        }
    }

}

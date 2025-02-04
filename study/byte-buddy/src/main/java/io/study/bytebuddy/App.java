package io.study.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.ToStringMethod;
import net.bytebuddy.jar.asm.Opcodes;

import java.lang.reflect.Method;

public class App {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");

        Class<?> type = new ByteBuddy().subclass(Object.class)
                .name("io.study.bytebuddy.DynamicType")
                .defineField("value", int.class, Opcodes.ACC_PRIVATE)
                .defineMethod("setValue", void.class, Opcodes.ACC_PUBLIC)
                .withParameter(int.class)
                .intercept(FieldAccessor.ofField("value"))
                .defineMethod("getValue", int.class, Opcodes.ACC_PUBLIC)
                .intercept(FieldAccessor.ofField("value"))
                .defineMethod("toString", String.class, Opcodes.ACC_PUBLIC)
                //.intercept(MethodCall.call(() -> "Hello World"))
                .intercept(ToStringMethod.prefixedByCanonicalClassName())
                .defineMethod("hello", void.class, Opcodes.ACC_PUBLIC)
                .intercept(MethodCall.run(() -> System.out.println("Hello World")))
                .make()
                .load(App.class.getClassLoader())
                .getLoaded();
        System.out.printf("type: %s\n", type);

        Object instance = type.newInstance();
        Method setValue = type.getMethod("setValue", int.class);
        setValue.invoke(instance, 100);
        Method getValue = type.getMethod("getValue");
        int v = (int) getValue.invoke(instance);
        Method hello = type.getMethod("hello");
        hello.invoke(instance);
        System.out.println(instance);
    }
}

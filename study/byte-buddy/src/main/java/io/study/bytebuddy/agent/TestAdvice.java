package io.study.bytebuddy.agent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

public class TestAdvice {

    @Advice.OnMethodEnter
    public static void before(@Advice.This Object obj,
        @Advice.Origin String method,
        @Advice.AllArguments Object[] args) {
        System.out.println("obj="+obj);
        System.out.println("args="+args);
        System.out.println("before method call: " + method);
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void after(@Advice.This Object obj,
                             @Advice.Origin String method,
                             @Advice.AllArguments Object[] args,
                             @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object result,
                             @Advice.Thrown(typing = Assigner.Typing.DYNAMIC) Throwable throwable)  {
        System.out.println("return="+result);
        System.out.println("throwable="+throwable);
        System.out.println("after method call: " + method);
    }

}

package io.study.bytebuddy.agent;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class App2 {
    public static void main(String[] args) throws Exception {
        ByteBuddyAgent.install();
        Instrumentation instrumentation = ByteBuddyAgent.getInstrumentation();
        System.out.println(instrumentation);
        var b1 = instrumentation.isRetransformClassesSupported();
        var b2 = instrumentation.isRedefineClassesSupported();
        System.out.printf("isRetransformClassesSupported: %s, isRedefineClassesSupported: %s\n", b1, b2);
        System.out.println();

        TypePool typePool = TypePool.Default.ofSystemLoader();
        TypeDescription typeDescription = typePool.describe("io.study.bytebuddy.agent.TestClass").resolve();
        ClassFileLocator classFileLocator = ClassFileLocator.ForClassLoader.ofSystemLoader();
        var clazz = new ByteBuddy()
                .rebase(typeDescription, classFileLocator)
                .method(isDeclaredBy(typeDescription))
                .intercept(MethodDelegation.to(TestRebaseInterceptor.class))
                .make()
                .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
                .getLoaded();
        System.out.println("clazz: " + clazz);
        System.out.println();

        clazz = TestClass.class;
        System.out.println("clazz: " + clazz);
        var obj = new TestClass("test");
        obj.sayHello();
        System.out.println("name: " + obj.getName());
        System.out.println();

        obj.setName("test2");
        obj.getName();
        obj.sayHello();
        System.out.println();

        ClassLoader classLoader = int.class.getClassLoader();
        System.out.printf("int: %s\n", classLoader);
        classLoader = HashMap.class.getClassLoader();
        System.out.printf("HashMap: %s\n", classLoader);
        classLoader = TestClass.class.getClassLoader();
        System.out.printf("TestClass: %s\n", classLoader);
        System.out.printf("TestClass Parent: %s\n", classLoader.getParent());
    }
}

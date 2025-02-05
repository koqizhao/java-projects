package io.study.bytebuddy.agent;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.Set;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class App {
    public static void main(String[] args) throws Exception {
        ByteBuddyAgent.install();
        Instrumentation instrumentation = ByteBuddyAgent.getInstrumentation();
        System.out.println(instrumentation);
        var b1 = instrumentation.isRetransformClassesSupported();
        var b2 = instrumentation.isRedefineClassesSupported();
        System.out.printf("isRetransformClassesSupported: %s, isRedefineClassesSupported: %s\n", b1, b2);
        System.out.println();

        var clazz = TestClass.class;
        System.out.println("clazz: " + clazz);
        var obj = new TestClass("test");
        obj.sayHello();
        System.out.println("name: " + obj.getName());
        System.out.println();

        var clazz2 = new ByteBuddy()
                .redefine(TestClass.class)
                .method(named("getName"))
                .intercept(MethodDelegation.to(TestRedefineInterceptor.class))
                .method(named("sayHello"))
                .intercept(MethodDelegation.to(TestRedefineInterceptor2.class))
                .make()
                .load(TestClass.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
                .getLoaded();
        System.out.println("clazz2: " + clazz2);
        obj.sayHello();
        System.out.println("name: " + obj.getName());
        System.out.println();

        var clazz3 = new ByteBuddy()
                .subclass(TestClass.class)
                .method(isDeclaredBy(TestClass.class))
                .intercept(Advice.to(TestAdvice.class))
                .make()
                .load(TestClass.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        System.out.println("clazz3: " + clazz3);
        obj.sayHello();
        var obj2 = clazz3.getDeclaredConstructor(String.class).newInstance("test2");
        obj2.sayHello();
        System.out.println();

        new AgentBuilder.Default()
                .type(is(TestClass.class))
                //.type(named("io.study.bytebuddy.agent.TestClass"))
                //.transform(new TestTransformer())
                .transform(new TestTransformer2())
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .with(new Listener())
                .with(new InstallationListener())
                .installOn(instrumentation);
        System.out.println("clazz4: " + TestClass.class);
        obj.sayHello();
        System.out.println();
    }

    public static class Listener implements AgentBuilder.Listener {

        @Override
        public void onDiscovery(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
            //System.out.println("onDiscovery: " + s);
        }

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b, DynamicType dynamicType) {
            System.out.println("onTransformation: " + typeDescription);
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {
            //System.out.println("onIgnored: " + typeDescription);
        }

        @Override
        public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {
            System.out.println("onError: " + s + ", error: " + throwable);
        }

        @Override
        public void onComplete(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
            //System.out.println("onComplete: " + s);
        }
    }

    public static class InstallationListener implements AgentBuilder.InstallationListener {

        @Override
        public void onBeforeInstall(Instrumentation instrumentation, ResettableClassFileTransformer resettableClassFileTransformer) {

        }

        @Override
        public void onInstall(Instrumentation instrumentation, ResettableClassFileTransformer resettableClassFileTransformer) {
            System.out.println("onInstall: " + resettableClassFileTransformer);
        }

        @Override
        public Throwable onError(Instrumentation instrumentation, ResettableClassFileTransformer resettableClassFileTransformer, Throwable throwable) {
            System.out.println("onError: " + resettableClassFileTransformer + ", error: " + throwable);
            return null;
        }

        @Override
        public void onReset(Instrumentation instrumentation, ResettableClassFileTransformer resettableClassFileTransformer) {

        }

        @Override
        public void onBeforeWarmUp(Set<Class<?>> set, ResettableClassFileTransformer resettableClassFileTransformer) {

        }

        @Override
        public void onWarmUpError(Class<?> aClass, ResettableClassFileTransformer resettableClassFileTransformer, Throwable throwable) {

        }

        @Override
        public void onAfterWarmUp(Map<Class<?>, byte[]> map, ResettableClassFileTransformer resettableClassFileTransformer, boolean b) {

        }
    }
}

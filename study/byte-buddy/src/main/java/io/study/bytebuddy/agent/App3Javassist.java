package io.study.bytebuddy.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.Instrumentation;

public class App3Javassist {
    public static void main(String[] args) throws Exception {
        ByteBuddyAgent.install();
        Instrumentation instrumentation = ByteBuddyAgent.getInstrumentation();
        System.out.println(instrumentation);
        var b1 = instrumentation.isRetransformClassesSupported();
        var b2 = instrumentation.isRedefineClassesSupported();
        System.out.printf("isRetransformClassesSupported: %s, isRedefineClassesSupported: %s\n", b1, b2);
        System.out.println();

        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("io.study.bytebuddy.agent.TestClass");
        for (CtMethod m : cc.getDeclaredMethods()) {
            m.insertBefore("System.out.println(\"before " + m.getName() + "\");");
            m.addCatch("{ System.out.println($e); throw $e; }", pool.get("java.lang.Exception"));
            m.insertAfter("System.out.println(\"after " + m.getName() + "\");");
            System.out.println("AOP: " + m.getName());
        }
        cc.toClass();

        var clazz = TestClass.class;
        System.out.println("clazz: " + clazz);
        System.out.println();

        System.out.println("clazz: " + clazz);
        var obj = new TestClass("test");
        obj.sayHello();
        System.out.println("name: " + obj.getName());
        System.out.println();

        obj.setName("test2");
        obj.getName();
        obj.sayHello();
        System.out.println();

        instrumentation.addTransformer(new JavassistTransformer(), true);
        instrumentation.retransformClasses(clazz);
        obj.setName("test3");
        obj.getName();
        obj.sayHello();
        System.out.println();
    }
}

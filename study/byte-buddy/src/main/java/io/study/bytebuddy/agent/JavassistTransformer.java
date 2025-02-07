package io.study.bytebuddy.agent;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author koqizhao
 *
 * Sep 17, 2018
 */
public class JavassistTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.equals("io/study/bytebuddy/agent/TestClass")) {
            return null;
        }
        System.out.println("Transforming " + className);
        ClassPool pool = null;
        CtClass cl = null;
        try {
            pool = ClassPool.getDefault();

            cl = pool.get("io.study.bytebuddy.agent.TestClass");
            cl.defrost();
            CtMethod[] methods = cl.getDeclaredMethods();
            for (CtMethod method : methods) {
                if (!method.isEmpty()) {
                    AOPInsertMethod(method);
                }
            }
            return cl.toBytecode();
        } catch (Exception e) {
            System.err.println("Could not instrument  " + className + ",  exception : " + e.getMessage());
            return null;
        }
    }

    private void AOPInsertMethod(CtMethod method) throws NotFoundException, CannotCompileException {
        method.instrument(new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                m.replace("{ long stime = System.currentTimeMillis(); $_ = $proceed($$);System.out.println(\""
                        + m.getClassName() + "." + m.getMethodName()
                        + " cost:\" + (System.currentTimeMillis() - stime) + \" ms\");}");
            }
        });
    }

}
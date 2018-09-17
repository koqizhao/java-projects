package io.study.java_agent;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

/**
 * @author koqizhao
 *
 * Sep 17, 2018
 */
public class JavassistTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] transformed = null;
        System.out.println("Transforming " + className);
        ClassPool pool = null;
        CtClass cl = null;
        try {
            pool = ClassPool.getDefault();

            cl = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
            if (cl.isInterface() == false) {
                CtMethod[] methods = cl.getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].isEmpty() == false) {
                        AOPInsertMethod(methods[i]);
                    }
                }

                transformed = cl.toBytecode();
            }
        } catch (Exception e) {
            System.err.println("Could not instrument  " + className + ",  exception : " + e.getMessage());
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
        return transformed;
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
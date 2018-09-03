package io.study.dynamicproxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public class ClassProxy {
    public static void main(String[] args) {
        //classProxy1(args);
        classProxy2(args);
    }

    public static void classProxy1(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(DefaultPerson.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
                System.out.printf("method %s start\n", arg1.getName());
                Object ret = arg3.invokeSuper(arg0, arg2);
                System.out.printf("method %s end\n", arg1.getName());
                return ret;
            }
        });

        DefaultPerson person = (DefaultPerson) enhancer.create();
        person.doSomeThing();
        System.out.printf("person: %s\n", person);
    }

    public static void classProxy2(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(DefaultPerson.class);
        enhancer.setCallbackFilter(new CallbackFilter() {
            @Override
            public int accept(Method method) {
                switch (method.getName()) {
                    case "toString":
                    case "hashCode":
                    case "equals":
                        return 1;
                    default:
                        return 0;

                }
            }
        });
        enhancer.setCallbacks(new Callback[] { new MethodInterceptor() {
            @Override
            public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
                System.out.printf("method %s start\n", arg1.getName());
                Object ret = arg3.invokeSuper(arg0, arg2);
                System.out.printf("method %s end\n", arg1.getName());
                return ret;
            }
        }, NoOp.INSTANCE });

        DefaultPerson person = (DefaultPerson) enhancer.create();
        person.doSomeThing();
        System.out.printf("person: %s\n", person);
    }

}

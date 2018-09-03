package io.study.dynamic_proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Mixin;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public class InterfaceProxy {

    public static void main(String[] args) {
        //javaProxy(args);
        //cglibProxy(args);
        cglibProxy2(args);
    }

    public static void javaProxy(String[] args) {
        AtomicReference<Class<?>> clazz = new AtomicReference<Class<?>>();
        Person person = (Person) Proxy.newProxyInstance(InterfaceProxy.class.getClassLoader(),
                new Class[] { Person.class }, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        clazz.set(proxy.getClass());
                        System.out.printf("proxy type: %s\n", clazz.get());

                        switch (method.getName()) {
                            case "toString":
                                return "proxy1";
                            case "equals":
                                return false;
                            case "hashcode":
                                return 0;
                            default:
                                return null;
                        }
                    }
                });

        person.doSomeThing();

        AtomicReference<Class<?>> clazz2 = new AtomicReference<Class<?>>();
        Person person2 = (Person) Proxy.newProxyInstance(InterfaceProxy.class.getClassLoader(),
                new Class[] { Person.class }, new InvocationHandler() {

                    private Person _real = new DefaultPerson();

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        clazz2.set(proxy.getClass());
                        System.out.printf("proxy type: %s\n", clazz2.get());

                        System.out.println("doSomeThing start");
                        Object ret = method.invoke(_real, args);
                        System.out.println("doSomeThing end");
                        return ret;
                    }
                });

        person2.doSomeThing();

        System.out.printf("clazz == clazz2 ? %s\n", clazz.get() == clazz2.get());
        System.out.printf("person: %s, person2: %s\n", person, person2);
        System.out.printf("person == person2 ? %s\n", person.equals(person2));
        System.out.printf("person2 == person ? %s\n", person2.equals(person));
    }

    public static void cglibProxy(String[] args) {
        AtomicReference<Class<?>> clazz = new AtomicReference<Class<?>>();
        Enhancer enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[] { Person.class });
        enhancer.setCallback(new net.sf.cglib.proxy.InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                clazz.set(proxy.getClass());
                System.out.printf("proxy type: %s\n", clazz.get());

                switch (method.getName()) {
                    case "toString":
                        return "proxy1";
                    case "equals":
                        return false;
                    case "hashcode":
                        return 0;
                    default:
                        return null;
                }
            }
        });
        Person person = (Person) enhancer.create();
        person.doSomeThing();

        AtomicReference<Class<?>> clazz2 = new AtomicReference<Class<?>>();
        enhancer = new Enhancer();
        enhancer.setInterfaces(new Class[] { Person.class });
        enhancer.setCallback(new net.sf.cglib.proxy.InvocationHandler() {
            private Person _real = new DefaultPerson();

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                clazz2.set(proxy.getClass());
                System.out.printf("proxy type: %s\n", clazz2.get());

                System.out.println("doSomeThing start");
                Object ret = method.invoke(_real, args);
                System.out.println("doSomeThing end");
                return ret;

            }
        });

        Person person2 = (Person) enhancer.create();
        person2.doSomeThing();

        System.out.printf("clazz == clazz2 ? %s\n", clazz.get() == clazz2.get());
        System.out.printf("person: %s, person2: %s\n", person, person2);
        System.out.printf("person == person2 ? %s\n", person.equals(person2));
        System.out.printf("person2 == person ? %s\n", person2.equals(person));
    }

    public static void cglibProxy2(String[] args) {
        Mixin mixin = Mixin.create(new Class[] { Person.class, Person2.class },
                new Object[] { new DefaultPerson(), new DefaultPerson2() });
        ((Person) mixin).doSomeThing();
        ((Person2) mixin).doSomeThing2();

        System.out.println();
        System.out.println();

        mixin = Mixin.create(new Object[] { new DefaultPerson(), new DefaultPerson2() });
        ((Person) mixin).doSomeThing();
        ((Person2) mixin).doSomeThing2();
    }

}

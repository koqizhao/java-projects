package io.study.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public class InterfaceProxy {

    public static void main(String[] args) {
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

                    private Person _real = new Person.DefaultPerson();

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

}

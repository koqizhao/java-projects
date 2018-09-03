package io.study.dynamicproxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.NoOp;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public class CglibCallbacks {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Calculator.class);
        enhancer.setCallbackFilter(new CallbackFilter() {
            @Override
            public int accept(Method method) {
                switch (method.getName()) {
                    case "plus":
                        return 0;
                    case "multiply":
                        return 1;
                    default:
                        return 2;
                }
            }
        });
        enhancer.setCallbacks(new Callback[] { new FixedValue() {
            @Override
            public Object loadObject() throws Exception {
                return -1;
            }
        }, new FixedValue() {
            @Override
            public Object loadObject() throws Exception {
                return -2;
            }
        }, NoOp.INSTANCE });

        Calculator calculator = (Calculator) enhancer.create();
        System.out.printf("plus: %s\n", calculator.plus(1, 2));
        System.out.printf("multiply: %s\n", calculator.multiply(1, 2));
    }

}

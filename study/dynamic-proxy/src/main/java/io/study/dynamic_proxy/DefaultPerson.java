package io.study.dynamic_proxy;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public class DefaultPerson implements Person {

    @Override
    public void doSomeThing() {
        System.out.println("I'm doting something.");
        m1();
        m2();
        m3();
        m4();
    }

    private void m1() {
        System.out.println("m1");
    }

    protected void m2() {
        System.out.println("m2");
    }

    void m3() {
        System.out.println("m3");
    }

    public final void m4() {
        System.out.println("m4");
    }
}

package io.study.dynamic_proxy;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public class DefaultPerson2 implements Person2 {

    @Override
    public void doSomeThing2() {
        System.out.println("I'm doting something 2.");
        m1();
        m2();
        m3();
        m4();
    }

    private void m1() {
        System.out.println("m1 2");
    }

    protected void m2() {
        System.out.println("m2 2");
    }

    void m3() {
        System.out.println("m3 2");
    }

    public final void m4() {
        System.out.println("m4 2");
    }
}

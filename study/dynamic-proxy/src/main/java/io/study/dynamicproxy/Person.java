package io.study.dynamicproxy;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public interface Person {

    void doSomeThing();

    public static class DefaultPerson implements Person {

        @Override
        public void doSomeThing() {
            System.out.println("I'm doting something.");
        }

    }

}

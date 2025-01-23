package io.study.classloader.component;

public interface Component extends Runnable, AutoCloseable {

    default String getName() {
        return getClass().getName();
    }

    @Override
    default void run() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            doRun();
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    void doRun();
}

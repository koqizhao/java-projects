package io.study.java_agent;

import java.lang.instrument.Instrumentation;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public class Program {

    private static volatile Instrumentation _instrumentation;

    public static void main(String[] args) throws InterruptedException {
        if (!instrument())
            return;

        while (true) {
            Thread.sleep(1000);
            System.out.println(_instrumentation.isModifiableClass(Program.class));
        }
    }

    public static boolean instrument() {
        /*
        return JavaAgentUtil.loadAgent(
                "/home/koqizhao/Projects/koqizhao/java-projects/study/java-agent/target/io.study.java-agent-0.0.1.jar");
        */

        return JavaAgentUtil.loadAgent(RuntimeAgent.class, "test");
    }

    public static void setInstrumentation(Instrumentation instrumentation) {
        _instrumentation = instrumentation;
    }
}

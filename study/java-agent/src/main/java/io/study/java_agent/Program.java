package io.study.java_agent;

import java.lang.instrument.Instrumentation;

import io.study.java_agent.model.Calculator;
import io.study.java_agent.util.JavaAgentUtil;

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

        System.out.println("can transform classes: " + _instrumentation.isRetransformClassesSupported());
        System.out.println("can define classes: " + _instrumentation.isRedefineClassesSupported());
        System.out.println(_instrumentation.isModifiableClass(Program.class));

        _instrumentation.addTransformer(new AsmTransformer(), false);
        _instrumentation.addTransformer(new JavassistTransformer(), false);
        System.out.println("added transformer");

        while (true) {
            Thread.sleep(1000);

            Calculator calculator = new Calculator();
            calculator.plus(1, 2);
            calculator.multiply(1, 2);
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

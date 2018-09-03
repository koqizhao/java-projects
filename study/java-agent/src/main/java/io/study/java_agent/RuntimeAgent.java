package io.study.java_agent;

import java.lang.instrument.Instrumentation;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public class RuntimeAgent {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("runtime agent started");
    }

}

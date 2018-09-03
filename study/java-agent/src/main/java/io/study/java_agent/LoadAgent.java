package io.study.java_agent;

import java.lang.instrument.Instrumentation;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public class LoadAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("load agent started");
    }

}

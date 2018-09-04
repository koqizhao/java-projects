package io.study.java_agent;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public class Program {

    public static void main(String[] args) throws InterruptedException, AttachNotSupportedException, IOException,
            AgentLoadException, AgentInitializationException {
        System.out.println("Hello, world!");

        int processID = getProcessID();
        System.out.println(processID);
        VirtualMachine vm = VirtualMachine.attach(String.valueOf(processID));
        vm.loadAgent(
                "/home/koqizhao/Projects/koqizhao/java-projects/study/java-agent/target/io.study.java-agent-0.0.1.jar");
        vm.detach();

        while (true) {
            Thread.sleep(1000);
        }
    }

    public static final int getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        System.out.println(runtimeMXBean.getName());
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0]);
    }
}

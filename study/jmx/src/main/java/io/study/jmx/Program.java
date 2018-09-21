package io.study.jmx;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.sun.jdmk.comm.HtmlAdaptorServer;

/**
 * @author koqizhao
 *
 * Sep 3, 2018
 */
public class Program {

    public static void main(String[] args) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, InterruptedException {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        mbs.registerMBean(new Hello(), new ObjectName("mine.mbeans:name=hello"));

        HtmlAdaptorServer htmlAdaptorServer = new HtmlAdaptorServer();
        mbs.registerMBean(htmlAdaptorServer, new ObjectName("mine.mbeans:name=htmlServer"));
        htmlAdaptorServer.setPort(18082); // default port 8082
        htmlAdaptorServer.start();

        System.out.println("sleep...");
        Thread.sleep(10 * 60 * 1000);
    }

}

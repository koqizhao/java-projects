package io.study.elasticjob;

import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;

public class App {
    
    private static final String ZK_CONNECT = "192.168.56.11:2181";
    private static final String ZK_NAMESPACE = "elasticjob";

    public static void main(String[] args) {
        new ScheduleJobBootstrap(createRegistryCenter(), new MyJob(), MyJob.CONF).schedule();
    }

    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(
            new ZookeeperConfiguration(ZK_CONNECT, ZK_NAMESPACE));
        regCenter.init();
        return regCenter;
    }
}

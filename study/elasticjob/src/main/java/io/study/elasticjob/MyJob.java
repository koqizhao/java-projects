package io.study.elasticjob;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;

public class MyJob implements SimpleJob {
    
    public static final JobConfiguration CONF = JobConfiguration.newBuilder("MyJob", 3)
        .cron("0/5 * * * * ?").build();
    
    @Override
    public void execute(ShardingContext context) {
        System.out.println("Job is started.");
        switch (context.getShardingItem()) {
            case 0: 
                // do something by sharding item 0
                break;
            case 1: 
                // do something by sharding item 1
                break;
            case 2: 
                // do something by sharding item 2
                break;
            // case n: ...
        }
        System.out.println("Job is finished.");
    }
}
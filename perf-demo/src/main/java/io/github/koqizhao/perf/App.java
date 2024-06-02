package io.github.koqizhao.perf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings("unused")
public class App {
	
	public static void main(String[] args) {
		System.out.println("Hello, world!");
		int threadCount = 2, sleep = 1, garbageSize = 1000;
		if (args.length > 0)
		    threadCount = Integer.valueOf(args[0]);
		if (args.length > 1)
		    sleep = Integer.valueOf(args[1]);
		if (args.length > 2)
		    garbageSize = Integer.valueOf(args[2]);
		System.out.printf("threads=%s, sleep=%s, garbageSize=%s\n", threadCount, sleep, garbageSize);
		
		Sample sample = new Sample();
		sample.calculate(10, 20);
		System.out.printf("Sample: %s\n", sample);
		
		multiThreadInvoke2(threadCount, sleep, garbageSize);

		CountDownLatch countDownLatch = new CountDownLatch(1);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		    try {
		        countDownLatch.countDown();
		        System.out.println("Sleep 1s to exit.");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		}, "perf-shutdown-hook"));
		try {
		    countDownLatch.await();
		} catch (Exception e) {
        } finally {
		    System.out.println("Program exit.");
        }
	}
	
	private static void generateGarbage(int count) {
		int j = 0;
		List<Sample> tmpSamples = new ArrayList<Sample>();
		for (int i = 0; i < count; i++) {
			Sample sample = new Sample();
			sample.calculate(i, j);
			j = sample.getData();
			tmpSamples.add(sample);
		}
		//System.out.printf("tmp samples: %d, j: %d\n", tmpSamples.size(), j);
	}
	
	private static void noGarbage(int sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
		}
	}
	
	private static void randomInvoke(int sleep, int garbageSize) {
		Random random = new Random();
		int count = random.nextInt(garbageSize);
		int mod = count % 10;
		switch (mod) {
		    case 0:
		    case 1:
		        noGarbage(sleep);
		        break;
		    default:
		        generateGarbage(count);
		        break;
		}
	}
	
	private static void garbageInvoke(int garbageSize, int sleep) {
	    Random random = new Random();
		int count = random.nextInt(garbageSize);
		generateGarbage(count + 1);
		noGarbage(sleep);
	}
	
    private static void multiThreadInvoke(int threadCount, int sleep, int garbageSize) {
	    for (int i = 0; i < threadCount; i++) {
	        String workerName = "demo-worker-" + i;
	        Thread worker = new Thread(() -> {
	            while (true) {
	                randomInvoke(sleep, garbageSize);
	            }
	        }, workerName);
	        worker.setDaemon(true);
	        worker.start();
	        System.out.println(workerName + " started.");
	    }
	}

	private static void multiThreadInvoke2(int threadCount, int sleep, int garbageSize) {
	    for (int i = 0; i < threadCount; i++) {
	        String workerName = "garbage-worker-" + i;
	        Thread worker;
	        if (i % 3 == 0) {
	            workerName = "non-garbage-worker-" + i;
	            worker = new Thread(() -> {
	                while (true) {
	                    noGarbage(1000);
	                }
	            }, workerName);
	        } else {
	            worker = new Thread(() -> {
	                while (true) {
	                    garbageInvoke(garbageSize, sleep);
	                }
	            }, workerName);
	        }
	        worker.setDaemon(true);
	        worker.start();
	        System.out.println(workerName + " started.");
	    }
	}

}

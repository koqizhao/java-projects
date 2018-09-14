package io.study.jmh;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.agrona.collections.Long2ObjectHashMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import com.carrotsearch.hppc.LongObjectHashMap;
import com.romix.scala.collection.concurrent.TrieMap;

import javolution.util.FastMap;

/**
 * @author koqizhao
 *
 * Sep 14, 2018
 */
public class JMH_Map {

    @State(Scope.Benchmark)
    public static class MapState {
        public final HashMap<Long, String> map;

        public MapState() {
            map = new HashMap<>();
            map.put(Long.MAX_VALUE, "test-v");
        }
    }

    @State(Scope.Benchmark)
    public static class ConcurrentMapState {
        public final ConcurrentHashMap<Long, String> map;

        public ConcurrentMapState() {
            map = new ConcurrentHashMap<>();
            map.put(Long.MAX_VALUE, "test-v");
        }
    }

    @State(Scope.Benchmark)
    public static class HppcMapState {
        public final LongObjectHashMap<String> map;

        public HppcMapState() {
            map = new LongObjectHashMap<>();
            map.put(Long.MAX_VALUE, "test-v");
        }
    }

    @State(Scope.Benchmark)
    public static class JVMapState {
        public final FastMap<Long, String> map;

        public JVMapState() {
            map = new FastMap<>();
            map.put(Long.MAX_VALUE, "test-v");
        }
    }

    @State(Scope.Benchmark)
    public static class TrieMapState {
        public final Map<Long, String> map;

        public TrieMapState() {
            map = new TrieMap<>();
            map.put(Long.MAX_VALUE, "test-v");
        }
    }

    @State(Scope.Benchmark)
    public static class AgronaMapState {
        public final Long2ObjectHashMap<String> map;

        public AgronaMapState() {
            map = new Long2ObjectHashMap<>();
            map.put(Long.MAX_VALUE, "test-v");
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String get(MapState mapState) {
        return mapState.map.get(Long.MAX_VALUE);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String put(MapState mapState) {
        return mapState.map.put(Long.MAX_VALUE, "test-v");
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String concurrentGet(ConcurrentMapState mapState) {
        return mapState.map.get(Long.MAX_VALUE);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String concurrentPut(MapState mapState) {
        return mapState.map.put(Long.MAX_VALUE, "test-v");
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String hppcGet(HppcMapState mapState) {
        return mapState.map.get(Long.MAX_VALUE);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String hppcPut(HppcMapState mapState) {
        return mapState.map.put(Long.MAX_VALUE, "test-v");
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String jvGet(JVMapState mapState) {
        return mapState.map.get(Long.MAX_VALUE);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String jvPut(JVMapState mapState) {
        return mapState.map.put(Long.MAX_VALUE, "test-v");
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String trieGet(TrieMapState mapState) {
        return mapState.map.get(Long.MAX_VALUE);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String triePut(TrieMapState mapState) {
        return mapState.map.put(Long.MAX_VALUE, "test-v");
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String agronaGet(AgronaMapState mapState) {
        return mapState.map.get(Long.MAX_VALUE);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public String agronaPut(AgronaMapState mapState) {
        return mapState.map.put(Long.MAX_VALUE, "test-v");
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(JMH_Map.class.getSimpleName()).warmupBatchSize(1)
                .warmupIterations(1).measurementBatchSize(1).measurementIterations(3)
                .measurementTime(TimeValue.seconds(2)).forks(1).threads(2).shouldDoGC(true)
                .jvmArgs("-Xms512m", "-Xmx512m", "-Xmn384m", "-XX:+UseG1GC").build();
        new Runner(options).run();
    }

}

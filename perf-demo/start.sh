#!/bin/bash

threads=20
if [ "$1" != "" ]; then
  threads=$1
fi

sleep_ms=1
if [ "$2" != "" ]; then
  sleep_ms=$2
fi

garbage_size=10000
if [ "$3" != "" ]; then
  garbage_size=$3
fi

java -server -Xms1g -Xmx1g -Xmn256m -Xss256k -XX:SurvivorRatio=8 \
  -XX:+UseG1GC \
  -XX:+PrintGC \
  -XX:+PrintGCDetails \
  -Xloggc:./gc.out \
  -XX:+PrintClassHistogram \
  -XX:+PrintGCApplicationStoppedTime \
  -XX:+PrintGCTimeStamps \
  -XX:+PrintGCDateStamps \
  -XX:+PrintTenuringDistribution \
  -XX:+UnlockCommercialFeatures \
  -XX:+FlightRecorder \
  -jar target/perf-demo-*.jar $threads $sleep_ms $garbage_size 2>&1 > perf.out &

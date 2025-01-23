#!/bin/bash

p=`dirname $0`
echo "dirname: $p"
rp=`realpath $p`
echo "realpath: $rp"

cd $rp/../../
mvn clean package
mv target/io.study.classloader-0.0.1.jar $rp/classloader.jar
cd $rp
java -cp "$rp/*" io.study.classloader.component.Main \
    plugin1:$rp:io.study.classloader.component.plugin.Component1 \
    plugin2:$rp:io.study.classloader.component.plugin.Component2

package io.study.classloader.component.plugin;

import io.study.classloader.component.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Component1 implements Component {
    @Override
    public void doRun() {
        log.info("Component1 doRun");
    }

    @Override
    public void close() {
        log.info("Component1 close");
    }
}

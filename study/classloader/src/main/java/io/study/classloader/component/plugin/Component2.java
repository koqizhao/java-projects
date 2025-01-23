package io.study.classloader.component.plugin;

import io.study.classloader.component.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Component2 implements Component {
    @Override
    public void doRun() {
        log.info("Component2 doRun");
    }

    @Override
    public void close() {
        log.info("Component2 close");
    }
}

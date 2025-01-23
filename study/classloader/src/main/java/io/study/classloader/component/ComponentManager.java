package io.study.classloader.component;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ComponentManager {
    private final Map<Plugin, Component> components = new HashMap<>();

    public synchronized void load(Plugin plugin) {
        try {
            Component component = plugin.getComponent();
            components.put(plugin, component);
            log.info("{} loaded", component);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized void unload(Plugin plugin) {
        plugin.close();
        Component component = components.remove(plugin);
        if (component != null) {
            try {
                component.close();
            } catch (Exception e) {
                log.error("{} close failed", component, e);
            }
            component = null;
        }
        plugin.unload();
    }

    public synchronized void run() {
        for (Component component : components.values()) {
            component.run();
        }
    }
}

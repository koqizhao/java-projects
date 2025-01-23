package io.study.classloader.component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PluginClassLoader extends URLClassLoader {
    @Getter
    private final String name;
    private final Collection<String> commonClassList = Arrays.asList(
        "io.study.classloader.component.Component",
        "io.study.classloader.component.core."
    );
    private final Map<String, Class<?>> pluginClassMap = new HashMap<>();

    public PluginClassLoader(String name, URL[] urls) {
        super(urls, PluginClassLoader.class.getClassLoader());
        this.name = name;
        log.info("PluginClassLoader {} created with jars: {}", name, urls);
    }

    private boolean isCommonClass(String name) {
        return commonClassList.stream().anyMatch(cls -> cls.equals(name) || cls.startsWith(name));
    }

    private Class<?> findPluginClass(String name) throws ClassNotFoundException {
        if (!pluginClassMap.containsKey(name)) {
            Class<?> cls = null;
            try {
                cls = findClass(name);
            } catch (ClassNotFoundException e)  {
                // ignore
            }
            addClassToCache(name, cls);
        }
        return pluginClassMap.get(name);
    }

    private void addClassToCache(String name, Class<?> cls) {
        pluginClassMap.put(name, cls);
        if (cls != null) {
            log.info("PluginClassLoader {} add class {}/{} to cache", name, cls.getName(), cls.hashCode());
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> cls = null;
            if (!isCommonClass(name)) {
                cls = findPluginClass(name);
            }
            if (cls == null) {
                cls = super.loadClass(name, resolve);
                if (cls != null && cls.getClassLoader() == this) {
                    addClassToCache(name, cls);
                }
            }
            if (resolve) {
                resolveClass(cls);
            }
            return cls;
        }
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public String toString() {
        return "PluginClassLoader{" +
                "name='" + name + "',hash='" + hashCode() + '\'' +
                '}';
    }
}

package io.study.classloader.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
@RequiredArgsConstructor
public class Plugin implements AutoCloseable {
    private final String name;
    private final String path;
    private final String componentClassName;
    private WeakReference<PluginClassLoader> clsLoader;
    private WeakReference<Class<Component>> componentClass;
    private WeakReference<Component> component;
    private final AtomicBoolean closed = new AtomicBoolean();

    private synchronized Class<Component> getComponentClass() throws ClassNotFoundException {
        if (componentClass == null) {
            if (clsLoader == null) {
                clsLoader = new WeakReference<>(new PluginClassLoader(name, findJars()));
            }
            componentClass = new WeakReference<>(
                    clsLoader.get().loadClass(componentClassName).asSubclass(Component.class));
        }
        return componentClass.get();
    }

    private synchronized PluginClassLoader getPluginClassLoader() {
        if (clsLoader == null) {
            clsLoader = new WeakReference<>(new PluginClassLoader(name, findJars()));
        }
        return clsLoader.get();
    }

    private URL[] findJars() {
        File dir = new File(path);
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.endsWith(".jar");
            }
        });
        if (files == null)
            return new URL[0];
        List<URL> urls = new ArrayList<>();
        for (File file : files) {
            try {
                urls.add(file.toURI().toURL());
            } catch (Exception e) {
                log.error("Plugin {} add jar {} failed", name, file.getName(), e);
            }
        }
        return urls.toArray(new URL[0]);
    }

    public Component getComponent() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (unloaded())
            reset();
        if (closed.get())
            throw new IllegalStateException("Plugin " + name + " is closed but not unloaded");
        if (component == null) {
            component = new WeakReference<>(getComponentClass().getDeclaredConstructor().newInstance());
        }
        return component.get();
    }

    @Override
    public synchronized void close() {
        if (closed.get())
            return;

        log.info("Closing {}", this);
        if (clsLoader != null) {
            try {
                clsLoader.get().close();
            } catch (Exception e) {
                log.error("Plugin {} close class loader failed", this, e);
            }
        }
        log.info("Plugin {} closed, unloaded: {}", this, unloaded());
        closed.set(true);
    }

    public synchronized void unload() {
        close();
        System.gc();
        log.info("After a full gc, Plugin {} unloaded, unloaded: {}", this, unloaded());
    }

    private boolean unloaded() {
        return closed.get() && clsLoader != null && clsLoader.get() == null
                && componentClass != null && componentClass.get() == null
                && component != null && component.get() == null;
    }

    private void reset() {
        clsLoader = null;
        componentClass = null;
        component = null;
        closed.set(false);
    }

    @Override
    public String toString() {
        return "Plugin{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", componentClassName='" + componentClassName + '\'' +
                ", hashCode=" + hashCode() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plugin plugin = (Plugin) o;
        return Objects.equals(name, plugin.name) && Objects.equals(path, plugin.path)
                && Objects.equals(componentClassName, plugin.componentClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, componentClassName);
    }
}
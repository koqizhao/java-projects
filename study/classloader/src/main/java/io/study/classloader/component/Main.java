package io.study.classloader.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello, world!");

        List<Plugin> plugins = resolvePlugins(args);
        ComponentManager manager = new ComponentManager();
        run(manager, plugins);
        run(manager, plugins);

        System.out.println("\nPress any key to exit.");
        System.in.read();
        System.out.println();
    }

    private static void run(ComponentManager componentManager, List<Plugin> plugins) {
        System.out.println();
        for (Plugin plugin : plugins) {
            componentManager.load(plugin);
        }
        componentManager.run();
        for (Plugin plugin : plugins) {
            componentManager.unload(plugin);
        }
    }

    private static List<Plugin> resolvePlugins(String[] args) {
        List<Plugin> plugins = new ArrayList<>();
        Arrays.asList(args).forEach(arg -> {
            String[] parts = arg.split(":");
            plugins.add(new Plugin(parts[0], parts[1], parts[2]));
        });
        return plugins;
    }
}

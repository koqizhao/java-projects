package io.study.dubbo.starter;

import java.util.concurrent.CountDownLatch;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.facade.ConfigurationManagers;
import org.mydotey.scf.facade.StringProperties;
import org.mydotey.scf.facade.StringPropertySources;
import org.mydotey.scf.source.stringproperty.propertiesfile.PropertiesFileConfigurationSource;
import org.mydotey.scf.source.stringproperty.propertiesfile.PropertiesFileConfigurationSourceConfig;

import io.study.dubbo.starter.client.Client;
import io.study.dubbo.starter.server.Server;

public class App {

    private static StringProperties properties;

    private static RegistryConfig registryConfig;

    public static void main(String[] args) throws Exception {
        initConfig();

        // 1 app 1 app config
        new ApplicationConfig("first-dubbo-app").addIntoConfigManager();
        new MonitorConfig().addIntoConfigManager();

        initRegistryConfig();
        startServer();
        startClient();

        new CountDownLatch(1).await();
    }

    private static void initConfig() {
        PropertiesFileConfigurationSourceConfig sourceConfig = StringPropertySources
            .newPropertiesFileSourceConfigBuilder().setName("app").setFileName("app.properties").build();
        PropertiesFileConfigurationSource source = StringPropertySources.newPropertiesFileSource(sourceConfig);
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder()
            .setName("app-manager").addSource(1, source).build();
        ConfigurationManager manager = ConfigurationManagers.newManager(managerConfig);
        properties = new StringProperties(manager);
    }
    
    private static void initRegistryConfig() {
        String zookeeperAddress = properties.getStringPropertyValue("zookeeper.address");
        registryConfig = new RegistryConfig(zookeeperAddress, "zookeeper");

        /*
        String nacosAddress = properties.getStringPropertyValue("nacos.address");
        registryConfig = new RegistryConfig(zookeeperAddress, "nacos");
        */
    }

    public static void startServer() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                Server server = new Server(registryConfig);
                server.start();
            } catch (Exception e) {
                System.err.println("Server stopped!");
            }
        });
        thread.setDaemon(true);
        thread.setName("dubbo-starter-server");
        thread.start();
        System.out.println("Server started.");
    }

    public static void startClient() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                Client client = new Client(registryConfig);
                while (true) {
                    client.invoke();
                    Thread.sleep(1 * 1000);
                }
            } catch (Exception e) {
                System.err.println("Client stopped!");
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.setName("dubbo-starter-client");
        thread.start();
        System.out.println("Client started.");
    }

}

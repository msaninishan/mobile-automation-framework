package com.nishan.mobile.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static Properties properties;

    // Step 1 - private constructor
    // Why? So nobody can do new ConfigManager() from outside
    private ConfigManager() {
        loadProperties();
    }

    // The inner class is the "locked inner room"
    // JVM only loads this class when getInstance() is first called
    // JVM class loading is GUARANTEED thread safe - no synchronized needed
    private static class ConfigManagerHolder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return ConfigManagerHolder.INSTANCE;
    }

    // Step 3 - load the right file based on platform
    private void loadProperties()  {
        properties = new Properties();
        // loads base config first
        try (InputStream loadedConfig = getClass().getClassLoader().getResourceAsStream("config.properties")) {

            if (loadedConfig == null) {
                throw new RuntimeException("config.properties not found");
            }

            properties.load(loadedConfig);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties", e);
        }

    }

    // Step 4 - the method everyone calls
    public String get(String key) {
        String value = System.getProperty(key,
                properties.getProperty(key));
        return value != null ? value.trim() : null;
    }
}
package com.oopsjpeg.snubot;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    public static final String TOKEN = "token";
    public static final String PREFIX = "prefix";

    private static final Properties DEFAULTS = new Properties();

    static {
        DEFAULTS.put(TOKEN, "");
        DEFAULTS.put(PREFIX, "s!");
    }

    private final Properties properties = new Properties();

    public Settings() {
        properties.putAll(DEFAULTS);
    }

    public void load(FileReader fr) throws IOException {
        properties.load(fr);
    }

    public void store(FileWriter fw) throws IOException {
        properties.store(fw, "Snubot Properties");
    }

    public String get(String key) {
        return properties.getProperty(key, "");
    }

    public void put(String key, String value) {
        properties.put(key, value);
    }
}

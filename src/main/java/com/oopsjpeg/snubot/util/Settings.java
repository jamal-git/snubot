package com.oopsjpeg.snubot.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Settings
{
    public static final String TOKEN = "token";
    public static final String PREFIX = "prefix";
    public static final String MONGO_HOST = "mongo_host";
    public static final String MONGO_DATABASE = "mongo_database";

    private static final Properties DEFAULTS = new Properties();

    static
    {
        DEFAULTS.put(TOKEN, "");
        DEFAULTS.put(PREFIX, "s!");
        DEFAULTS.put(MONGO_HOST, "localhost");
        DEFAULTS.put(MONGO_DATABASE, "snubot");
    }

    private final Properties properties = new Properties();

    public void load(FileReader fr) throws IOException
    {
        properties.load(fr);
    }

    public void store(FileWriter fw) throws IOException
    {
        properties.store(fw, "Snubot Properties");
    }

    public String get(String key)
    {
        return get(key, DEFAULTS.getProperty(key, ""));
    }

    public String get(String key, String defaultValue)
    {
        return properties.getProperty(key, defaultValue);
    }

    public void put(String key, String value)
    {
        properties.put(key, value);
    }
}

package org.synogen.ftlautosave;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.logging.LogManager;

public class App
{
    public static Config config;


    public static void main( String[] args ) throws IOException {
        InputStream logConfig = App.class.getClassLoader().getResourceAsStream("logging.properties");
        LogManager.getLogManager().readConfiguration(logConfig);

        ObjectMapper jackson = new ObjectMapper();
        File configfile = Paths.get("config.json").toFile();
        if (!configfile.exists()) {
            jackson.writeValue(configfile, new Config());
        }

        config = jackson.readValue(configfile, Config.class);

        for (String filename : config.getFiles()) {
            new FileWatch(filename).start();
        }
    }
}

package org.synogen.ftlautosave;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App {
    public static Config config;

    public static Logger log;

    public static void main( String[] args ) throws IOException {
        // init logger
        InputStream logConfig = App.class.getClassLoader().getResourceAsStream("logging.properties");
        LogManager.getLogManager().readConfiguration(logConfig);
        log = Logger.getLogger("ftlautosave");

        // load app configuration
        config = Config.fromFile("config.json");
        log.info("Using " + config.getFtlSavePath() + " as FTL save path");
        for (String filename : config.getFiles()) {
            new FileWatch(filename).start();
        }
    }
}

package org.synogen.ftlautosave;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class App
{
    public static void main( String[] args ) throws IOException {
        InputStream logConfig = App.class.getClassLoader().getResourceAsStream("logging.properties");
        LogManager.getLogManager().readConfiguration(logConfig);

        new FileWatch(Config.PROFILE_FILE).start();
        new FileWatch(Config.SAVE_FILE).start();
    }
}

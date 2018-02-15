package org.synogen.ftlautosave;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App extends Application {
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

        new FileWatch(config.getSavefile()).start();
        new FileWatch(config.getProfile()).start();

        //launch UI
        Application.launch(App.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(App.class.getClassLoader().getResource("main.fxml"));

        stage.setTitle("ftlautosave");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        App.log.info("Exiting");
        System.exit(0);
    }
}

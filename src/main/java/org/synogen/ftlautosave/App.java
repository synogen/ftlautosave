package org.synogen.ftlautosave;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.synogen.ftlautosave.ui.MainController;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App extends Application {

    private static final String CONFIG_FILE = "config.json";

    public static Config config;

    public static Logger log;

    public static FileWatch saveWatcher;

    public static FileWatch profileWatcher;

    public static void main( String[] args ) throws IOException {
        //launch application
        Application.launch(App.class, args);
    }

    private MainController mainController;

    @Override
    public void init() throws Exception {
        // init logger
        InputStream logConfig = App.class.getClassLoader().getResourceAsStream("logging.properties");
        LogManager.getLogManager().readConfiguration(logConfig);
        log = Logger.getLogger("ftlautosave");

        // load app configuration
        config = Config.fromFile(CONFIG_FILE);

        // start watching files
        initWatchers();
    }

    public static void initWatchers() {
        log.info("Using " + config.getFtlSavePath() + " as FTL save path");

        if (saveWatcher != null) {
            saveWatcher.interrupt();
        }
        if (profileWatcher != null) {
            profileWatcher.interrupt();
        }
        saveWatcher = new FileWatch(config.getSavefile());
        profileWatcher = new FileWatch(config.getProfile());
        saveWatcher.start();
        profileWatcher.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getClassLoader().getResource("main.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();

        stage.setTitle("ftlautosave");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        mainController.saveConfiguration();
        config.toFile(CONFIG_FILE);
        App.log.info("Exiting");
        System.exit(0);
    }
}

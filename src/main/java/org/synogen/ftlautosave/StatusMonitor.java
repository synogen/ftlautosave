package org.synogen.ftlautosave;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StatusMonitor extends Thread{

    private Circle profileIndicator;
    private Circle saveIndicator;
    private Circle runPathIndicator;
    private Label profileStatus;
    private Label saveStatus;
    private Label runPathStatus;



    public StatusMonitor(Circle profileIndicator, Circle saveIndicator, Circle runPathIndicator, Label profileStatus, Label saveStatus, Label runPathStatus) {
        this.profileIndicator = profileIndicator;
        this.saveIndicator = saveIndicator;
        this.runPathIndicator = runPathIndicator;
        this.profileStatus = profileStatus;
        this.saveStatus = saveStatus;
        this.runPathStatus = runPathStatus;
    }

    @Override
    public void run() {
        App.log.info("Status monitor starting");
        while (true) {
            try {
                if (App.profileWatcher.isAlive()) {
                    if (App.profileWatcher.isWatching()) {
                        Platform.runLater(() -> {
                            profileIndicator.setFill(Color.GREEN);
                            profileStatus.setText("Profile file is being monitored for changes");
                        });
                    } else {
                        Platform.runLater(() -> {
                            profileIndicator.setFill(Color.RED);
                            profileStatus.setText("Profile file is not being monitored for changes");
                        });
                    }
                }
                sleep(300);

                if (App.saveWatcher.isAlive()) {
                    if (App.saveWatcher.isWatching()) {
                        Platform.runLater(() -> {
                            saveIndicator.setFill(Color.GREEN);
                            saveStatus.setText("Save file is being monitored for changes");
                        });
                    } else {
                        Platform.runLater(() -> {
                            saveIndicator.setFill(Color.RED);
                            saveStatus.setText("Save file is not being monitored for changes");
                        });
                    }
                }
                sleep(300);

                Path runPath = Paths.get(App.config.getFtlRunPath());
                if (Files.isExecutable(runPath)) {
                    Platform.runLater(() -> {
                        runPathIndicator.setFill(Color.GREEN);
                        runPathStatus.setText("Run path file is valid");
                    });
                } else {
                    Platform.runLater(() -> {
                        runPathIndicator.setFill(Color.RED);
                        runPathStatus.setText("Run path file does not exist or is not executable");
                    });
                }
                sleep(300);
            } catch (InterruptedException e) {
                App.log.info("Status monitor exiting");
            }
        }
    }
}

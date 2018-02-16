package org.synogen.ftlautosave.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.synogen.ftlautosave.App;
import org.synogen.ftlautosave.BackupSave;
import org.synogen.ftlautosave.Util;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainController {

    @FXML
    private ListView<BackupSave> savesList;

    @FXML
    private TextField savePath;

    @FXML
    private TextField runPath;

    @FXML
    public void initialize() throws IOException {
        savePath.setText(App.config.getFtlSavePath());
        runPath.setText(App.config.getFtlRunPath());

        refreshSavesList(null);
    }

    @FXML
    public void refreshSavesList(ActionEvent event) throws IOException {
        savesList.getItems().clear();
        Path savePath = Paths.get(App.config.getFtlSavePath());

        List<Path> savefilesList = new ArrayList<Path>();
        List<Path> profilesList = new ArrayList<Path>();

        if (Files.exists(savePath)) {
            DirectoryStream<Path> savefiles = Files.newDirectoryStream(savePath, entry -> {
                if (entry.getFileName().toString().startsWith(App.config.getSavefile() + ".")) {
                    return true;
                }
                return false;
            });
            DirectoryStream<Path> profiles = Files.newDirectoryStream(savePath, entry -> {
                if (entry.getFileName().toString().startsWith(App.config.getProfile() + ".")) {
                    return true;
                }
                return false;
            });

            //convert iterators to list due to error message saying they have the same iterator when looping one iterator inside of the other
            savefiles.forEach(savefilesList::add);
            profiles.forEach(profilesList::add);
        }

        for (Path savefile : savefilesList) {
            for (Path profile : profilesList) {
                //find a profile with a timestamp that matches closely
                Instant saveTime = Util.getTimestamp(savefile.getFileName().toString());
                Instant profileTime = Util.getTimestamp(profile.getFileName().toString());

                if (Math.abs(saveTime.until(profileTime, ChronoUnit.MILLIS)) < 500) {
                    savesList.getItems().add(new BackupSave(savefile, profile));
                }
            }
        }
        savesList.getItems().sort(Collections.reverseOrder(new SortBackupSaves()));
    }

    public void saveConfiguration() {
        App.config.setFtlSavePath(savePath.getText());
        App.config.setFtlRunPath(runPath.getText());
    }

    private class SortBackupSaves implements Comparator<BackupSave> {
        @Override
        public int compare(BackupSave o1, BackupSave o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    }

    @FXML
    private void restoreSave(ActionEvent event) throws IOException {
        BackupSave save = savesList.getSelectionModel().getSelectedItem();
        if (save != null) {
            Path savefile = Paths.get(App.config.getFtlSavePath() + "\\" + App.config.getSavefile());
            Path profile = Paths.get(App.config.getFtlSavePath() + "\\" + App.config.getProfile());
            App.log.info("Copying " + save.getSavefile().toString() + " to " + savefile.toString());
            Files.copy(save.getSavefile(), savefile, StandardCopyOption.REPLACE_EXISTING);
            App.log.info("Copying " + save.getProfile().toString() + " to " + profile.toString());
            Files.copy(save.getProfile(), profile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @FXML
    private void restoreAndStartFtl(ActionEvent event) throws IOException {
        restoreSave(event);

        App.log.info("Starting FTL");
        Path ftlRunPath = Paths.get(App.config.getFtlRunPath());
        Path ftlWorkingDirectory = ftlRunPath.getParent();

        new ProcessBuilder()
                .directory(ftlWorkingDirectory.toFile())
                .command(ftlRunPath.toString())
                .start();
    }

    @FXML
    private void applyConfig(ActionEvent event) throws IOException {
        saveConfiguration();

        App.initWatchers();

        refreshSavesList(event);
    }

}

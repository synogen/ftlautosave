package org.synogen.ftlautosave.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import org.synogen.ftlautosave.App;
import org.synogen.ftlautosave.DirectoryWatch;
import org.synogen.ftlautosave.StatusMonitor;
import org.synogen.ftlautosave.save.BackupSave;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MainController {

    @FXML
    private ListView<BackupSave> savesList;

    @FXML
    private TextField savePath;

    @FXML
    private TextField runPath;

    @FXML
    private Circle profileIndicator;
    @FXML
    private Circle saveIndicator;
    @FXML
    private Circle runPathIndicator;
    @FXML
    private Label profileStatus;
    @FXML
    private Label saveStatus;
    @FXML
    private Label runPathStatus;
    @FXML
    private CheckBox autoStartFtl;
    @FXML
    private CheckBox autoUpdateSnapshots;
    @FXML
    private CheckBox limitBackupSaves;
    @FXML
    private TitledPane snapshotsTitle;


    private StatusMonitor statusMonitor;
    private DirectoryWatch directoryWatch;
    private String fileSep;

    @FXML
    public void initialize() {
        savePath.setText(App.config.getFtlSavePath());
        runPath.setText(App.config.getFtlRunPath());
        autoStartFtl.setSelected(App.config.getAutoStartFtl());
        autoUpdateSnapshots.setSelected(App.config.getAutoUpdateSnapshots());
        limitBackupSaves.setSelected(App.config.getLimitBackupSaves());
        fileSep = System.getProperty("file.separator");

        resetUIWatchers();
    }

    public void resetUIWatchers() {
        if (statusMonitor != null && statusMonitor.isAlive()) {
            statusMonitor.interrupt();
        }
        if (directoryWatch != null && directoryWatch.isAlive()) {
            directoryWatch.interrupt();
        }
        statusMonitor = new StatusMonitor(profileIndicator, saveIndicator, runPathIndicator, profileStatus, saveStatus, runPathStatus);
        statusMonitor.start();
        directoryWatch = new DirectoryWatch(Paths.get(App.config.getFtlSavePath()), savesList, snapshotsTitle);
        if (App.config.getAutoUpdateSnapshots()) {
            directoryWatch.start();
        }
    }

    @FXML
    public void refreshSavesList(ActionEvent event) {
        directoryWatch.refreshSaves();
    }

    public void saveConfiguration() {
        App.config.setFtlSavePath(savePath.getText());
        App.config.setFtlRunPath(runPath.getText());
        App.config.setAutoStartFtl(autoStartFtl.isSelected());
        App.config.setAutoUpdateSnapshots(autoUpdateSnapshots.isSelected());
        App.config.setLimitBackupSaves(limitBackupSaves.isSelected());
    }

    @FXML
    private void restoreSave(ActionEvent event) throws IOException {
        BackupSave save = savesList.getSelectionModel().getSelectedItem();
        if (save != null) {
            Path savefile = Paths.get(App.config.getFtlSavePath() + fileSep + App.config.getSavefile());
            Path profile = Paths.get(App.config.getFtlSavePath() + fileSep + App.config.getProfile());
            App.log.info("Copying " + save.getSavefile().toString() + " to " + savefile.toString());
            Files.copy(save.getSavefile(), savefile, StandardCopyOption.REPLACE_EXISTING);
            App.log.info("Copying " + save.getProfile().toString() + " to " + profile.toString());
            Files.copy(save.getProfile(), profile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @FXML
    private void restoreAndStartFtl(ActionEvent event) throws IOException {
        restoreSave(event);

        startFtl();
    }

    @FXML
    private void applyConfig(ActionEvent event) throws IOException {
        saveConfiguration();

        App.initWatchers();

        resetUIWatchers();
    }

    public void startFtl() throws IOException {
        App.log.info("Starting FTL");
        Path ftlRunPath = Paths.get(App.config.getFtlRunPath());
        Path ftlWorkingDirectory = ftlRunPath.getParent();

        new ProcessBuilder()
                .directory(ftlWorkingDirectory.toFile())
                .command(ftlRunPath.toString())
                .inheritIO()
                .start();
    }
}

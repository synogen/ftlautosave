package org.synogen.ftlautosave.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.time.Instant;
import java.util.HashSet;

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
    @FXML
    private TextField maxNrOfBackupSaves;


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
        maxNrOfBackupSaves.setText(App.config.getMaxNrOfBackupSaves().toString());
        fileSep = System.getProperty("file.separator");

        // force the checkMaxNrOfBackupSaves field to be numeric only and have 4 digits max
        maxNrOfBackupSaves.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    newValue = newValue.replaceAll("[^\\d]", "");
                }
                newValue = newValue.length() > 4 ? newValue.substring(0, 4) : newValue;
                maxNrOfBackupSaves.setText(newValue);
            }
        });

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
        if(directoryWatch != null)
        {
            directoryWatch = new DirectoryWatch(Paths.get(App.config.getFtlSavePath()), directoryWatch.markedSaves, savesList, snapshotsTitle);
        }
        else{
            directoryWatch = new DirectoryWatch(Paths.get(App.config.getFtlSavePath()), savesList, snapshotsTitle);
        }
        if (App.config.getAutoUpdateSnapshots()) {
            directoryWatch.start();
        }else{
            directoryWatch.refreshSaves();
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
        App.config.setMaxNrOfBackupSaves(checkMaxNrOfBackupSaves(maxNrOfBackupSaves.getText()));
    }

    // do not accept empty values or (accidental?) values below 10 which may delete too many saves
    private Integer checkMaxNrOfBackupSaves(String newValue)
    {
        if(newValue == null || newValue.isEmpty()){
            return 10;
        }
        Integer newIntVal = Integer.parseInt(newValue);
        newIntVal = newIntVal < 10 ? 10 : newIntVal;
        // also update value in GUI to inform user
        maxNrOfBackupSaves.setText(newIntVal.toString());
        return newIntVal;
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
    private void markSaveAndRefresh(ActionEvent event) throws IOException {
        BackupSave save = savesList.getSelectionModel().getSelectedItem();
        if (save != null) {
            if(directoryWatch.markedSaves.contains(save.getTimestamp())){
                directoryWatch.markedSaves.remove(save.getTimestamp());
            }else{
                directoryWatch.markedSaves.add(save.getTimestamp());
            }
        }
        directoryWatch.refreshSaves();
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

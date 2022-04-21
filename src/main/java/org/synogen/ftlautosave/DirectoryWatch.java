package org.synogen.ftlautosave;

import javafx.application.Platform;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import org.synogen.ftlautosave.save.BackupSave;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Thread for keeping the saves list up to date and to purge old files if needed
 */
public class DirectoryWatch extends Thread {

    private Path savePath;
    private ListView<BackupSave> savesList;
    private TitledPane title;
    public HashSet<Instant> markedSaves;

    public DirectoryWatch(Path savePath, ListView<BackupSave> savesList, TitledPane title) {
        this.savePath = savePath;
        this.savesList = savesList;
        this.title = title;
        this.markedSaves = new HashSet<>();

        this.setPriority(Thread.MIN_PRIORITY);
    }

    public DirectoryWatch(Path savePath, HashSet<Instant> markedSaves, ListView<BackupSave> savesList, TitledPane title) {
        this(savePath, savesList, title);
        this.markedSaves = markedSaves;
    }

    @Override
    public void run() {
        App.log.info("Snapshot list updater starting");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                refreshSaves();
                sleep(3000);
            } catch (InterruptedException e) {
                App.log.info("Snapshot list updater exiting");
                Thread.currentThread().interrupt();
            }
        }
    }

    public void refreshSaves() {
        ArrayList<BackupSave> saves = new ArrayList<>();

        Path savePath = Paths.get(App.config.getFtlSavePath());

        List<Path> savefilesList = new ArrayList<>();
        List<Path> profilesList = new ArrayList<>();

        if (Files.exists(savePath)) {
            try {
                DirectoryStream<Path> savefiles = Files.newDirectoryStream(savePath, entry -> {
                    if (entry.getFileName().toString().startsWith(App.config.getSavefile() + ".") && entry.getFileName().toString().matches(".*\\d+$")) {
                        return true;
                    }
                    return false;
                });
                DirectoryStream<Path> profiles = Files.newDirectoryStream(savePath, entry -> {
                    if (entry.getFileName().toString().startsWith(App.config.getProfile() + ".") && entry.getFileName().toString().matches(".*\\d+$")) {
                        return true;
                    }
                    return false;
                });

                //convert iterators to list due to error message saying they have the same iterator when looping one iterator inside of the other
                savefiles.forEach(savefilesList::add);
                profiles.forEach(profilesList::add);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        for (Path savefile : savefilesList) {
            for (Path profile : profilesList) {
                //find a profile with a timestamp that matches closely
                Instant saveTime = Util.getTimestamp(savefile.getFileName().toString());
                Instant profileTime = Util.getTimestamp(profile.getFileName().toString());

                if (Math.abs(saveTime.until(profileTime, ChronoUnit.MILLIS)) < 500) {
                    saves.add(new BackupSave(savefile, profile));
                }
            }
        }
        saves.sort(Collections.reverseOrder(new Util.SortBackupSaves()));

        if (App.config.getLimitBackupSaves() && saves.size() > App.config.getMaxNrOfBackupSaves()) {
            App.log.info(App.config.getMaxNrOfBackupSaves() + " save snapshots exceeded, deleting oldest files");
            List<BackupSave> purgeList = saves.subList(App.config.getMaxNrOfBackupSaves() - 1, saves.size() - 1);
            for (BackupSave save : purgeList) {
                save.deleteFiles();
            }
            purgeList.clear();
        }

        Platform.runLater(() -> {
            savesList.getItems().clear();
            savesList.getItems().addAll(saves);
            savesList.setCellFactory(param -> new ListCell<BackupSave>() {
                @Override
                protected void updateItem(BackupSave item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle(null);
                    } else if(markedSaves.contains(item.getTimestamp())) {
                        setText(item.toString());
                        // default accent is 0099CE, selected FFFF00, blended to midpoint 80CC67
                        // so user knows that a marked save is selected
                        setStyle("-fx-accent: #80CC67; -fx-control-inner-background: #FFFF00;");
                    }else{
                        setText(item.toString());
                        setStyle(null);
                    }
                }
            });
            title.setText("FTL save snapshots (" + savesList.getItems().size() + ")");
        });
    }
}

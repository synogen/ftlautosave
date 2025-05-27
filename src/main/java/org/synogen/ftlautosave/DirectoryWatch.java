package org.synogen.ftlautosave;

import javafx.application.Platform;
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
import java.util.List;

/**
 * Thread for keeping the saves list up to date and to purge old files if needed
 */
public class DirectoryWatch extends Thread {

    private Path savePath;
    private ListView<BackupSave> savesList;
    private TitledPane title;

    public DirectoryWatch(Path savePath, ListView<BackupSave> savesList, TitledPane title) {
        this.savePath = savePath;
        this.savesList = savesList;
        this.title = title;

        this.setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void run() {
        App.log.info("Snapshot list updater starting");

        while (true) {
            try {
                refreshSaves();

                sleep(3000);
            } catch (InterruptedException e) {
                App.log.info("Snapshot list updater exiting");
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
        saves.sort(new Util.SortBackupSaves());

        if (App.config.getLimitBackupSaves() && saves.size() > 500) {
            App.log.info("500 save snapshots exceeded, deleting oldest files");
            List<BackupSave> purgeList = saves.subList(500 - 1, saves.size() - 1);
            for (BackupSave save : purgeList) {
                save.deleteFiles();
            }
            purgeList.clear();
        }

        Platform.runLater(() -> {
            // add only new saves
            saves.forEach(s -> {
                if (!savesList.getItems().contains(s)) savesList.getItems().add(0, s);
            });
            // remove deleted ones
            List toRemove = new ArrayList();
            savesList.getItems().forEach(listItem -> {
                if (!saves.contains(listItem)) toRemove.add(listItem);
            });
            savesList.getItems().removeAll(toRemove);

            title.setText("Snapshots (" + savesList.getItems().size() + ")");
        });
    }
}

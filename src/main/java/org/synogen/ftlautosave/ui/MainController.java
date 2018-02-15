package org.synogen.ftlautosave.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.synogen.ftlautosave.App;
import org.synogen.ftlautosave.BackupSave;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;

public class MainController {

    @FXML
    private ListView<BackupSave> savesList;

    @FXML
    public void initialize() throws IOException {
        Path savePath = Paths.get(App.config.getFtlSavePath());
        DirectoryStream<Path> directory = Files.newDirectoryStream(savePath, entry -> {
            for (String savefile : App.config.getFiles()) {
                if (entry.getFileName().toString().startsWith(savefile + ".")) {
                    return true;
                }
            }
            return false;
        });

        for (Path entry : directory) {
            savesList.getItems().add(new BackupSave(entry, null));
        }
        savesList.getItems().sort(Collections.reverseOrder(new SortBackupSaves()));
    }

    private class SortBackupSaves implements Comparator<BackupSave> {

        @Override
        public int compare(BackupSave o1, BackupSave o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    }

    @FXML
    private void restoreAndStartFtl(ActionEvent event) {

    }

}

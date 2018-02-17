package org.synogen.ftlautosave;

import lombok.Data;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Data
public class BackupSave {

    private Path savefile;
    private Path profile;
    private Instant timestamp;
    private FtlSaveFile saveContent;

    public BackupSave(Path savefile, Path profile) {
        this.savefile = savefile;
        this.profile = profile;

        String filename = savefile.getFileName().toString();
        Integer split = filename.lastIndexOf(".");
        Long milliseconds = Long.valueOf(filename.substring(split + 1));
        timestamp = Instant.ofEpochMilli(milliseconds);

        saveContent = new FtlSaveFile(savefile);
    }

    @Override
    public String toString() {
        Duration offset = Duration.of(ZonedDateTime.now().getOffset().getTotalSeconds(), ChronoUnit.SECONDS);
        return timestamp.plus(offset).toString() + " " + saveContent.getShipname(); //TODO add more info (preferably from the save itself)
    }

}

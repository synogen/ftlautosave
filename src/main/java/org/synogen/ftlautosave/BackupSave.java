package org.synogen.ftlautosave;

import lombok.Data;

import java.nio.file.Path;
import java.time.Instant;

@Data
public class BackupSave {

    public BackupSave(Path savefile, Path profile) {
        this.savefile = savefile;
        this.profile = profile;

        String filename = savefile.getFileName().toString();
        Integer split = filename.lastIndexOf(".");
        Long milliseconds = Long.valueOf(filename.substring(split + 1));
        timestamp = Instant.ofEpochMilli(milliseconds);

    }

    private Path savefile;
    private Path profile;
    private Instant timestamp;

    @Override
    public String toString() {
        return timestamp.toString(); //TODO add more info (preferably from the save itself)
    }

}

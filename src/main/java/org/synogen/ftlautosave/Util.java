package org.synogen.ftlautosave;

import org.synogen.ftlautosave.save.BackupSave;

import java.time.Instant;
import java.util.Comparator;

public class Util {
    public static Instant getTimestamp(String filename) {
        Integer split = filename.lastIndexOf(".");
        Long milliseconds = Long.valueOf(filename.substring(split + 1));
        return Instant.ofEpochMilli(milliseconds);
    }

    public static class SortBackupSaves implements Comparator<BackupSave> {
        @Override
        public int compare(BackupSave o1, BackupSave o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    }
}

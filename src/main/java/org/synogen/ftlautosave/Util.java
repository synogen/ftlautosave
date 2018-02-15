package org.synogen.ftlautosave;

import java.time.Instant;

public class Util {
    public static Instant getTimestamp(String filename) {
        Integer split = filename.lastIndexOf(".");
        Long milliseconds = Long.valueOf(filename.substring(split + 1));
        return Instant.ofEpochMilli(milliseconds);
    }
}

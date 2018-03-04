package org.synogen.ftlautosave;

import lombok.Data;

import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
        return formatter.format(timestamp) + " Save format version " + saveContent.getVersion() +
                (
                        saveContent.isInvalidFile() ?
                        "\n    " +
                        "! File could not be fully read, values might be missing or wrong !" : ""
                ) +
                "\n  " +
                "Ship " + saveContent.getShipname() +
                "\n    " +
                "Hull: " + saveContent.getHull() + "    " +
                "Fuel: " + saveContent.getFuel() + "    " +
                "Drone parts: " + saveContent.getDroneParts() + "    " +
                "Missiles: " + saveContent.getMissiles() + "    " +
                "Scrap: " + saveContent.getScrap() +
                "\n  " +
                "Stats" +
                "\n    " +
                "Total ships defeated: " + saveContent.getTotalShipsDefeated() + "\t\t" +
                "Total crew obtained: " + saveContent.getTotalCrewObtained() +
                "\n    " +
                "Total locations explored: " + saveContent.getTotalLocationsExplored() + "\t" +
                "Total scrap collected: " + saveContent.getTotalScrapCollected() +
                "\n-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -";

    }

}

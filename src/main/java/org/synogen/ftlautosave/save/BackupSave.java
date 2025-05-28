package org.synogen.ftlautosave.save;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.synogen.ftlautosave.App;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Data
@EqualsAndHashCode
public class BackupSave {

    private Path savefile;
    private Path profile;
    private Instant timestamp;
    @EqualsAndHashCode.Exclude private FtlSaveFile saveContent;

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
        return formatter.format(timestamp) + " Save format version " + saveContent.getVersion() + (saveContent.getSaveModifier().length() > 0? " (" + saveContent.getSaveModifier() + ")" : "") +
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

    public void deleteFiles() {
        try {
            String filename = this.savefile.toString();
            Files.delete(this.savefile);
            App.log.info("Deleted " + filename);
        } catch (FileNotFoundException e) {
            // OK
        } catch (IOException e) {
            App.log.info("Could not delete " + this.savefile.toString());
        }

        try {
            String filename = this.profile.toString();
            Files.delete(this.profile);
            App.log.info("Deleted " + filename);
        } catch (FileNotFoundException e) {
            // OK
        } catch (IOException e) {
            App.log.info("Could not delete " + this.profile.toString());
        }
    }
}

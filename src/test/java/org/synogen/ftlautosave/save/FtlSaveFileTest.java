package org.synogen.ftlautosave.save;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

class FtlSaveFileTest {

    @BeforeAll
    static void setupLogger() {
        org.synogen.ftlautosave.App.log = Logger.getLogger("test");
    }

    @Test
    void constructor_readsVersion(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("continue.sav");
        byte[] versionBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(9).array();
        byte[] moreBytes = new byte[200];
        System.arraycopy(versionBytes, 0, moreBytes, 0, 4);
        Files.write(file, moreBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        FtlSaveFile save = new FtlSaveFile(file);
        assertEquals(9, save.getVersion());
    }

    @Test
    void constructor_notFound_setsDefaults() {
        FtlSaveFile save = new FtlSaveFile(Path.of("does_not_exist.sav"));

        assertEquals(0, save.getVersion());
        assertEquals(0, save.getHull());
        assertEquals(0, save.getFuel());
        assertEquals(0, save.getDroneParts());
        assertEquals(0, save.getMissiles());
        assertEquals(0, save.getScrap());
        assertEquals(0, save.getTotalShipsDefeated());
        assertEquals(0, save.getTotalCrewObtained());
        assertEquals(0, save.getTotalLocationsExplored());
        assertEquals(0, save.getTotalScrapCollected());
        assertEquals("", save.getShipname());
        assertEquals("", save.getShiptype());
        assertEquals("", save.getSaveModifier());
    }

    @Test
    void constructor_unsupportedVersion_marksInvalid(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("continue.sav");
        byte[] versionBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(99).array();
        byte[] moreBytes = new byte[200];
        System.arraycopy(versionBytes, 0, moreBytes, 0, 4);
        Files.write(file, moreBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        FtlSaveFile save = new FtlSaveFile(file);
        assertTrue(save.isInvalidFile());
    }

    @Test
    void constructor_fieldsDefaultAfterError(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("continue.sav");
        byte[] versionBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(11).array();
        byte[] moreBytes = new byte[30];
        System.arraycopy(versionBytes, 0, moreBytes, 0, 4);
        Files.write(file, moreBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        FtlSaveFile save = new FtlSaveFile(file);

        assertEquals(11, save.getVersion());
        assertTrue(save.isInvalidFile());
        assertEquals(0, save.getHull());
        assertEquals(0, save.getFuel());
    }
}

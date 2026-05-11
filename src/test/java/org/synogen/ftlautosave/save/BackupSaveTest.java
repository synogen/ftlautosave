package org.synogen.ftlautosave.save;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

class BackupSaveTest {

    @BeforeAll
    static void setupLogger() {
        org.synogen.ftlautosave.App.log = Logger.getLogger("test");
    }

    @Test
    void constructor_parsesTimestampFromFilename(@TempDir Path tempDir) throws IOException {
        Path saveFile = tempDir.resolve("continue.sav.1712345678901");
        Path profileFile = tempDir.resolve("ae_prof.sav.1712345678901");
        Files.write(saveFile, new byte[]{0, 0, 0, 9}, StandardOpenOption.CREATE);
        Files.createFile(profileFile);

        BackupSave backup = new BackupSave(saveFile, profileFile);

        assertEquals(Instant.ofEpochMilli(1712345678901L), backup.getTimestamp());
    }

    @Test
    void constructor_setsSaveAndProfilePaths(@TempDir Path tempDir) throws IOException {
        Path saveFile = tempDir.resolve("continue.sav.1712345678901");
        Path profileFile = tempDir.resolve("ae_prof.sav.1712345678901");
        Files.write(saveFile, new byte[]{0, 0, 0, 9}, StandardOpenOption.CREATE);
        Files.createFile(profileFile);

        BackupSave backup = new BackupSave(saveFile, profileFile);

        assertEquals(saveFile, backup.getSavefile());
        assertEquals(profileFile, backup.getProfile());
    }

    @Test
    void constructor_handlesInvalidSaveFileGracefully(@TempDir Path tempDir) throws IOException {
        Path saveFile = tempDir.resolve("continue.sav.1712345678901");
        Path profileFile = tempDir.resolve("ae_prof.sav.1712345678901");
        Files.write(saveFile, new byte[]{0, 0, 0, 99}, StandardOpenOption.CREATE);
        Files.createFile(profileFile);

        BackupSave backup = new BackupSave(saveFile, profileFile);

        assertTrue(backup.getSaveContent().isInvalidFile());
        assertEquals(Instant.ofEpochMilli(1712345678901L), backup.getTimestamp());
    }

    @Test
    void constructor_throwsOnNonNumericFilename(@TempDir Path tempDir) {
        Path saveFile = tempDir.resolve("continue.sav.bad");
        Path profileFile = tempDir.resolve("ae_prof.sav.bad");

        assertThrows(NumberFormatException.class, () ->
                new BackupSave(saveFile, profileFile));
    }

    @Test
    void deleteFiles_deletesBothFiles(@TempDir Path tempDir) throws IOException {
        Path saveFile = tempDir.resolve("continue.sav.1712345678901");
        Path profileFile = tempDir.resolve("ae_prof.sav.1712345678901");
        Files.write(saveFile, new byte[]{0, 0, 0, 9}, StandardOpenOption.CREATE);
        Files.createFile(profileFile);

        BackupSave backup = new BackupSave(saveFile, profileFile);
        backup.deleteFiles();

        assertFalse(Files.exists(saveFile));
        assertFalse(Files.exists(profileFile));
    }

    @Test
    void deleteFiles_doesNotThrowOnAlreadyDeleted(@TempDir Path tempDir) throws IOException {
        Path saveFile = tempDir.resolve("continue.sav.1712345678901");
        Path profileFile = tempDir.resolve("ae_prof.sav.1712345678901");
        Files.write(saveFile, new byte[]{0, 0, 0, 9}, StandardOpenOption.CREATE);
        Files.createFile(profileFile);

        BackupSave backup = new BackupSave(saveFile, profileFile);
        Files.delete(saveFile);
        Files.delete(profileFile);

        assertDoesNotThrow(backup::deleteFiles);
    }

    @Test
    void toString_doesNotThrowOnInvalidContent(@TempDir Path tempDir) throws IOException {
        Path saveFile = tempDir.resolve("continue.sav.1712345678901");
        Path profileFile = tempDir.resolve("ae_prof.sav.1712345678901");
        Files.write(saveFile, new byte[]{0, 0, 0, 99}, StandardOpenOption.CREATE);
        Files.createFile(profileFile);

        BackupSave backup = new BackupSave(saveFile, profileFile);
        String result = backup.toString();

        assertNotNull(result);
        assertTrue(result.contains("File could not be fully read"));
        assertTrue(result.contains("Ship"));
        assertTrue(result.contains("Hull:"));
    }
}

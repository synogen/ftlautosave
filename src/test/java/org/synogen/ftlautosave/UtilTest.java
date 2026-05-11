package org.synogen.ftlautosave;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.synogen.ftlautosave.save.BackupSave;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

class UtilTest {

    @BeforeAll
    static void setupLogger() {
        org.synogen.ftlautosave.App.log = Logger.getLogger("test");
    }

    @Test
    void getTimestamp_parsesMillisecondSuffix() {
        Instant result = Util.getTimestamp("continue.sav.1712345678901");
        assertEquals(Instant.ofEpochMilli(1712345678901L), result);
    }

    @Test
    void getTimestamp_parsesProfileFile() {
        Instant result = Util.getTimestamp("ae_prof.sav.1712345678901");
        assertEquals(Instant.ofEpochMilli(1712345678901L), result);
    }

    @Test
    void getTimestamp_throwsOnNonNumericSuffix() {
        assertThrows(NumberFormatException.class, () ->
                Util.getTimestamp("continue.sav.abc123"));
    }

    @Test
    void getTimestamp_throwsOnEmptySuffix() {
        assertThrows(NumberFormatException.class, () ->
                Util.getTimestamp("continue.sav."));
    }

    @Test
    void getTimestamp_throwsOnNoSuffix() {
        assertThrows(NumberFormatException.class, () ->
                Util.getTimestamp("continue.sav"));
    }

    @Test
    void sortBackupSaves_comparesByTimestamp(@TempDir Path tempDir) throws IOException {
        Path saveEarly = tempDir.resolve("continue.sav.1000");
        Path profEarly = tempDir.resolve("ae_prof.sav.1000");
        Path saveLate = tempDir.resolve("continue.sav.2000");
        Path profLate = tempDir.resolve("ae_prof.sav.2000");
        Files.write(saveEarly, new byte[]{0, 0, 0, 9});
        Files.createFile(profEarly);
        Files.write(saveLate, new byte[]{0, 0, 0, 9});
        Files.createFile(profLate);

        BackupSave a = new BackupSave(saveEarly, profEarly);
        BackupSave b = new BackupSave(saveLate, profLate);

        Util.SortBackupSaves comparator = new Util.SortBackupSaves();
        assertTrue(comparator.compare(a, b) < 0);
        assertTrue(comparator.compare(b, a) > 0);
        assertEquals(0, comparator.compare(a, a));
    }
}

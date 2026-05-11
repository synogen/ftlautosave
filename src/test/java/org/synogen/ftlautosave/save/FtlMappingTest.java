package org.synogen.ftlautosave.save;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

class FtlMappingTest {

    private byte[] leInt(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }

    private byte[] leString(String s) {
        byte[] strBytes = s.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(4 + strBytes.length).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(strBytes.length);
        buf.put(strBytes);
        return buf.array();
    }

    private Path writeBytes(Path dir, byte[]... parts) throws IOException {
        Path file = dir.resolve("test.sav");
        byte[] all = new byte[0];
        for (byte[] p : parts) {
            byte[] tmp = java.util.Arrays.copyOf(all, all.length + p.length);
            System.arraycopy(p, 0, tmp, all.length, p.length);
            all = tmp;
        }
        Files.write(file, all, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return file;
    }

    @Test
    void parse_integers(@TempDir Path tempDir) throws IOException, FTLSaveFormatInvalid {
        Path file = writeBytes(tempDir, leInt(42), leInt(99));
        FtlMapping mapping = new FtlMapping(0, "ii");
        mapping.parse(file);
    }

    @Test
    void parse_namedIntegers(@TempDir Path tempDir) throws IOException, FTLSaveFormatInvalid {
        Path file = writeBytes(tempDir, leInt(10), leInt(20));
        FtlMapping mapping = new FtlMapping(0, "(a)i(b)i");
        mapping.parse(file);
        assertEquals(10, mapping.getInteger("a"));
        assertEquals(20, mapping.getInteger("b"));
    }

    @Test
    void parse_string(@TempDir Path tempDir) throws IOException, FTLSaveFormatInvalid {
        Path file = writeBytes(tempDir, leString("hello"));
        FtlMapping mapping = new FtlMapping(0, "s");
        mapping.parse(file);
    }

    @Test
    void parse_namedString(@TempDir Path tempDir) throws IOException, FTLSaveFormatInvalid {
        Path file = writeBytes(tempDir, leString("Kestrel"));
        FtlMapping mapping = new FtlMapping(0, "(shipname)s");
        mapping.parse(file);
        assertEquals("Kestrel", mapping.getString("shipname"));
    }

    @Test
    void parse_combinedNamed(@TempDir Path tempDir) throws IOException, FTLSaveFormatInvalid {
        Path file = writeBytes(tempDir, leInt(100), leString("Kestrel"), leInt(50));
        FtlMapping mapping = new FtlMapping(0, "(scrap)i(shipname)s(hull)i");
        mapping.parse(file);
        assertEquals(100, mapping.getInteger("scrap"));
        assertEquals("Kestrel", mapping.getString("shipname"));
        assertEquals(50, mapping.getInteger("hull"));
    }

    @Test
    void parse_offset(@TempDir Path tempDir) throws IOException, FTLSaveFormatInvalid {
        byte[] offsetBytes = leInt(9999);
        byte[] dataBytes = leInt(77);
        byte[] all = new byte[offsetBytes.length + dataBytes.length];
        System.arraycopy(offsetBytes, 0, all, 0, offsetBytes.length);
        System.arraycopy(dataBytes, 0, all, offsetBytes.length, dataBytes.length);
        Path file = tempDir.resolve("test.sav");
        Files.write(file, all, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        FtlMapping mapping = new FtlMapping(4, "(val)i");
        mapping.parse(file);
        assertEquals(77, mapping.getInteger("val"));
    }

    @Test
    void parse_fixedRepeat(@TempDir Path tempDir) throws IOException, FTLSaveFormatInvalid {
        Path file = writeBytes(tempDir, leInt(1), leInt(2), leInt(3));
        FtlMapping mapping = new FtlMapping(0, "[3i]");
        mapping.parse(file);
    }

    @Test
    void parse_variableRepeat(@TempDir Path tempDir) throws IOException, FTLSaveFormatInvalid {
        Path file = writeBytes(tempDir, leInt(2), leInt(10), leInt(20));
        FtlMapping mapping = new FtlMapping(0, "[xi]");
        mapping.parse(file);
    }

    @Test
    void parse_unknownVariableReturnsDefault() throws FTLSaveFormatInvalid, IOException {
        Path file = Path.of("nonexistent");
        FtlMapping mapping = new FtlMapping(0, "");
        assertEquals(0, mapping.getInteger("nope"));
        assertEquals("unknown", mapping.getString("nope"));
    }

    @Test
    void parse_negativeStringLengthThrows(@TempDir Path tempDir) throws IOException {
        Path file = writeBytes(tempDir, leInt(-1));
        FtlMapping mapping = new FtlMapping(0, "s");
        assertThrows(FTLSaveFormatInvalid.class, () -> mapping.parse(file));
    }

    @Test
    void parse_excessiveStringLengthThrows(@TempDir Path tempDir) throws IOException {
        Path file = writeBytes(tempDir, leInt(9999));
        FtlMapping mapping = new FtlMapping(0, "s");
        assertThrows(FTLSaveFormatInvalid.class, () -> mapping.parse(file));
    }

    @Test
    void parse_negativeRepeatCountThrows(@TempDir Path tempDir) throws IOException {
        Path file = writeBytes(tempDir, leInt(-5));
        FtlMapping mapping = new FtlMapping(0, "[xi]");
        assertThrows(FTLSaveFormatInvalid.class, () -> mapping.parse(file));
    }

    @Test
    void parse_excessiveRepeatCountThrows(@TempDir Path tempDir) throws IOException {
        Path file = writeBytes(tempDir, leInt(300));
        FtlMapping mapping = new FtlMapping(0, "[xi]");
        assertThrows(FTLSaveFormatInvalid.class, () -> mapping.parse(file));
    }

    @Test
    void parse_mixedNamedAndUnnamed(@TempDir Path tempDir) throws IOException, FTLSaveFormatInvalid {
        Path file = writeBytes(tempDir,
                leInt(100), leString("Kestrel"), leInt(30), leInt(15));
        FtlMapping mapping = new FtlMapping(0,
                "(scrap)i(shipname)s(hull)i(fuel)i");
        mapping.parse(file);
        assertEquals(100, mapping.getInteger("scrap"));
        assertEquals("Kestrel", mapping.getString("shipname"));
        assertEquals(30, mapping.getInteger("hull"));
        assertEquals(15, mapping.getInteger("fuel"));
    }
}

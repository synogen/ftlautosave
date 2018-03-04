package org.synogen.ftlautosave;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Data
public class FtlSaveFile {

    @Getter(AccessLevel.NONE)
    private Path path;

    private final Integer MAX_READ_BUFFER = 2048;

    private final Integer MAX_SKIP_TIMES = 256;

    private boolean invalidFile = false;

    private Integer version;

    private Integer totalShipsDefeated;
    private Integer totalLocationsExplored;
    private Integer totalScrapCollected;
    private Integer totalCrewObtained;

    private String shipname;
    private String shiptype;

    private Integer hull;
    private Integer fuel;
    private Integer droneParts;
    private Integer missiles;
    private Integer scrap;

    public FtlSaveFile(Path path) {
        this.path = path;

        try {
            SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ);

            version = readInteger(channel);

            channel.position(12);

            totalShipsDefeated = readInteger(channel);
            totalLocationsExplored = readInteger(channel);
            totalScrapCollected = readInteger(channel);
            totalCrewObtained = readInteger(channel);

            shipname = readNextString(channel);
            shiptype = readNextString(channel);

            skipStructure(channel, "ii");

            // state vars
            skipVariableStructures(channel, "si");

            // ship details
            skipStructure(channel,"sss");

            // start crew?
            skipVariableStructures(channel, "ss");

            // ??
            skipStructure(channel,"iiii");

            // ship values
            hull = readInteger(channel);
            fuel = readInteger(channel);
            droneParts = readInteger(channel);
            missiles = readInteger(channel);
            scrap = readInteger(channel);

            channel.close();
        } catch (IOException e) {
            App.log.info("Could not read savegame due to I/O exception");
        } catch (FTlSaveFormatInvalid fTlSaveFormatInvalid) {
            invalidFile = true;
            App.log.info(path.getFileName().toString() + " appears to contain an unsupported save format");
        }
    }

    /**
     * Reads the structure count first and then skips over that structure that many times
     * @param channel
     * @param structure
     * @throws IOException
     */
    private void skipVariableStructures(SeekableByteChannel channel, String structure) throws IOException, FTlSaveFormatInvalid {
        Integer times = readInteger(channel);
        if (times <= 0 || times > MAX_SKIP_TIMES) {
            throw new FTlSaveFormatInvalid();
        }
        skipStructureTimes(channel, structure, times);
    }

    /**
     * Skips over a structure
     * @param channel
     * @param structure structure string using 'i' for integer and 's' for string, so "iss" would read one integer and two variable length strings
     * @throws IOException
     */
    private void skipStructure(SeekableByteChannel channel, String structure) throws IOException, FTlSaveFormatInvalid {
        char[] struct = structure.toCharArray();
        for (int i = 0; i < struct.length; i++) {
            switch (struct[i]) {
                case 's': readNextString(channel); break;
                case 'i': readInteger(channel); break;
            }
        }
    }

    /**
     * Skips a given structure times 'times'
     * @param channel
     * @param structure
     * @param times
     * @throws IOException
     */
    private void skipStructureTimes(SeekableByteChannel channel, String structure, Integer times) throws IOException, FTlSaveFormatInvalid {
        for (int i = 0; i < times; i++) {
            skipStructure(channel, structure);
        }
    }

    /**
     * Reads a single four byte integer
     * @param channel
     * @return
     * @throws IOException
     */
    private Integer readInteger(SeekableByteChannel channel) throws IOException {
        ByteBuffer intValue = ByteBuffer.allocate(4);
        intValue.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(intValue);
        return intValue.getInt(0);
    }

    /**
     * Reads a variable length string by reading the length in the first four bytes, then reading length * bytes for the string value
     * @param channel
     * @return
     * @throws IOException
     */
    private String readNextString(SeekableByteChannel channel) throws IOException, FTlSaveFormatInvalid {
        Integer length = readInteger(channel);
        if (length <= 0 || length> MAX_READ_BUFFER) {
            throw new FTlSaveFormatInvalid();
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        channel.read(buffer);
        return new String(buffer.array());
    }

}

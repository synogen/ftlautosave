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

@Data
public class FtlSaveFile {

    @Getter(AccessLevel.NONE)
    private Path path;

    private Integer totalShipsDefeated;
    private Integer totalLocationsExplored;
    private Integer totalScrapCollected;
    private Integer totalCrewObtained;

    private String shipname;

    public FtlSaveFile(Path path) {
        this.path = path;

        try {
            SeekableByteChannel channel = Files.newByteChannel(path);

            channel.position(12);

            totalShipsDefeated = readInteger(channel);
            totalLocationsExplored = readInteger(channel);
            totalScrapCollected = readInteger(channel);
            totalCrewObtained = readInteger(channel);

            shipname = readNextString(channel);
        } catch (IOException e) {
            App.log.info("Unrecognized savegame format");
        }

    }

    private Integer readInteger(SeekableByteChannel channel) throws IOException {
        ByteBuffer intValue = ByteBuffer.allocate(4);
        intValue.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(intValue);
        return intValue.getInt(0);
    }

    private String readNextString(SeekableByteChannel channel) throws IOException {
        Integer length = readInteger(channel);
        ByteBuffer buffer = ByteBuffer.allocate(length);
        channel.read(buffer);
        return new String(buffer.array());
    }

}

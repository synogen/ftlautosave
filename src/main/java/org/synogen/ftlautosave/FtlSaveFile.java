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
import java.util.ArrayList;
import java.util.HashMap;

@Data
public class FtlSaveFile {

    @Getter(AccessLevel.NONE)
    private Path path;

    private final HashMap<Integer, FtlMapping> SUPPORTED_VERSIONS = new HashMap<>();
    {
        SUPPORTED_VERSIONS.put(9, new FtlMapping(12,
                "(totalShipsDefeated)i" +
                        "(totalLocationsExplored)i" +
                        "(totalScrapCollected)i" +
                        "(totalCrewObtained)i" +
                        "(shipname)s" +
                        "(shiptype)s" +
                        "ii[xsi]sss[xss]iiii" +
                        "(hull)i" +
                        "(fuel)i" +
                        "(droneParts)i" +
                        "(missiles)i" +
                        "(scrap)i"));
        SUPPORTED_VERSIONS.put(11, new FtlMapping(16,
                "(totalShipsDefeated)i" +
                        "(totalLocationsExplored)i" +
                        "(totalScrapCollected)i" +
                        "(totalCrewObtained)i" +
                        "(shipname)s" +
                        "(shiptype)s" +
                        "ii[xsi]sss[xss]iiii" +
                        "(hull)i" +
                        "(fuel)i" +
                        "(droneParts)i" +
                        "(missiles)i" +
                        "(scrap)i"));
    }
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
            channel.close();

            FtlMapping mapping = SUPPORTED_VERSIONS.get(version);
            mapping.parse(path);

            totalShipsDefeated = mapping.getInteger("totalShipsDefeated");
            totalLocationsExplored = mapping.getInteger("totalLocationsExplored");
            totalScrapCollected = mapping.getInteger("totalScrapCollected");
            totalCrewObtained = mapping.getInteger("totalCrewObtained");

            shipname = mapping.getString("shipname");
            shiptype = mapping.getString("shiptype");

            // ship values
            hull = mapping.getInteger("hull");
            fuel = mapping.getInteger("fuel");
            droneParts = mapping.getInteger("droneParts");
            missiles = mapping.getInteger("missiles");
            scrap = mapping.getInteger("scrap");

        } catch (IOException e) {
            App.log.info("Could not read savegame due to I/O exception");
        } catch (FTlSaveFormatInvalid fTlSaveFormatInvalid) {
            invalidFile = true;
            App.log.info(path.getFileName().toString() + " appears to contain an unsupported save format");
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

}

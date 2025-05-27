package org.synogen.ftlautosave.save;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.synogen.ftlautosave.App;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.logging.Level;

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
                        "ii[xsi]iiisss[xss]iiii" +
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
            version = FtlMapping.readInteger(channel);
            channel.close();

            FtlMapping mapping = SUPPORTED_VERSIONS.get(version);;
            if (!SUPPORTED_VERSIONS.containsKey(version)) {
                int distance = Integer.MAX_VALUE;
                for (Integer supported : SUPPORTED_VERSIONS.keySet()) {
                    if (Math.abs(supported - version) < distance) {
                        distance = Math.abs(supported - version);
                        mapping = SUPPORTED_VERSIONS.get(supported);
                    }
                }
            }

            try {
                mapping.parse(path);
            } catch (FTlSaveFormatInvalid fTlSaveFormatInvalid) {
                invalidFile = true;
                var variablesRead = mapping.variablesRead();
                App.log.log(Level.FINE, path.getFileName().toString() + " appears to contain an unsupported save format" + (variablesRead > 0? " (only " + variablesRead + " variables read)" : ""));
            }

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
        }
    }

}

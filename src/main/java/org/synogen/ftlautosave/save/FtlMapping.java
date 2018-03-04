package org.synogen.ftlautosave.save;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FtlMapping {

    private final Integer MAX_READ_BUFFER = 2048;

    private final Integer MAX_SKIP_TIMES = 256;

    private Integer startAt;
    private String structure;

    private HashMap<String, Object> variables = new HashMap<>();

    /**
     *
     * @param startAt At which offset to start reading the file
     * @param structure string definition of the file structure
     *                  s -> a string consisting of an integer for length and then the string itself
     *                  i -> an integer
     *                  [3si] -> a structure 'si' occuring 3 times
     *                  [xsi] -> a structure 'si' occuring x times, the structure is prefaced by an integer that gives the amount of times it occurs
     *                  (fuel)i -> map the next integer to the variable 'fuel'
     */
    public FtlMapping(Integer startAt, String structure) {
        this.startAt = startAt;
        this.structure = structure.replaceAll("\\s","");
    }

    public void parse(Path path) throws IOException, FTlSaveFormatInvalid {
        SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ);
        channel.position(startAt);

        parseStructure(channel, structure);
    }

    private void parseStructure(SeekableByteChannel channel, String structure) throws IOException, FTlSaveFormatInvalid {
        Pattern definition = Pattern.compile("(s)|(i)|(\\(.+?\\)[s|i])|(\\[(\\d+|x)(.+?)\\])");
        Matcher matcher = definition.matcher(structure);

        while (matcher.find()) {
            String match = matcher.group();
            if (match.matches("s")) {
                readNextString(channel);
            } else if (match.matches("i")) {
                readInteger(channel);
            } else if (match.matches("\\(.+\\)[s]")) {
                variables.put(match.substring(1, match.length()-2), readNextString(channel));
            } else if (match.matches("\\(.+\\)[i]")) {
                variables.put(match.substring(1, match.length()-2), readInteger(channel));
            } else if (match.matches("\\[([0-9]+|x).+?\\]")) {
                String number = matcher.group(5);
                String content = matcher.group(6);
                if (number.matches("x")) {
                    skipVariableStructures(channel, content);
                } else {
                    Integer numberI = Integer.valueOf(number);
                    skipStructureTimes(channel, content, numberI);
                }
                // TODO parse through so variable mappings work for substructures as well
                // parseStructure(channel, match.substring(1, match.length()-2));
            }
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
        if (times < 0 || times > MAX_SKIP_TIMES) {
            throw new FTlSaveFormatInvalid(channel.position() - 4, times);
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
    protected static Integer readInteger(SeekableByteChannel channel) throws IOException {
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
            throw new FTlSaveFormatInvalid(channel.position() - 4, length);
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        channel.read(buffer);
        return new String(buffer.array());
    }

    public Integer getInteger(String name) {
        if (variables.containsKey(name)) {
            return (Integer)variables.get(name);
        }
        return 0;
    }

    public String getString(String name) {
        if (variables.containsKey(name)) {
            return (String)variables.get(name);
        }
        return "unknown";
    }
}

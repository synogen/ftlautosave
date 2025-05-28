package org.synogen.ftlautosave.save;

public class FTLSaveFormatInvalid extends Throwable {

    public FTLSaveFormatInvalid(long position, Integer value) {
        super("Unexpected value " + value + " at position " + position);
    }
}

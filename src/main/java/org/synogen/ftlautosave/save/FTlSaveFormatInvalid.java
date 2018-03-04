package org.synogen.ftlautosave.save;

public class FTlSaveFormatInvalid extends Throwable {

    public FTlSaveFormatInvalid(long position, Integer value) {
        super("Unexpected value " + value + " at position " + position);
    }
}

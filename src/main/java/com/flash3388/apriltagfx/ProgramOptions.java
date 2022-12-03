package com.flash3388.apriltagfx;

import java.nio.file.Path;
import java.util.Optional;

public class ProgramOptions {

    private final Path mCustomNativesDir;

    public ProgramOptions(Path customNativesDir) {
        mCustomNativesDir = customNativesDir;
    }

    public Optional<Path> getCustomNativesDir() {
        return Optional.ofNullable(mCustomNativesDir);
    }
}

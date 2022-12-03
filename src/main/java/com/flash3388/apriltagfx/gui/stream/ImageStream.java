package com.flash3388.apriltagfx.gui.stream;

import org.opencv.core.Mat;

import java.util.function.Supplier;

public interface ImageStream extends Supplier<Mat>, AutoCloseable {

    @Override
    Mat get();

    @Override
    void close();
}

package com.flash3388.apriltagfx.gui.stream;

import org.opencv.core.Mat;

public class EmptyImage implements ImageStream {

    @Override
    public Mat get() {
        return null;
    }

    @Override
    public void close() {

    }
}

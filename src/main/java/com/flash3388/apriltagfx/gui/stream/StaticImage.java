package com.flash3388.apriltagfx.gui.stream;

import org.opencv.core.Mat;

public class StaticImage implements ImageStream {

    private final Mat mMat;

    public StaticImage(Mat mat) {
        mMat = mat;
    }

    @Override
    public Mat get() {
        return mMat;
    }

    @Override
    public void close() {
        mMat.release();
    }
}

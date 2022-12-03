package com.flash3388.apriltagfx.gui.stream;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraStream implements ImageStream {

    private final VideoCapture mVideoCapture;
    private final Mat mMat;

    public CameraStream(VideoCapture videoCapture) {
        mVideoCapture = videoCapture;
        mMat = new Mat();
    }

    @Override
    public Mat get() {
        mVideoCapture.read(mMat);
        return mMat;
    }

    @Override
    public void close() {
        mMat.release();
        mVideoCapture.release();
    }
}

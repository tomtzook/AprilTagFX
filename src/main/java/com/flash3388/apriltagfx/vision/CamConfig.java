package com.flash3388.apriltagfx.vision;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;

import java.util.ArrayList;
import java.util.List;

public class CamConfig {

    private final Mat mIntrinsicMatrix;
    private final MatOfDouble mDistCoefficients;

    private final List<Mat> mRVectors;
    private final List<Mat> mTVectors;

    public CamConfig(Mat intrinsicMatrix, MatOfDouble distCoefficients, List<Mat> rVectors, List<Mat> tVectors) {
        mIntrinsicMatrix = intrinsicMatrix;
        mDistCoefficients = distCoefficients;
        mRVectors = rVectors;
        mTVectors = tVectors;
    }

    public CamConfig() {
        this(new Mat(), new MatOfDouble(), new ArrayList<>(), new ArrayList<>());
    }

    public Mat getIntrinsicMatrix() {
        return mIntrinsicMatrix;
    }

    public MatOfDouble getDistCoefficients() {
        return mDistCoefficients;
    }

    public List<Mat> getRVectors() {
        return mRVectors;
    }

    public List<Mat> getTVectors() {
        return mTVectors;
    }

    public Mat getR() {
        return mRVectors.get(0);
    }

    public Mat getT() {
        return mTVectors.get(0);
    }
}

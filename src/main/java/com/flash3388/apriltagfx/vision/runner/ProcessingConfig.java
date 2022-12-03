package com.flash3388.apriltagfx.vision.runner;

import com.flash3388.apriltagfx.vision.CamConfig;
import com.flash3388.apriltags4j.DetectionInfo;
import com.flash3388.apriltags4j.FamilyType;
import org.opencv.core.Mat;

public class ProcessingConfig {

    private final CamConfig mCamConfig;
    private final double mTagSizeM;
    private final FamilyType mFamilyType;

    public ProcessingConfig(CamConfig camConfig, double tagSizeM, FamilyType familyType) {
        mCamConfig = camConfig;
        mTagSizeM = tagSizeM;
        mFamilyType = familyType;
    }

    public CamConfig getCameraConfig() {
        return mCamConfig;
    }

    public double getTagSizeMeters() {
        return mTagSizeM;
    }

    public DetectionInfo getDetectionInfo() {
        CamConfig camConfig = getCameraConfig();
        Mat intrinsicMat = camConfig.getIntrinsicMatrix();

        DetectionInfo detectionInfo = new DetectionInfo();
        detectionInfo.tagSize = getTagSizeMeters();
        detectionInfo.focalCenterX = intrinsicMat.get(0, 2)[0];
        detectionInfo.focalCenterY = intrinsicMat.get(1, 2)[0];
        detectionInfo.focalLengthX = intrinsicMat.get(0, 0)[0];
        detectionInfo.focalLengthY = intrinsicMat.get(1, 1)[0];
        return detectionInfo;
    }

    public FamilyType getFamily() {
        return mFamilyType;
    }
}

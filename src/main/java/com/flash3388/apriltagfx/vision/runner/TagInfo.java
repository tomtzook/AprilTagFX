package com.flash3388.apriltagfx.vision.runner;

import com.jmath.vectors.Vector3;

public class TagInfo {

    private final int mId;
    private final Vector3 mObjectCenter;

    public TagInfo(int id, Vector3 objectCenter) {
        mId = id;
        mObjectCenter = objectCenter;
    }

    public int getId() {
        return mId;
    }

    public double getDistanceM() {
        return mObjectCenter.magnitude();
    }

    public double getAzimuthDegrees() {
        return mObjectCenter.azimuth();
    }

    public double getInclinationDegrees() {
        return mObjectCenter.inclination();
    }

    public Vector3 getObjectCenter() {
        return mObjectCenter;
    }
}

package com.flash3388.apriltagfx.vision.calibrator;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;

public class Config {

    private final int mChessboardXSize;
    private final int mChessboardYSize;

    public Config(int chessboardXSize, int chessboardYSize) {
        mChessboardXSize = chessboardXSize;
        mChessboardYSize = chessboardYSize;
    }

    public Size getPatternSize() {
        return new Size(mChessboardXSize, mChessboardYSize);
    }

    public int getChessboardFlags() {
        return Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_FAST_CHECK + Calib3d.CALIB_CB_NORMALIZE_IMAGE;
    }

    public TermCriteria getTermCriteria() {
        return new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 30, 0.001);
    }
}

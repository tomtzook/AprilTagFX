package com.flash3388.apriltagfx.vision.calibrator;

import com.flash3388.apriltagfx.vision.CamConfig;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Calibrator {

    private final Config mConfig;

    public Calibrator(Config config) {
        mConfig = config;
    }

    public CamConfig calibrate(List<Path> imagePaths) throws IOException {
        List<Mat> imagePoints = new ArrayList<>();

        Size imageSize = null;
        for (Path path : imagePaths) {
            Mat img = Imgcodecs.imread(path.toAbsolutePath().toString());
            if (img.empty()) {
                throw new IOException("failed to load image");
            }

            Optional<MatOfPoint2f> optionalImgPoints = processImage(img);
            if (optionalImgPoints.isEmpty()) {
                continue;
            }

            MatOfPoint2f imgPoints = optionalImgPoints.get();
            imagePoints.add(imgPoints);

            imageSize = img.size();
        }

        List<Mat> objectPoints = createObjectPoints(imagePoints.size());

        CamConfig result = new CamConfig();
        double e = Calib3d.calibrateCamera(
                objectPoints,
                imagePoints,
                imageSize,
                result.getIntrinsicMatrix(),
                result.getDistCoefficients(),
                result.getRVectors(),
                result.getTVectors());
        return result;
    }

    private Optional<MatOfPoint2f> processImage(Mat img) {
        Mat grayscale = new Mat();
        Imgproc.cvtColor(img, grayscale, Imgproc.COLOR_BGR2GRAY);

        return findChessboardCorners(grayscale);
    }

    private Optional<MatOfPoint2f> findChessboardCorners(Mat img) {
        MatOfPoint2f corners = new MatOfPoint2f();
        boolean found = Calib3d.findChessboardCorners(
                img,
                mConfig.getPatternSize(),
                corners,
                mConfig.getChessboardFlags()
        );

        if (!found) {
            return Optional.empty();
        }

        Imgproc.cornerSubPix(img,
                corners,
                new Size(11, 11),
                new Size(-1, -1),
                mConfig.getTermCriteria());
        return Optional.of(corners);
    }

    private List<Mat> createObjectPoints(int count) {
        List<Mat> points = new ArrayList<>();
        Mat objectPoint = createObjectPoints();
        points.add(objectPoint);

        for (int i = 1; i < count; i++) {
            Mat mat = new Mat();
            objectPoint.copyTo(mat);
            points.add(mat);
        }

        return points;
    }

    private Mat createObjectPoints() {
        Size boardSize = mConfig.getPatternSize();
        Mat mat = Mat.zeros((int)(boardSize.width * boardSize.height), 1, CvType.CV_32FC3);
        double[] values = new double[(int)(boardSize.width * boardSize.height * 3)];
        int x = 0;
        int y = 0;

        for (int i = 0; i < values.length; i+=3) {
            values[i] = x;
            values[i+1] = y;
            values[i+2] = 0;

            x++;
            if (x == boardSize.width) {
                y++;
                x = 0;
            }
        }

        mat.put(0, 0, values);
        return mat;
    }
}

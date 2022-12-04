package com.flash3388.apriltagfx.vision.runner;

import com.flash3388.apriltagfx.vision.CamConfig;
import com.flash3388.apriltags4j.Detection;
import com.flash3388.apriltags4j.DetectionInfo;
import com.flash3388.apriltags4j.Detections;
import com.flash3388.apriltags4j.Detector;
import com.flash3388.apriltags4j.Family;
import com.flash3388.apriltags4j.FamilyType;
import com.flash3388.apriltags4j.TagPose;
import com.jmath.vectors.Vector3;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Processor implements AutoCloseable {

    private final Scalar[] COLORS = {
            new Scalar(0, 255, 0),
            new Scalar(255, 0, 0),
            new Scalar(0, 0, 255),
            new Scalar(255, 255, 0),
            new Scalar(255, 0, 255),
            new Scalar(0, 255, 255),
            new Scalar(50, 0, 0),
            new Scalar(0, 50, 0),
            new Scalar(0, 0, 50),
            new Scalar(50, 50, 0),
            new Scalar(50, 0, 50),
            new Scalar(0, 50, 50),
            new Scalar(100, 0, 0),
            new Scalar(0, 100, 0),
            new Scalar(0, 0, 100),
            new Scalar(100, 100, 0),
            new Scalar(100, 0, 100),
            new Scalar(0, 100, 100),
            new Scalar(200, 0, 0),
            new Scalar(0, 200, 0),
            new Scalar(0, 0, 200),
            new Scalar(200, 200, 0),
            new Scalar(200, 0, 200),
            new Scalar(0, 200, 200),
    };

    private final Detector mDetector;
    private Family mConfiguredFamily;
    private FamilyType mConfiguredFamilyType;

    private Map<Integer, Scalar> mAssignedColorMap;
    private Queue<Scalar> mAvailableColors;

    public Processor() {
        mDetector = new Detector();
        mConfiguredFamily = null;
        mConfiguredFamilyType = null;

        mAssignedColorMap = new HashMap<>();
        mAvailableColors = new ArrayDeque<>();
    }

    public Detector getDetector() {
        return mDetector;
    }

    public Result process(Mat img, ProcessingConfig config) throws Exception {
        if (!config.getFamily().equals(mConfiguredFamilyType)) {
            if (mConfiguredFamily != null) {
                mDetector.removeFamily(mConfiguredFamily);
                mConfiguredFamily.close();
            }

            mConfiguredFamilyType = config.getFamily();
            mConfiguredFamily = new Family(mConfiguredFamilyType);
            mDetector.addFamily(mConfiguredFamily);

            mAvailableColors.clear();
            mAvailableColors.addAll(Arrays.asList(COLORS));
            mAssignedColorMap.clear();
        }

        Mat grayscale = new Mat();
        Imgproc.cvtColor(img, grayscale, Imgproc.COLOR_BGR2GRAY);

        CamConfig camConfig = config.getCameraConfig();
        DetectionInfo detectionInfo = config.getDetectionInfo();
        Result result = new Result();

        try (Detections detections = mDetector.detect(grayscale)) {
            while (detections.hasNext()) {
                Detection detection = detections.next();

                TagPose pose = estimatePose(detection, detectionInfo);
                //Mat pose_R = convertMatCoordinateSystem(pose.R);
                //Mat pose_t = convertMatCoordinateSystem(pose.t);
                Point3 centerFromCamera = transposeCenter(camConfig, pose.R, pose.t);

                Vector3 objectCenterVec = new Vector3(centerFromCamera.x, centerFromCamera.y, centerFromCamera.z);
                result.put(new TagInfo(detection.id, objectCenterVec));

                Scalar color = mAssignedColorMap.get(detection.id);
                if (color == null) {
                    if (!mAvailableColors.isEmpty()) {
                        color = mAvailableColors.remove();
                    } else {
                        color = new Scalar(0, 0, 0);
                    }
                    mAssignedColorMap.put(detection.id, color);
                }

                // draw info
                pose.drawBoundingBox(img,
                        camConfig.getIntrinsicMatrix(),
                        camConfig.getDistCoefficients(),
                        detectionInfo.tagSizeMeters,
                        color);
                Imgproc.putText(
                        img,
                        String.valueOf(detection.id),
                        new Point(detection.centerX, detection.centerY),
                        Imgproc.FONT_ITALIC,
                        0.5,
                        color,
                        2
                );
            }
        }

        return result;
    }

    private TagPose estimatePose(Detection detection, DetectionInfo detectionInfo) {
        TagPose pose = detection.estimatePose(detectionInfo);
        Mat pose_R = new Mat();
        Calib3d.Rodrigues(pose.R, pose_R);

        return new TagPose(pose_R, pose.t, pose.error);
    }

    private static MatOfPoint2f projectPoints(MatOfPoint3f mat, CamConfig camConfig, Mat R, Mat t) {
        MatOfPoint2f imagePoints = new MatOfPoint2f();
        Calib3d.projectPoints(mat, R, t, camConfig.getIntrinsicMatrix(), camConfig.getDistCoefficients(), imagePoints);
        return imagePoints;
    }

    private static MatOfPoint2f projectPoints(MatOfPoint3f mat, CamConfig camConfig) {
        return projectPoints(mat, camConfig,
                Mat.zeros(3, 1, CvType.CV_64F),
                Mat.zeros(3, 1, CvType.CV_64F));
    }

    private static Mat convertCoordinateSystem(Mat mat, Mat R, Mat t) {
        Mat result = new Mat();
        Core.add(R.cross(mat), t, result);
        return result;
    }

    private static Mat camToWorld(Mat mat, CamConfig camConfig) {
        return convertCoordinateSystem(mat, camConfig.getR(), camConfig.getT());
    }

    private static Mat translateMat(Mat mat, Mat t) {
        Mat result = new Mat();
        Core.add(mat, t, result);
        return result;
    }

    private static MatOfPoint2f unprojectPoints(MatOfPoint2f points, CamConfig camConfig) {
        MatOfPoint2f undistortedPoints = new MatOfPoint2f();
        Calib3d.undistortPoints(points,
                undistortedPoints,
                camConfig.getIntrinsicMatrix(),
                camConfig.getDistCoefficients());

        Mat intrinsicMat = camConfig.getIntrinsicMatrix();
        double focalCenterX = intrinsicMat.get(0, 2)[0];
        double focalCenterY = intrinsicMat.get(1, 2)[0];
        double focalLengthX = intrinsicMat.get(0, 0)[0];
        double focalLengthY = intrinsicMat.get(1, 1)[0];

        MatOfPoint2f sub = new MatOfPoint2f();
        MatOfPoint2f div = new MatOfPoint2f();
        Core.repeat(new MatOfPoint2f(new Point(focalCenterX, focalCenterY)),
                undistortedPoints.rows(), undistortedPoints.cols(), sub);
        Core.repeat(new MatOfPoint2f(new Point(focalLengthX, focalLengthY)),
                undistortedPoints.rows(), undistortedPoints.cols(), div);

        MatOfPoint2f result1 = new MatOfPoint2f();
        MatOfPoint2f result2 = new MatOfPoint2f();
        Core.subtract(undistortedPoints, sub, result1);
        Core.divide(result1, div, result2);

        undistortedPoints.release();
        sub.release();
        div.release();
        result1.release();
        return result2;
    }

    private static Point getImageCenter(CamConfig camConfig) {
        MatOfPoint2f point = new MatOfPoint2f(new Point(0, 0));
        MatOfPoint2f result = unprojectPoints(point, camConfig);

        return new Point(result.get(0, 0));
    }

    private static Point3 transposeCenter(CamConfig camConfig, Mat R, Mat t) {
        Mat tagCenter = Mat.zeros(3, 1, CvType.CV_64F);
        Mat tagCenterInCamera = convertCoordinateSystem(tagCenter, R, t);
        //Mat tagCenterInWorld = camToWorld(tagCenterInCamera, camConfig);

        Mat result = tagCenterInCamera.t().reshape(3);
        return new Point3(result.get(0, 0));
        /*Point imageCenter = getImageCenter(camConfig);

        Mat result = tagCenterInCamera.t().reshape(3);
        Point3 p = new Point3(result.get(0, 0));
        p.x += imageCenter.x;
        p.y += imageCenter.y;
        return p;*/
    }

    /*private static Mat convertMatCoordinateSystem(Mat mat) {
        // opencv is X right, Y down, Z forward
        // we want X right, Y up, Z forward
        Mat result = new Mat();
        Core.multiply(mat, new Scalar(1, -1, 1), result);
        return result;
    }*/

    private static void drawBoxes(Mat img, Point[] points, Scalar color) {
        assert points.length == 8;

        Imgproc.line(img, points[0], points[1], color, 1);
        Imgproc.line(img, points[1], points[2], color, 1);
        Imgproc.line(img, points[2], points[3], color, 1);
        Imgproc.line(img, points[3], points[0], color, 1);

        Imgproc.line(img, points[4], points[5], color, 1);
        Imgproc.line(img, points[5], points[6], color, 1);
        Imgproc.line(img, points[6], points[7], color, 1);
        Imgproc.line(img, points[7], points[4], color, 1);

        Imgproc.line(img, points[0], points[4], color, 1);
        Imgproc.line(img, points[1], points[5], color, 1);
        Imgproc.line(img, points[2], points[6], color, 1);
        Imgproc.line(img, points[3], points[7], color, 1);
    }

    private static void makeBoundingBox(Mat img, CamConfig camConfig, Mat R, Mat t, double tagSize, Scalar color) {
        Mat opoints = new Mat(8, 1, CvType.CV_32FC3);
        opoints.put(0, 0,
                -1, -1, 0,
                1, -1, 0,
                1, 1, 0,
                -1, 1, 0,
                -1, -1, -2,
                1, -1, -2,
                1, 1, -2,
                -1, 1, -2);
        MatOfPoint3f objectPoints = new MatOfPoint3f();
        Core.multiply(opoints, Scalar.all(0.5 * tagSize), objectPoints);

        MatOfPoint2f imagePoints = projectPoints(objectPoints, camConfig, R, t);
        drawBoxes(img, imagePoints.toArray(), color);
    }

    @Override
    public void close() throws Exception {
        mDetector.clearFamilies();
        mDetector.close();

        if (mConfiguredFamily != null) {
            mConfiguredFamily.close();
        }
    }
}

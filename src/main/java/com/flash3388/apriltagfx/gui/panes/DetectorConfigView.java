package com.flash3388.apriltagfx.gui.panes;

import com.flash3388.apriltagfx.gui.ProcessingControl;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PropertySheet;

import java.util.List;

public class DetectorConfigView extends VBox {

    private final PropertySheet mPropertySheet;
    private DetectorConfig mDetectorConfig;

    public DetectorConfigView() {
        mPropertySheet = new PropertySheet();
        mPropertySheet.setMaxWidth(350);
        getChildren().add(mPropertySheet);
    }

    public void linkToDetector(ProcessingControl processingControl) {
        mDetectorConfig = new DetectorConfig(processingControl);

        List<PropertySheet.Item> items = List.of(
                new DetectorConfigItem(
                        "General",
                        "Number Of Threads",
                        "How many threads should be used?",
                        Integer.class,
                        mDetectorConfig,
                        (c)-> c.nthreads,
                        (c, v)-> {c.nthreads = (Integer) v; }
                ),
                new DetectorConfigItem(
                        "General",
                        "Detection of Quads",
                        "detection of quads can be done on a lower-resolution image,\n" +
                                "improving speed at a cost of pose accuracy and a slight\n" +
                                "decrease in detection rate. Decoding the binary payload is\n" +
                                "still done at full resolution. .",
                        Float.class,
                        mDetectorConfig,
                        (c)-> c.quadDecimate,
                        (c, v)-> {c.quadDecimate = (Float) v; }
                ),
                new DetectorConfigItem(
                        "General",
                        "Gaussian Blur",
                        "What Gaussian blur should be applied to the segmented image\n" +
                                "(used for quad detection?)  Parameter is the standard deviation\n" +
                                "in pixels.  Very noisy images benefit from non-zero values\n" +
                                "(e.g. 0.8).",
                        Float.class,
                        mDetectorConfig,
                        (c)-> c.quadSigma,
                        (c, v)-> {c.quadSigma = (Float) v; }
                ),
                new DetectorConfigItem(
                        "General",
                        "Refine Edges",
                        "When true, the edges of the each quad are adjusted to \"snap\n" +
                                "to\" strong gradients nearby. This is useful when decimation is\n" +
                                "employed, as it can increase the quality of the initial quad\n" +
                                "estimate substantially. Generally recommended to be on (true).\n" +
                                "Very computationally inexpensive. Option is ignored if\n" +
                                "quad_decimate = 1.",
                        Boolean.class,
                        mDetectorConfig,
                        (c)-> c.refineEdges,
                        (c, v)-> {c.refineEdges = (Boolean) v; }
                ),
                new DetectorConfigItem(
                        "General",
                        "Decode Sharpening",
                        "How much sharpening should be done to decoded images? This\n" +
                                "can help decode small tags but may or may not help in odd\n" +
                                "lighting conditions or low light conditions.",
                        Double.class,
                        mDetectorConfig,
                        (c)-> c.decodeSharpening,
                        (c, v)-> {c.decodeSharpening = (Double) v; }
                ),
                new DetectorConfigItem(
                        "General",
                        "Debug",
                        "When true, write a variety of debugging images to the\n" +
                                "current working directory at various stages through the\n" +
                                "detection process. (Somewhat slow).",
                        Boolean.class,
                        mDetectorConfig,
                        (c)-> c.debug,
                        (c, v)-> {c.debug = (Boolean) v; }
                ),
                new DetectorConfigItem(
                        "Quad Thresh",
                        "Min Cluster Pixels",
                        "Reject quads containing too few pixels",
                        Integer.class,
                        mDetectorConfig,
                        (c)-> c.quadThreshParams.minClusterPixels,
                        (c, v)-> {c.quadThreshParams.minClusterPixels = (Integer) v; }
                ),
                new DetectorConfigItem(
                        "Quad Thresh",
                        "Max Corner Candidates",
                        "how many corner candidates to consider when segmenting a group\n" +
                                "of pixels into a quad.",
                        Integer.class,
                        mDetectorConfig,
                        (c)-> c.quadThreshParams.maxNmaxima,
                        (c, v)-> {c.quadThreshParams.maxNmaxima = (Integer) v; }
                ),
                new DetectorConfigItem(
                        "Quad Thresh",
                        "Critical [Radians]",
                        "Reject quads where pairs of edges have angles that are close to\n" +
                                "straight or close to 180 degrees. Zero means that no quads are\n" +
                                "rejected. (In radians).",
                        Float.class,
                        mDetectorConfig,
                        (c)-> c.quadThreshParams.criticalRad,
                        (c, v)-> {c.quadThreshParams.criticalRad = (Float) v; }
                ),
                new DetectorConfigItem(
                        "Quad Thresh",
                        "Maximum MeanSquaredError",
                        "When fitting lines to the contours, what is the maximum mean\n" +
                                "squared error allowed?  This is useful in rejecting contours\n" +
                                "that are far from being quad shaped; rejecting these quads \"early\"\n" +
                                "saves expensive decoding processing.",
                        Float.class,
                        mDetectorConfig,
                        (c)-> c.quadThreshParams.maxLineFitMse,
                        (c, v)-> {c.quadThreshParams.maxLineFitMse = (Float) v; }
                ),
                new DetectorConfigItem(
                        "Quad Thresh",
                        "Minimum White Black Diff",
                        "When we build our model of black & white pixels, we add an\n" +
                                "extra check that the white model must be (overall) brighter\n" +
                                "than the black model.  How much brighter? (in pixel values,\n" +
                                "[0,255]).",
                        Integer.class,
                        mDetectorConfig,
                        (c)-> c.quadThreshParams.minWhiteBlackDiff,
                        (c, v)-> {c.quadThreshParams.minWhiteBlackDiff = (Integer) v; }
                ),
                new DetectorConfigItem(
                        "Quad Thresh",
                        "Should De-Glitch?",
                        "should the thresholded image be deglitched? Only useful for\n" +
                                "very noisy images",
                        Integer.class,
                        mDetectorConfig,
                        (c)-> c.quadThreshParams.deglitch,
                        (c, v)-> {c.quadThreshParams.deglitch = (Integer) v; }
                )
        );
        mPropertySheet.getItems().addAll(items);
    }
}

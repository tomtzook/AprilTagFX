package com.flash3388.apriltagfx.gui.stream.selectors;

import com.flash3388.apriltagfx.gui.stream.ImageStream;
import com.flash3388.apriltagfx.gui.stream.StaticImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class StaticImageSelector implements StreamSelector {

    private final Stage mStage;

    public StaticImageSelector(Stage stage) {
        mStage = stage;
    }

    @Override
    public Optional<ImageStream> selectNew() throws Exception {
        Optional<File> selectedFile = selectImageFile();
        if (!selectedFile.isPresent()) {
            return Optional.empty();
        }

        Mat image = loadImage(selectedFile.get());
        return Optional.of(new StaticImage(image));
    }

    private Optional<File> selectImageFile() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image File",
                "*.jpg", "*.png");
        chooser.getExtensionFilters().add(extFilter);
        chooser.setSelectedExtensionFilter(extFilter);

        File file = chooser.showOpenDialog(mStage);
        return Optional.ofNullable(file);
    }

    private Mat loadImage(File file) throws IOException {
        byte[] imageData = Files.readAllBytes(file.toPath());

        Mat mat = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_UNCHANGED);
        mat.convertTo(mat, CvType.CV_8U);

        return mat;
    }
}

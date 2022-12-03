package com.flash3388.apriltagfx.gui.panes;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayInputStream;

public class StreamView extends HBox {

    private static final double IMAGE_WIDTH = 1280;
    private static final double IMAGE_HEIGHT = 720;

    private final ImageView mImageView;

    public StreamView() {
        mImageView = new ImageView();
        mImageView.setFitWidth(IMAGE_WIDTH);
        mImageView.setFitHeight(IMAGE_HEIGHT);

        getChildren().add(mImageView);
    }

    public void setImage(Mat mat) {
        mImageView.setImage(matToImage(mat));
    }

    private Image matToImage(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
}

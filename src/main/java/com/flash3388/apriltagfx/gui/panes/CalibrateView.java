package com.flash3388.apriltagfx.gui.panes;

import com.flash3388.apriltagfx.gui.Dialogs;
import com.flash3388.apriltagfx.gui.controls.LabeledIntSlider;
import com.flash3388.apriltagfx.io.CamConfigIo;
import com.flash3388.apriltagfx.vision.CamConfig;
import com.flash3388.apriltagfx.vision.calibrator.Calibrator;
import com.flash3388.apriltagfx.vision.calibrator.Config;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CalibrateView extends BorderPane {

    private final Stage mOwner;
    private final ListView<Path> mCalibrationImagesView;
    private final LabeledIntSlider mCornersXSlider;
    private final LabeledIntSlider mCornersYSlider;
    private final Button mSaveConfigToFileBtn;

    private CamConfig mCalculatedConfig;

    public CalibrateView(Stage owner) {
        mOwner = owner;
        mCalculatedConfig = null;

        mCalibrationImagesView = new ListView<>();
        mCalibrationImagesView.setCellFactory((param)-> {
            return new ListCell<>() {
                @Override
                protected void updateItem(Path item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            };
        });
        mCalibrationImagesView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button addImages = new Button("Add");
        addImages.setOnAction((e)-> addCalibrationImages());
        Button removeImages = new Button("Remove");
        removeImages.setOnAction((e)-> {
            mCalibrationImagesView.getItems().removeAll(mCalibrationImagesView.getSelectionModel().getSelectedItems());
        });

        HBox imagesButtonsPane = new HBox();
        imagesButtonsPane.setSpacing(2);
        imagesButtonsPane.getChildren().addAll(addImages, removeImages);

        VBox imagesRoot = new VBox();
        imagesRoot.setSpacing(5);
        imagesRoot.setPadding(new Insets(5));
        imagesRoot.getChildren().addAll(mCalibrationImagesView, imagesButtonsPane);

        setLeft(imagesRoot);

        mCornersXSlider = new LabeledIntSlider("X Corners", 1, 20);
        mCornersYSlider = new LabeledIntSlider("Y Corners", 1, 20);

        VBox configPane = new VBox();
        configPane.setSpacing(5);
        configPane.getChildren().addAll(mCornersXSlider, mCornersYSlider);
        configPane.setPadding(new Insets(5));

        setRight(configPane);

        mSaveConfigToFileBtn = new Button("Save");
        mSaveConfigToFileBtn.setDisable(true);
        mSaveConfigToFileBtn.setOnAction((e)-> saveConfig());

        Button runBtn = new Button("Calibrate");
        runBtn.setOnAction((e)-> {
            runCalibration();
            mSaveConfigToFileBtn.setDisable(mCalculatedConfig == null);
        });

        VBox center = new VBox();
        center.setSpacing(5);
        center.setPadding(new Insets(2));
        center.getChildren().addAll(runBtn, mSaveConfigToFileBtn);

        setCenter(center);
    }

    private void saveConfig() {
        Optional<Path> pathOptional = chooseSavePath();
        if (pathOptional.isEmpty()) {
            return;
        }

        try {
            Path path = pathOptional.get();
            CamConfigIo.save(path, mCalculatedConfig);
            Dialogs.showInfo("Calibration", "Camera Configuration Saved");
            mSaveConfigToFileBtn.setDisable(true);
        } catch (Throwable t) {
            Dialogs.showError("Calibration", "Failed saving configuration", t);
        }
    }

    private Optional<Path> chooseSavePath() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showSaveDialog(mOwner);
        if (file == null) {
            return Optional.empty();
        } else {
            return Optional.of(file.toPath());
        }
    }

    private void addCalibrationImages() {
        Collection<Path> paths = chooseNewCalibrationImages();
        mCalibrationImagesView.getItems().addAll(paths);
    }

    private Collection<Path> chooseNewCalibrationImages() {
        FileChooser chooser = new FileChooser();
        List<File> files = chooser.showOpenMultipleDialog(mOwner);
        return files.stream()
                .map(File::toPath)
                .collect(Collectors.toSet());
    }

    private void runCalibration() {
        List<Path> imagePaths = mCalibrationImagesView.getItems();
        int xCorners = mCornersXSlider.getValue();
        int yCorners = mCornersYSlider.getValue();

        try {
            Calibrator calibrator = new Calibrator(new Config(xCorners, yCorners));
            mCalculatedConfig = calibrator.calibrate(imagePaths);
            Dialogs.showInfo("Calibration", "Camera Configuration Calculated");
        } catch (Throwable t) {
            Dialogs.showError("Calibration", "Failed calibrating", t);
        }
    }
}

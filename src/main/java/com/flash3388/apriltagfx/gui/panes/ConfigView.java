package com.flash3388.apriltagfx.gui.panes;

import com.flash3388.apriltagfx.gui.Dialogs;
import com.flash3388.apriltagfx.gui.controls.LabeledDoubleSlider;
import com.flash3388.apriltagfx.io.CamConfigIo;
import com.flash3388.apriltagfx.vision.CamConfig;
import com.flash3388.apriltagfx.vision.runner.ProcessingConfig;
import com.flash3388.apriltags4j.FamilyType;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigView extends VBox {

    private final Stage mOwner;
    private final AtomicReference<CamConfig> mLoadedCameraConfig;
    private final AtomicReference<FamilyType> mSelectedFamilyType;
    private final AtomicLong mConfiguredTagSize;

    private final Label mLoadedCamConfigPath;

    public ConfigView(Stage owner) {
        mOwner = owner;
        mLoadedCameraConfig = new AtomicReference<>();
        mSelectedFamilyType = new AtomicReference<>(FamilyType.values()[0]);
        mConfiguredTagSize = new AtomicLong(0);

        HBox camConfigInfoPane = new HBox();
        camConfigInfoPane.setSpacing(5.0);
        mLoadedCamConfigPath = new Label("Not Loaded");
        mLoadedCamConfigPath.setMinWidth(300);
        mLoadedCamConfigPath.setMaxWidth(300);
        Button loadNewCamConfigBtn = new Button("Load");
        loadNewCamConfigBtn.setOnAction((e)-> {
            loadNewCamConfig();
        });
        camConfigInfoPane.getChildren().addAll(
                new Label("Camera Config:"),
                mLoadedCamConfigPath
        );
        VBox camConfigPane = new VBox();
        camConfigPane.setSpacing(5);
        camConfigPane.getChildren().addAll(camConfigInfoPane, loadNewCamConfigBtn);

        LabeledDoubleSlider tagSizeSlider = new LabeledDoubleSlider("Tag Size[m]", 0, 20);
        tagSizeSlider.valueProperty().addListener((obs, o, n)-> {
            mConfiguredTagSize.set(Double.doubleToLongBits(n.doubleValue()));
        });

        ComboBox<FamilyType> familyTypeComboBox = new ComboBox<>();
        familyTypeComboBox.getItems().addAll(FamilyType.values());
        familyTypeComboBox.setValue(mSelectedFamilyType.get());
        familyTypeComboBox.valueProperty().addListener((obs, o, n)-> {
            mSelectedFamilyType.set(n);
        });

        setPadding(new Insets(5));
        setSpacing(10);
        getChildren().addAll(camConfigPane, tagSizeSlider, familyTypeComboBox);
    }

    private void loadNewCamConfig() {
        Optional<Path> optional = chooseNewCamConfigFile();
        if (optional.isEmpty()) {
            return;
        }

        Path path = optional.get();
        try {
            CamConfig camConfig = CamConfigIo.load(path);
            mLoadedCameraConfig.set(camConfig);
            mLoadedCamConfigPath.setText(path.toString());
        } catch (IOException e) {
            Dialogs.showError("Error", "Failed loading camera configuration", e);
        }
    }

    private Optional<Path> chooseNewCamConfigFile() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(mOwner);
        if (file == null) {
            return Optional.empty();
        } else {
            return Optional.of(file.toPath());
        }
    }

    public Optional<ProcessingConfig> getProcessingConfig() {
        CamConfig camConfig = mLoadedCameraConfig.get();
        if (camConfig == null) {
            return Optional.empty();
        }

        double tagSize = Double.longBitsToDouble(mConfiguredTagSize.get());
        FamilyType familyType = mSelectedFamilyType.get();

        return Optional.of(new ProcessingConfig(camConfig, tagSize, familyType));
    }
}

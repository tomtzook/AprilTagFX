package com.flash3388.apriltagfx.gui;

import com.flash3388.apriltagfx.gui.panes.CalibrateView;
import com.flash3388.apriltagfx.gui.panes.ConfigView;
import com.flash3388.apriltagfx.gui.panes.OutputView;
import com.flash3388.apriltagfx.gui.panes.StreamView;
import com.flash3388.apriltagfx.vision.runner.ProcessingConfig;
import com.flash3388.apriltagfx.vision.runner.Result;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.opencv.core.Mat;

import java.util.Optional;

public class MainWindow implements AutoCloseable {

    private final double mWidth;
    private final double mHeight;

    private final Stage mOwner;
    private final BorderPane mRoot;

    private final StreamView mStreamView;
    private final ConfigView mConfigView;
    private final OutputView mOutputView;
    private final Menu mStreamMenu;

    private final ProcessingControl mProcessingControl;

    public MainWindow(Stage owner, double width, double height) {
        mOwner = owner;
        mWidth = width;
        mHeight = height;
        mRoot = new BorderPane();

        mStreamView = new StreamView();
        mConfigView = new ConfigView(owner);
        mOutputView = new OutputView();
        mStreamMenu = new Menu("Stream");

        mProcessingControl = new ProcessingControl(owner, this);
    }

    public Scene createScene() {
        Menu calibrateMenu = new Menu("Calibrate");
        MenuItem doCalibrate = new MenuItem("Open");
        doCalibrate.setOnAction((e)-> {
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mOwner);

            CalibrateView calibrateView = new CalibrateView(stage);
            stage.setScene(new Scene(calibrateView, 500, 300));
            stage.showAndWait();
        });
        calibrateMenu.getItems().add(doCalibrate);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(mStreamMenu, calibrateMenu);

        VBox dataPane = new VBox();
        dataPane.setSpacing(5);
        dataPane.setPadding(new Insets(5));
        dataPane.getChildren().addAll(mOutputView, mConfigView);

        mRoot.setTop(menuBar);
        mRoot.setCenter(mStreamView);
        mRoot.setRight(dataPane);

        return new Scene(mRoot, mWidth, mHeight);
    }

    public void addStreamSwitchingControl(String name, Runnable task) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction((e)->task.run());

        mStreamMenu.getItems().add(menuItem);
    }

    public Optional<ProcessingConfig> getProcessingConfig() {
        return mConfigView.getProcessingConfig();
    }

    public void loadOutput(Mat mat, Result result) {
        Platform.runLater(()-> {
            mStreamView.setImage(mat);
            mOutputView.setOutput(result);
        });
    }

    public void clearOutput() {
        Platform.runLater(mOutputView::clear);
    }

    @Override
    public void close() throws Exception {
        mProcessingControl.close();
    }
}

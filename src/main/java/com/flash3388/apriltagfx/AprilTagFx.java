package com.flash3388.apriltagfx;

import com.castle.code.Natives;
import com.castle.exceptions.CodeLoadException;
import com.castle.exceptions.FindException;
import com.castle.util.closeables.Closeables;
import com.flash3388.apriltagfx.gui.ApplicationGui;
import com.flash3388.apriltagfx.gui.Dialogs;
import com.flash3388.apriltagfx.gui.MainWindow;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public class AprilTagFx {

    private static final boolean FORCE_FULL_SCREEN = false;

    private static final double WINDOW_WIDTH = 1800;
    private static final double WINDOW_HEIGHT = 800;

    private final ProgramOptions mProgramOptions;
    private final ExecutorService mExecutorService;
    private final Logger mLogger;

    private final AtomicReference<MainWindow> mMainWindow;

    public AprilTagFx(ProgramOptions programOptions, ExecutorService executorService, Logger logger) {
        mProgramOptions = programOptions;
        mExecutorService = executorService;
        mLogger = logger;
        mMainWindow = new AtomicReference<>();
    }

    public void run() throws InitializationException {
        mLogger.info("Starting GUI");
        Stage primaryStage = ApplicationGui.startGui(mExecutorService);
        mLogger.info("GUI launched");

        showMainWindow(primaryStage);
    }

    private void showMainWindow(Stage primaryStage) throws InitializationException {
        try {
            CountDownLatch runLatch = new CountDownLatch(1);

            Platform.runLater(()-> {
                final MainWindow mainWindow = new MainWindow(primaryStage, WINDOW_WIDTH, WINDOW_HEIGHT);
                mMainWindow.set(mainWindow);

                primaryStage.setScene(mainWindow.createScene());

                if (FORCE_FULL_SCREEN) {
                    primaryStage.setFullScreen(true);
                    primaryStage.setMaximized(true);
                }

                primaryStage.setOnCloseRequest((e)-> {
                    try {
                        mainWindow.close();
                    } catch (Exception ex) {}

                    runLatch.countDown();
                });
                primaryStage.show();

                try {
                    loadNatives();
                } catch (Throwable t) {
                    Dialogs.showError("Error", "Failed loading natives", t);
                    primaryStage.close();
                    Closeables.silentClose(mainWindow);
                    runLatch.countDown();
                    return;
                }

                mainWindow.loadDetectorConfig();
            });

            runLatch.await();
        } catch (Exception e) {
            Platform.exit();
            throw new InitializationException(e);
        }
    }

    private void loadNatives() throws CodeLoadException {
        try {
            Natives.Loader loader = Natives.newLoader();

            if (mProgramOptions.getCustomNativesDir().isPresent()) {
                loader.firstFrom(mProgramOptions.getCustomNativesDir().get());
            }

            loader.load("opencv_java\\d+", "apriltags_jni");
        } catch (FindException | IOException e) {
            throw new CodeLoadException(e);
        }
    }
}

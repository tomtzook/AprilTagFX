package com.flash3388.apriltagfx;

import com.flash3388.apriltagfx.gui.ApplicationGui;
import com.flash3388.apriltagfx.gui.MainWindow;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public class AprilTagFx {

    private static final boolean FORCE_FULL_SCREEN = false;

    private static final double WINDOW_WIDTH = 1800;
    private static final double WINDOW_HEIGHT = 800;

    private final ExecutorService mExecutorService;
    private final Logger mLogger;

    private final AtomicReference<MainWindow> mMainWindow;

    public AprilTagFx(ExecutorService executorService, Logger logger) {
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
            });

            runLatch.await();
        } catch (Exception e) {
            Platform.exit();
            throw new InitializationException(e);
        }
    }
}

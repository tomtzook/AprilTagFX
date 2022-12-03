package com.flash3388.apriltagfx.gui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class ApplicationGui extends Application {

    private static AtomicReference<ApplicationGui> sInstance = new AtomicReference<>();
    private static CyclicBarrier sLaunchBarrier = new CyclicBarrier(2);

    private final AtomicReference<Stage> mPrimaryStage = new AtomicReference<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        sInstance.set(this);
        mPrimaryStage.set(primaryStage);
        sLaunchBarrier.await();
    }

    public static Stage startGui(ExecutorService executorService) {
        Future<?> guiRunFuture = executorService.submit(new GuiLaunchTask());

        try {
            sLaunchBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {

        }

        return sInstance.get().mPrimaryStage.get();
    }
}

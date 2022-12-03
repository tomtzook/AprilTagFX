package com.flash3388.apriltagfx.gui;

import com.flash3388.apriltagfx.gui.stream.EmptyImage;
import com.flash3388.apriltagfx.gui.stream.ImageStream;
import com.flash3388.apriltagfx.gui.stream.selectors.CameraStreamSelector;
import com.flash3388.apriltagfx.gui.stream.selectors.StaticImageSelector;
import com.flash3388.apriltagfx.gui.stream.selectors.StreamSelector;
import com.flash3388.apriltagfx.vision.runner.Processor;
import com.flash3388.apriltagfx.vision.runner.RunningTask;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ProcessingControl implements AutoCloseable {

    private final MainWindow mWindow;
    private final ScheduledExecutorService mExecutorService;
    private final AtomicReference<Future<?>> mRunFuture;
    private final Processor mProcessor;

    private ImageStream mImageStream;

    public ProcessingControl(Stage stage, MainWindow window) {
        mWindow = window;
        mExecutorService = Executors.newSingleThreadScheduledExecutor();
        mRunFuture = new AtomicReference<>(null);
        mProcessor = new Processor();

        mImageStream = new EmptyImage();

        mWindow.addStreamSwitchingControl("Load Image", ()->{
            selectNewStream(new StaticImageSelector(stage));
        });
        mWindow.addStreamSwitchingControl("Open Camera", ()->{
            selectNewStream(new CameraStreamSelector());
        });
    }

    public synchronized void start() {
        Future<?> future = mExecutorService.scheduleAtFixedRate(
                new RunningTask(
                        mImageStream,
                        mWindow::getProcessingConfig,
                        mWindow::loadOutput,
                        mProcessor),
                100,
                50,
                TimeUnit.MILLISECONDS);
        mRunFuture.set(future);
    }

    public synchronized void stop() {
        Future<?> future = mRunFuture.getAndSet(null);
        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    public synchronized void close() throws Exception {
        stop();
        mExecutorService.shutdownNow();
        mImageStream.close();
        mProcessor.close();
    }

    private void selectNewStream(StreamSelector selector) {
        stop();
        try {
            Optional<ImageStream> optionalImageStream = selector.selectNew();
            if (!optionalImageStream.isPresent()) {
                // canceled
                return;
            }

            ImageStream newStream = optionalImageStream.get();
            switchStream(newStream);
            mWindow.clearOutput();
        } catch (Exception e) {
            e.printStackTrace();
            Dialogs.showError("Error", "Error Switching Streams", e);
        } finally {
            start();
        }
    }

    private void switchStream(ImageStream newStream) {
        ImageStream old = mImageStream;
        mImageStream = newStream;
        old.close();
    }
}

package com.flash3388.apriltagfx.vision.runner;

import com.flash3388.apriltagfx.gui.stream.ImageStream;
import org.opencv.core.Mat;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class RunningTask implements Runnable {

    private final ImageStream mImageStream;
    private final Supplier<Optional<ProcessingConfig>> mConfigSupplier;
    private final BiConsumer<Mat, Result> mOutputConsumer;
    private final Processor mProcessor;

    public RunningTask(ImageStream imageStream,
                       Supplier<Optional<ProcessingConfig>> configSupplier,
                       BiConsumer<Mat, Result> outputConsumer,
                       Processor processor) {
        mImageStream = imageStream;
        mConfigSupplier = configSupplier;
        mOutputConsumer = outputConsumer;
        mProcessor = processor;
    }

    @Override
    public void run() {
        try {
            Mat mat = mImageStream.get();
            if (mat == null || mat.empty()) {
                return;
            }

            Optional<ProcessingConfig> optionalConfig = mConfigSupplier.get();
            if (optionalConfig.isEmpty()) {
                mOutputConsumer.accept(mat, null);
                return;
            }

            ProcessingConfig config = optionalConfig.get();
            Result output = mProcessor.process(mat, config);
            mOutputConsumer.accept(mat, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

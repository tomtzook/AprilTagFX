package com.flash3388.apriltagfx;

import com.castle.code.Natives;
import com.castle.exceptions.CodeLoadException;
import com.castle.exceptions.FindException;
import com.castle.util.closeables.Closer;
import com.castle.util.logging.LoggerBuilder;
import javafx.application.Platform;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        Logger logger = new LoggerBuilder("AprilTagFx")
                .enableConsoleLogging(true)
                .build();

        loadNatives();

        ProgramOptions options = handleArguments(args);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Closer closer = Closer.empty();
        closer.add(executorService::shutdownNow);
        try {
            AprilTagFx aprilTagFx = new AprilTagFx(executorService, logger);
            aprilTagFx.run();
        } finally {
            closer.close();
        }

        System.out.println("hello");
        Platform.exit();
    }

    private static ProgramOptions handleArguments(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("AprilTagFx").build()
                .defaultHelp(true)
                .description("");

        Namespace namespace = parser.parseArgs(args);

        return new ProgramOptions();
    }

    private static void loadNatives() {
        try {
            Natives.newLoader()
                    .load("opencv_java\\d+", "apriltags_jni");
        } catch (FindException | CodeLoadException | IOException e) {
            throw new Error(e);
        }
    }
}

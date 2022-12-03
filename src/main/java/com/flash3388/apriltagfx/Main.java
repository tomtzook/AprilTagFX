package com.flash3388.apriltagfx;

import com.castle.util.closeables.Closer;
import com.castle.util.logging.LoggerBuilder;
import javafx.application.Platform;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        Logger logger = new LoggerBuilder("AprilTagFx")
                .enableConsoleLogging(true)
                .build();

        ProgramOptions programOptions = handleArguments(args);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Closer closer = Closer.empty();
        closer.add(executorService::shutdownNow);
        try {
            AprilTagFx aprilTagFx = new AprilTagFx(programOptions, executorService, logger);
            aprilTagFx.run();
        } finally {
            closer.close();
        }

        Platform.exit();
    }

    private static ProgramOptions handleArguments(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("AprilTagFx").build()
                .defaultHelp(true)
                .description("");
        parser.addArgument("--natives-dir")
                .dest("nativesDir")
                .type(String.class)
                .required(false);

        Namespace namespace = parser.parseArgs(args);

        Path nativesDirPath = namespace.getString("nativesDir") != null ?
                Paths.get(namespace.getString("nativesDir")) :
                null;
        return new ProgramOptions(nativesDirPath);
    }
}

// src/main/java/com/example/filehandler/FileCollectorApplication.java
package com.example.filehandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.filehandler.config.AppConfig;
import com.example.filehandler.factory.FileWriterStrategyFactory;
import com.example.filehandler.reader.FileReader;
import com.example.filehandler.reader.FileReaderImpl;
import com.example.filehandler.strategy.FileWriterStrategy;

/**
 * Main application class for collecting and processing files.
 */
public class FileCollectorApplication {
    private static final Logger logger = LoggerFactory.getLogger(FileCollectorApplication.class);

    public static void main(String[] args) {
        AppConfig config = loadConfiguration(args);
        Path outputPath = Paths.get(config.getOutputFilePath());

        try {
            prepareOutputFile(outputPath);

            FileReader fileReader = new FileReaderImpl()
                    .withOutputFile(outputPath)
                    .withIgnoreFolders(config.getIgnoreFolders());

            processFiles(fileReader, config);

            logger.info("File contents written to {}", outputPath.normalize().toAbsolutePath());
        } catch (Exception e) {
            logger.error("Error processing files: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Loads application configuration from command-line args or defaults.
     *
     * @param args command-line arguments
     * @return application configuration
     */
    private static AppConfig loadConfiguration(String[] args) {
        // In a real application, this would load from a config file or command line
        // args
        AppConfig config = new AppConfig();

        config.setOutputFilePath("./output.txt");

        config.setIgnoreFolders(Arrays.asList(
                "C:\\Users\\ntgpt\\OneDrive\\workspace\\job-scheduling-system-ui\\src\\pages\\Applications1\\jobs\\JobDialog\\JobDialogForm",
                "C:\\Users\\ntgpt\\OneDrive\\workspace\\job-scheduling-system-ui\\src\\types\\jobs1"));

        config.setFolderPaths(Arrays.asList(
                // "C:\\Users\\n" + //
                // "tgpt\\OneDrive\\workspace\\qlz_flash_cards_ui\\lib\\features\\flashcard\\presentation",
                "C:\\Users\\n" + //
                        "tgpt\\OneDrive\\workspace\\Kardio\\app\\src\\main\\res\\n" + //
                        "avigation"));

        return config;
    }

    /**
     * Prepares the output file by deleting it if it exists.
     *
     * @param outputPath the path to the output file
     * @throws IOException if an I/O error occurs
     */
    private static void prepareOutputFile(Path outputPath) throws IOException {
        Files.deleteIfExists(outputPath);
    }

    /**
     * Processes files using the configured file reader and strategies.
     *
     * @param fileReader the file reader to use
     * @param config     the application configuration
     */
    private static void processFiles(FileReader fileReader, AppConfig config) {
        List<String> folderPaths = config.getFolderPaths();
        FileWriterStrategyFactory strategyFactory = new FileWriterStrategyFactory(folderPaths);

        try {
            // Process all registered file types
            for (String fileType : config.getRegisteredFileTypes()) {
                List<String> extensions = config.getFileExtensions(fileType);
                FileWriterStrategy strategy = strategyFactory.createStrategy(fileType);

                if (strategy != null) {
                    logger.info("Processing {} files with extensions: {}", fileType, extensions);
                    fileReader.readAndWriteFiles(folderPaths, extensions, strategy);
                } else {
                    logger.warn("No strategy found for file type: {}", fileType);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading and writing files: {}", e.getMessage(), e);
        }
    }
}
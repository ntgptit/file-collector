package com.example.filehandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.filehandler.config.AppConfig;
import com.example.filehandler.factory.FileWriterStrategyFactory;
import com.example.filehandler.reader.FileReader;
import com.example.filehandler.reader.FileReaderBuilder;
import com.example.filehandler.strategy.FileWriterStrategy;

/**
 * Main application class for collecting and processing files.
 * This version supports file exclusions for specific file types.
 */
public class FileCollectorApplication {
    private static final Logger logger = LoggerFactory.getLogger(FileCollectorApplication.class);

    /**
     * Loads application configuration with support for file exclusions.
     *
     * @return application configuration
     */
    private static AppConfig loadConfiguration() {
        return new AppConfig.Builder()
                .setOutputFilePath("./output.txt")
                .addIgnoreFolder(
                        "C:\\Users\\ntgpt\\OneDrive\\workspace\\job-scheduling-system-ui\\src\\pages\\Applications1\\jobs\\JobDialog\\JobDialogForm")
                .addIgnoreFolder("C:\\Users\\ntgpt\\OneDrive\\workspace\\job-scheduling-system-ui\\src\\types\\jobs1")
                .addFolderPath("D:\\workspace\\spaced_learning_app")
                // Define file types and their extensions
                .addFileType("java", List.of(".java", ".fxml"))
                .addFileType("typescript", List.of(".ts", ".tsx"))
//                .addFileType("properties", List.of(".properties"))
                .addFileType("dart", List.of(".dart"))
//                .addFileType("kotlin", List.of(".kt", ".xml"))
                .addFileType("kotlin", List.of(".kt"))
                // Define exclusions for specific file types
                .addFileExclusions("dart", List.of(".freezed.dart", ".g.dart"))
                .addFileExclusions("java", List.of(".generated.java"))
                .addFileExclusions("typescript", List.of(".generated.ts"))
                .build();
    }

    public static void main(String[] args) {
        final var config = loadConfiguration();

        if (config.getFolderPaths().isEmpty()) {
            logger.error("No folder paths configured. Exiting application.");
            System.exit(1);
            return;
        }

        final var outputPath = Paths.get(config.getOutputFilePath());

        try {
            prepareOutputFile(outputPath);

            // Using the FileReaderBuilder with exclusions
            final var fileReader = new FileReaderBuilder()
                    .withOutputFile(outputPath)
                    .withIgnoreFolders(config.getIgnoreFolders())
                    .build();

            processFiles(fileReader, config);

            logger.info("File contents written to {}", outputPath.normalize().toAbsolutePath());
        } catch (final Exception e) {
            logger.error("Error processing files: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Prepares the output file by deleting it if it exists.
     *
     * @param outputPath the path to the output file
     * @throws IOException if an I/O error occurs
     */
    private static void prepareOutputFile(Path outputPath) throws IOException {
        if (outputPath == null) {
            throw new IllegalArgumentException("Output path cannot be null");
        }

        Files.deleteIfExists(outputPath);

        // Create parent directories if they don't exist
        final var parent = outputPath.getParent();
        if ((parent != null) && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    /**
     * Processes files using the configured file reader and strategies.
     *
     * @param fileReader the file reader to use
     * @param config     the application configuration
     */
    private static void processFiles(FileReader fileReader, AppConfig config) {
        final var folderPaths = config.getFolderPaths();
        final var strategyFactory = new FileWriterStrategyFactory(folderPaths);
        final var fileTypes = config.getRegisteredFileTypes();

        if (fileTypes.isEmpty()) {
            logger.warn("No file types registered for processing");
            return;
        }

        for (final String fileType : fileTypes) {
            processFileType(fileReader, config, strategyFactory, fileType);
        }
    }

    /**
     * Processes files of a specific type.
     *
     * @param fileReader      the file reader to use
     * @param config          the application configuration
     * @param strategyFactory the strategy factory
     * @param fileType        the file type to process
     */
    private static void processFileType(FileReader fileReader, AppConfig config,
            FileWriterStrategyFactory strategyFactory, String fileType) {
        final var extensions = config.getFileExtensions(fileType);

        if (extensions.isEmpty()) {
            logger.warn("No extensions configured for file type: {}", fileType);
            return;
        }

        final var exclusions = config.getFileExclusions(fileType);

        strategyFactory.createStrategy(fileType)
                .ifPresentOrElse(
                        strategy -> processWithStrategy(fileReader, config, fileType, extensions, exclusions, strategy),
                        () -> logger.warn("No strategy found for file type: {}", fileType));
    }

    /**
     * Processes files with a specific strategy, applying exclusions.
     *
     * @param fileReader the file reader to use
     * @param config     the application configuration
     * @param fileType   the file type being processed
     * @param extensions the file extensions to process
     * @param exclusions the file patterns to exclude
     * @param strategy   the strategy to use
     */
    private static void processWithStrategy(FileReader fileReader, AppConfig config,
            String fileType, List<String> extensions, List<String> exclusions, FileWriterStrategy strategy) {
        if (!exclusions.isEmpty()) {
            logger.info("Processing {} files with extensions: {} (excluding: {})",
                    fileType, extensions, exclusions);
        } else {
            logger.info("Processing {} files with extensions: {}", fileType, extensions);
        }

        try {
            fileReader.readAndWriteFilesWithExclusions(
                    config.getFolderPaths(), extensions, exclusions, strategy);
        } catch (final IOException e) {
            logger.error("Error reading and writing files of type {}: {}", fileType, e.getMessage(), e);
        }
    }
}
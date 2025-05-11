package com.example.filehandler.factory;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.filehandler.strategy.DartFileWriterStrategy;
import com.example.filehandler.strategy.FileWriterStrategy;
import com.example.filehandler.strategy.JavaFileWriterStrategy;
import com.example.filehandler.strategy.KotlinFileWriterStrategy;
import com.example.filehandler.strategy.PropertiesFileWriterStrategy;
import com.example.filehandler.strategy.TypeScriptFileWriterStrategy;

/**
 * Factory for creating FileWriterStrategy instances based on file type.
 */
public class FileWriterStrategyFactory {
    private static final Logger logger = LoggerFactory.getLogger(FileWriterStrategyFactory.class);
    private final List<String> folderPaths;

    /**
     * Creates a new FileWriterStrategyFactory.
     *
     * @param folderPaths list of folder paths to pass to strategies
     */
    public FileWriterStrategyFactory(List<String> folderPaths) {
        if (folderPaths == null) {
            throw new IllegalArgumentException("Folder paths cannot be null");
        }
        this.folderPaths = folderPaths;
    }

    /**
     * Creates a FileWriterStrategy for the specified file type.
     *
     * @param fileType the type of file to process
     * @return Optional containing the appropriate FileWriterStrategy or empty if not supported
     */
    public Optional<FileWriterStrategy> createStrategy(String fileType) {
        if ((fileType == null) || fileType.trim().isEmpty()) {
            logger.warn("File type cannot be null or empty");
            return Optional.empty();
        }

        return switch (fileType.toLowerCase()) {
        case "java" -> Optional.of(new JavaFileWriterStrategy(this.folderPaths));
        case "typescript" -> Optional.of(new TypeScriptFileWriterStrategy(this.folderPaths));
        case "properties" -> Optional.of(new PropertiesFileWriterStrategy(this.folderPaths));
        case "dart" -> Optional.of(new DartFileWriterStrategy(this.folderPaths));
        case "kotlin" -> Optional.of(new KotlinFileWriterStrategy(this.folderPaths));
        default -> {
            logger.warn("Unsupported file type: {}", fileType);
            yield Optional.empty();
        }
        };
    }
}
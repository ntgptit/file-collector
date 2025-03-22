// src/main/java/com/example/filehandler/factory/FileWriterStrategyFactory.java
package com.example.filehandler.factory;

import java.util.List;

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
        this.folderPaths = folderPaths;
    }

    /**
     * Creates a FileWriterStrategy for the specified file type.
     *
     * @param fileType the type of file to process
     * @return the appropriate FileWriterStrategy or null if not supported
     */
    public FileWriterStrategy createStrategy(String fileType) {
        return switch (fileType.toLowerCase()) {
            case "java" -> new JavaFileWriterStrategy(this.folderPaths);
            case "typescript" -> new TypeScriptFileWriterStrategy(this.folderPaths);
            case "properties" -> new PropertiesFileWriterStrategy(this.folderPaths);
            case "dart" -> new DartFileWriterStrategy(this.folderPaths);
            case "kotlin" -> new KotlinFileWriterStrategy(this.folderPaths);
            default -> {
                logger.warn("Unsupported file type: {}", fileType);
                yield null;
            }
        };
    }

}
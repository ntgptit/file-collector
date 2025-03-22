// src/main/java/com/example/filehandler/util/FileUtil.java
package com.example.filehandler.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for file operations.
 */
public final class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Finds files with specified extensions in a folder, excluding ignored folders.
     *
     * @param folder         the root folder to search in
     * @param fileExtensions list of file extensions to include
     * @param ignoreFolders  list of folder paths to ignore
     * @return list of files that match the criteria
     * @throws IOException if an I/O error occurs
     */
    public static List<Path> findFiles(Path folder, List<String> fileExtensions, List<String> ignoreFolders)
            throws IOException {
        try (Stream<Path> paths = Files.walk(folder)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> !shouldIgnorePath(path, ignoreFolders))
                    .filter(path -> hasMatchingExtension(path, fileExtensions))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Checks if a path should be ignored based on the list of ignored folders.
     *
     * @param path          the path to check
     * @param ignoreFolders list of folder paths to ignore
     * @return true if the path should be ignored, false otherwise
     */
    private static boolean shouldIgnorePath(Path path, List<String> ignoreFolders) {
        String absolutePath = path.toAbsolutePath().toString();
        return ignoreFolders.stream()
                .anyMatch(ignorePath -> absolutePath.startsWith(ignorePath));
    }

    /**
     * Checks if a file has an extension that matches one of the specified extensions.
     *
     * @param path           the file path to check
     * @param fileExtensions list of file extensions to match
     * @return true if the file has a matching extension, false otherwise
     */
    private static boolean hasMatchingExtension(Path path, List<String> fileExtensions) {
        String pathStr = path.toString().toLowerCase();
        return fileExtensions.stream()
                .anyMatch(ext -> pathStr.endsWith(ext.toLowerCase()));
    }

    /**
     * Opens a file with the default system application.
     *
     * @param file the file to open
     * @throws IOException if an I/O error occurs
     */
    public static void openFile(Path file) throws IOException {
        if (Files.exists(file)) {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(file.toAbsolutePath().toString()));
            } else {
                logger.warn("Desktop is not supported on this platform, cannot open file");
            }
        } else {
            logger.warn("File does not exist: {}", file);
        }
    }
}
package com.example.filehandler.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for file operations.
 */
public final class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Finds files with specified extensions in a folder, excluding ignored folders.
     * Backward compatible method.
     *
     * @param folder         the root folder to search in
     * @param fileExtensions list of file extensions to include
     * @param ignoreFolders  list of folder paths to ignore
     * @return list of files that match the criteria
     * @throws IOException if an I/O error occurs
     */
    public static List<Path> findFiles(Path folder, List<String> fileExtensions,
            List<String> ignoreFolders) throws IOException {
        return findFiles(folder, fileExtensions, ignoreFolders, Collections.emptyList());
    }

    /**
     * Finds files with specified extensions in a folder, excluding ignored folders and specific file patterns.
     *
     * @param folder         the root folder to search in
     * @param fileExtensions list of file extensions to include
     * @param ignoreFolders  list of folder paths to ignore
     * @param fileExclusions list of file patterns to exclude
     * @return list of files that match the criteria
     * @throws IOException if an I/O error occurs
     */
    public static List<Path> findFiles(Path folder, List<String> fileExtensions,
            List<String> ignoreFolders, List<String> fileExclusions) throws IOException {
        if (folder == null) {
            logger.error("Folder path cannot be null");
            return Collections.emptyList();
        }

        if (!Files.exists(folder)) {
            logger.error("Folder does not exist: {}", folder);
            return Collections.emptyList();
        }

        if ((fileExtensions == null) || fileExtensions.isEmpty()) {
            logger.warn("File extensions list is empty, no files will be matched");
            return Collections.emptyList();
        }

        try (var paths = Files.walk(folder)) {
            return paths
                    .filter(FileUtil::isRegularFile)
                    .filter(path -> !shouldIgnorePath(path, ignoreFolders))
                    .filter(path -> hasMatchingExtension(path, fileExtensions))
                    .filter(path -> !matchesExclusionPattern(path, fileExclusions))
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            logger.error("Error walking file tree: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Checks if a file has an extension that matches one of the specified extensions.
     *
     * @param path           the file path to check
     * @param fileExtensions list of file extensions to match
     * @return true if the file has a matching extension, false otherwise
     */
    private static boolean hasMatchingExtension(Path path, List<String> fileExtensions) {
        if ((path == null) || (fileExtensions == null) || fileExtensions.isEmpty()) {
            return false;
        }

        final var pathStr = path.toString().toLowerCase();

        for (final String ext : fileExtensions) {
            if ((ext == null) || ext.isEmpty()) {
                continue;
            }

            final var lowerExt = ext.toLowerCase();
            if (pathStr.endsWith(lowerExt)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a path is a regular file.
     *
     * @param path the path to check
     * @return true if the path is a regular file, false otherwise
     */
    private static boolean isRegularFile(Path path) {
        return Files.isRegularFile(path);
    }

    /**
     * Checks if a file matches any of the exclusion patterns.
     *
     * @param path           the file path to check
     * @param fileExclusions list of file patterns to exclude
     * @return true if the file matches an exclusion pattern, false otherwise
     */
    private static boolean matchesExclusionPattern(Path path, List<String> fileExclusions) {
        if ((path == null) || (fileExclusions == null) || fileExclusions.isEmpty()) {
            return false;
        }

        final var pathStr = path.toString().toLowerCase();

        for (final String exclusion : fileExclusions) {
            if ((exclusion == null) || exclusion.isEmpty()) {
                continue;
            }

            final var lowerExclusion = exclusion.toLowerCase();
            if (pathStr.endsWith(lowerExclusion)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Opens a file with the default system application.
     *
     * @param file the file to open
     * @throws IOException if an I/O error occurs
     */
    public static void openFile(Path file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        if (!Files.exists(file)) {
            logger.warn("File does not exist: {}", file);
            return;
        }

        if (!Desktop.isDesktopSupported()) {
            logger.warn("Desktop is not supported on this platform, cannot open file");
            return;
        }

        Desktop.getDesktop().open(new File(file.toAbsolutePath().toString()));
    }

    /**
     * Checks if a path should be ignored based on the list of ignored folders.
     *
     * @param path          the path to check
     * @param ignoreFolders list of folder paths to ignore
     * @return true if the path should be ignored, false otherwise
     */
    private static boolean shouldIgnorePath(Path path, List<String> ignoreFolders) {
        if ((ignoreFolders == null) || ignoreFolders.isEmpty()) {
            return false;
        }

        final var absolutePath = path.toAbsolutePath().toString();

        for (final String ignorePath : ignoreFolders) {
            if (absolutePath.startsWith(ignorePath)) {
                return true;
            }
        }

        return false;
    }

    private FileUtil() {
        // Private constructor to prevent instantiation
    }
}
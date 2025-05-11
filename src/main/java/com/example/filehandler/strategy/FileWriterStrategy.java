package com.example.filehandler.strategy;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * Abstract strategy for processing file content before writing.
 * Implements Template Method pattern for file content processing.
 */
public abstract class FileWriterStrategy {
    protected final List<String> folderPaths;

    /**
     * Creates a new FileWriterStrategy with the specified folder paths.
     *
     * @param folderPaths list of folder paths used for path normalization
     * @throws IllegalArgumentException if folderPaths is null
     */
    protected FileWriterStrategy(List<String> folderPaths) {
        this.folderPaths = Objects.requireNonNull(folderPaths, "Folder paths cannot be null");
    }

    /**
     * Gets a normalized folder path for the file.
     * Changes backslashes to forward slashes and simplifies paths that contain /src/.
     *
     * @param file the file to get the path for
     * @return the normalized path
     */
    protected String getFolderPath(Path file) {
        if (file == null) {
            return "";
        }

        final var pathStr = StringUtils.replace(file.toAbsolutePath().toString(), "\\", "/");

        if (pathStr.contains("/src/")) {
            return "src" + "/" + StringUtils.substringAfterLast(pathStr, "src/");
        }

        return pathStr;
    }

    /**
     * Determines if a line is a comment line based on the file type.
     * This is an abstract method that must be implemented by subclasses.
     *
     * @param line the line to check
     * @return true if the line is a comment, false otherwise
     */
    protected abstract boolean isCommentLine(String line);

    /**
     * Processes file lines by adding the file path and removing comment lines.
     * Uses template method pattern to allow specific implementations to define
     * their own logic for identifying comment lines.
     *
     * @param file  the file being processed
     * @param lines the lines from the file
     * @return the processed lines
     */
    public List<String> processLines(Path file, List<String> lines) {
        if ((file == null) || (lines == null) || lines.isEmpty()) {
            return Collections.emptyList();
        }

        final List<String> result = new ArrayList<>();

        // Add file path as a comment at the beginning
        final var folderPath = getFolderPath(file);
        result.add("// " + folderPath);

        // Process each line
        for (final String line : lines) {
            if ((line == null) || shouldSkipLine(line)) {
                continue;
            }

            result.add(line);
        }

        return result;
    }

    /**
     * Determines if a line should be skipped during processing.
     * Subclasses can override this method to add additional skip conditions.
     *
     * @param line the line to check
     * @return true if the line should be skipped, false otherwise
     */
    protected boolean shouldSkipLine(String line) {
        return line.trim().isEmpty() || isCommentLine(line);
    }
}
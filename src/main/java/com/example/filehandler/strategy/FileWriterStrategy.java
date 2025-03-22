// src/main/java/com/example/filehandler/strategy/FileWriterStrategy.java
package com.example.filehandler.strategy;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Abstract strategy for processing file content before writing.
 */
public abstract class FileWriterStrategy {
    protected final List<String> folderPaths;

    /**
     * Creates a new FileWriterStrategy with the specified folder paths.
     *
     * @param folderPaths list of folder paths used for path normalization
     */
    protected FileWriterStrategy(List<String> folderPaths) {
        this.folderPaths = folderPaths;
    }

    /**
     * Processes file lines by adding the file path and removing comment lines.
     *
     * @param file  the file being processed
     * @param lines the lines from the file
     * @return the processed lines
     */
    public List<String> processLines(Path file, List<String> lines) {
        List<String> result = new ArrayList<>();
        String folderPath = this.getFolderPath(file);

        // Add file path as a comment at the top
        result.add("// " + folderPath);

        // Process each line
        for (String line : lines) {
            if (this.shouldSkipLine(line)) {
                continue;
            }
            result.add(line);
        }

        return result;
    }

    /**
     * Determines if a line should be skipped during processing.
     *
     * @param line the line to check
     * @return true if the line should be skipped, false otherwise
     */
    protected boolean shouldSkipLine(String line) {
        return line.trim().isEmpty() || this.isCommentLine(line);
    }

    /**
     * Determines if a line is a comment line based on the file type.
     *
     * @param line the line to check
     * @return true if the line is a comment, false otherwise
     */
    protected abstract boolean isCommentLine(String line);

    /**
     * Gets a normalized folder path for the file.
     *
     * @param file the file to get the path for
     * @return the normalized path
     */
    protected String getFolderPath(Path file) {
        String pathStr = StringUtils.replace(file.toAbsolutePath().toString(), "\\", "/");
        return pathStr.contains("/src/")
                ? "src" + "/" + StringUtils.substringAfterLast(pathStr, "src/")
                : pathStr;
    }
}
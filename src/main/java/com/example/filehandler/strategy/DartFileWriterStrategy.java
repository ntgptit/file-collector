// src/main/java/com/example/filehandler/strategy/DartFileWriterStrategy.java
package com.example.filehandler.strategy;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Strategy for processing Dart files.
 */
public class DartFileWriterStrategy extends FileWriterStrategy {
    // Match single-line doc comments (///)
    private static final Pattern DART_DOC_SINGLE_LINE_PATTERN = Pattern.compile("^\\s*///.*$");

    // Match block doc comments (/** */)
    private static final Pattern DART_DOC_BLOCK_PATTERN = Pattern.compile("^\\s*/\\*\\*.*\\*/\\s*$");

    // Match single-line comments (//)
    private static final Pattern DART_COMMENT_PATTERN = Pattern.compile("^\\s*//.*$");

    // Match block comments (/* */)
    private static final Pattern DART_BLOCK_COMMENT_PATTERN = Pattern.compile("^\\s*/\\*.*\\*/\\s*$");

    /**
     * Creates a new DartFileWriterStrategy.
     *
     * @param folderPaths list of folder paths
     */
    public DartFileWriterStrategy(List<String> folderPaths) {
        super(folderPaths);
    }

    @Override
    protected boolean shouldSkipLine(String line) {
        String trimmedLine = line.trim();
        // Skip empty lines, comments, and import/package statements
        return super.shouldSkipLine(line) ||
                trimmedLine.startsWith("import ") ||
                trimmedLine.startsWith("export ") ||
                trimmedLine.startsWith("part ") ||
                trimmedLine.startsWith("library ");
    }

    @Override
    protected boolean isCommentLine(String line) {
        return DART_DOC_SINGLE_LINE_PATTERN.matcher(line).matches() ||
                DART_DOC_BLOCK_PATTERN.matcher(line).matches() ||
                DART_COMMENT_PATTERN.matcher(line).matches() ||
                DART_BLOCK_COMMENT_PATTERN.matcher(line).matches();
    }
}
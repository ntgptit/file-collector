package com.example.filehandler.strategy;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Strategy for processing Kotlin files.
 */
public class KotlinFileWriterStrategy extends FileWriterStrategy {
    private static final Pattern KOTLIN_DOC_PATTERN = Pattern.compile("^\\s*/\\*\\*.*\\*/\\s*$");
    private static final Pattern KOTLIN_COMMENT_PATTERN = Pattern.compile("^\\s*//.*$");
    private static final Pattern IMPORT_PACKAGE_PATTERN =
            Pattern.compile("^\\s*(import|package)\\s+.*$");

    /**
     * Creates a new KotlinFileWriterStrategy.
     *
     * @param folderPaths list of folder paths
     */
    public KotlinFileWriterStrategy(List<String> folderPaths) {
        super(folderPaths);
    }

    @Override
    protected boolean shouldSkipLine(String line) {
        String trimmedLine = line.trim();
        return super.shouldSkipLine(line) || IMPORT_PACKAGE_PATTERN.matcher(trimmedLine).matches();
    }

    @Override
    protected boolean isCommentLine(String line) {
        return KOTLIN_DOC_PATTERN.matcher(line).matches()
                || KOTLIN_COMMENT_PATTERN.matcher(line).matches();
    }
}

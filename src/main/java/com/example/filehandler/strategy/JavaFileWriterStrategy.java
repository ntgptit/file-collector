// src/main/java/com/example/filehandler/strategy/JavaFileWriterStrategy.java
package com.example.filehandler.strategy;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Strategy for processing Java files.
 */
public class JavaFileWriterStrategy extends FileWriterStrategy {
    private static final Pattern JAVA_DOC_PATTERN = Pattern.compile("^\\s*/\\*\\*.*\\*/\\s*$");
    private static final Pattern JAVA_COMMENT_PATTERN = Pattern.compile("^\\s*//.*$");

    /**
     * Creates a new JavaFileWriterStrategy.
     *
     * @param folderPaths list of folder paths
     */
    public JavaFileWriterStrategy(List<String> folderPaths) {
        super(folderPaths);
    }

    @Override
    protected boolean shouldSkipLine(String line) {
        return super.shouldSkipLine(line) || line.trim().startsWith("import") || line.trim().startsWith("package");
    }

    @Override
    protected boolean isCommentLine(String line) {
        return JAVA_DOC_PATTERN.matcher(line).matches() || JAVA_COMMENT_PATTERN.matcher(line).matches();
    }
}
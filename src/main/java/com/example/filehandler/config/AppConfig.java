// src/main/java/com/example/filehandler/config/AppConfig.java
package com.example.filehandler.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration class for the file collector application.
 */
public class AppConfig {
    private String outputFilePath;
    private List<String> ignoreFolders = new ArrayList<>();
    private List<String> folderPaths = new ArrayList<>();

    // Map of file types to their extensions
    private Map<String, List<String>> fileTypeExtensions = new HashMap<>();

    public AppConfig() {
        // Initialize default file type extensions
        this.fileTypeExtensions.put("java", List.of(".java", ".fxml"));
        this.fileTypeExtensions.put("typescript", List.of(".ts", ".tsx"));
        this.fileTypeExtensions.put("properties", List.of(".properties"));
        this.fileTypeExtensions.put("dart", List.of(".dart"));
        this.fileTypeExtensions.put("kotlin", List.of(".kt", ".xml"));
    }

    public String getOutputFilePath() {
        return this.outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public List<String> getIgnoreFolders() {
        return this.ignoreFolders;
    }

    public void setIgnoreFolders(List<String> ignoreFolders) {
        this.ignoreFolders = ignoreFolders;
    }

    public List<String> getFolderPaths() {
        return this.folderPaths;
    }

    public void setFolderPaths(List<String> folderPaths) {
        this.folderPaths = folderPaths;
    }

    /**
     * Get the file extensions for a specific file type.
     *
     * @param fileType the file type (e.g., "java", "dart")
     * @return list of file extensions for the specified type
     */
    public List<String> getFileExtensions(String fileType) {
        return this.fileTypeExtensions.getOrDefault(fileType, List.of());
    }

    /**
     * Add or update file extensions for a specific file type.
     *
     * @param fileType   the file type (e.g., "java", "dart")
     * @param extensions list of file extensions for the type
     */
    public void setFileExtensions(String fileType, List<String> extensions) {
        this.fileTypeExtensions.put(fileType, extensions);
    }

    /**
     * Get all registered file types.
     *
     * @return set of all file types
     */
    public List<String> getRegisteredFileTypes() {
        return List.copyOf(this.fileTypeExtensions.keySet());
    }
}
package com.example.filehandler.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration class for the file collector application.
 * Follows immutable builder pattern for property setting.
 */
public class AppConfig {
    /**
     * Builder for creating AppConfig instances.
     */
    public static class Builder {
        private String outputFilePath;
        private final List<String> ignoreFolders = new ArrayList<>();
        private final List<String> folderPaths = new ArrayList<>();
        private final Map<String, List<String>> fileTypeExtensions = new HashMap<>();
        private final Map<String, List<String>> fileTypeExclusions = new HashMap<>();

        /**
         * Adds or updates file exclusions for a specific file type.
         * These are patterns that should be excluded even if they match extensions.
         *
         * @param fileType   the file type
         * @param exclusions the list of exclusion patterns
         * @return this builder instance
         */
        public Builder addFileExclusions(String fileType, List<String> exclusions) {
            if ((fileType != null) && !fileType.trim().isEmpty() && (exclusions != null)) {
                this.fileTypeExclusions.put(fileType, new ArrayList<>(exclusions));
            }
            return this;
        }

        /**
         * Adds or updates file extensions for a specific file type.
         *
         * @param fileType   the file type
         * @param extensions the list of file extensions
         * @return this builder instance
         */
        public Builder addFileType(String fileType, List<String> extensions) {
            if ((fileType != null) && !fileType.trim().isEmpty() && (extensions != null)) {
                this.fileTypeExtensions.put(fileType, new ArrayList<>(extensions));
            }
            return this;
        }

        /**
         * Adds a folder path to search.
         *
         * @param folderPath the folder path to add
         * @return this builder instance
         */
        public Builder addFolderPath(String folderPath) {
            if ((folderPath != null) && !folderPath.trim().isEmpty()) {
                this.folderPaths.add(folderPath);
            }
            return this;
        }

        /**
         * Adds a folder to ignore.
         *
         * @param folder the folder to ignore
         * @return this builder instance
         */
        public Builder addIgnoreFolder(String folder) {
            if ((folder != null) && !folder.trim().isEmpty()) {
                this.ignoreFolders.add(folder);
            }
            return this;
        }

        /**
         * Builds a new AppConfig instance.
         *
         * @return a new AppConfig instance
         */
        public AppConfig build() {
            if ((this.outputFilePath == null) || this.outputFilePath.trim().isEmpty()) {
                throw new IllegalStateException("Output file path must be set");
            }

            return new AppConfig(this);
        }

        /**
         * Sets the list of folder paths to search.
         *
         * @param folderPaths the list of folder paths
         * @return this builder instance
         */
        public Builder setFolderPaths(List<String> folderPaths) {
            this.folderPaths.clear();
            if (folderPaths != null) {
                this.folderPaths.addAll(folderPaths);
            }
            return this;
        }

        /**
         * Sets the list of folders to ignore.
         *
         * @param ignoreFolders the list of folders to ignore
         * @return this builder instance
         */
        public Builder setIgnoreFolders(List<String> ignoreFolders) {
            this.ignoreFolders.clear();
            if (ignoreFolders != null) {
                this.ignoreFolders.addAll(ignoreFolders);
            }
            return this;
        }

        /**
         * Sets the output file path.
         *
         * @param outputFilePath the path to the output file
         * @return this builder instance
         */
        public Builder setOutputFilePath(String outputFilePath) {
            this.outputFilePath = outputFilePath;
            return this;
        }
    }

    private final String outputFilePath;
    private final List<String> ignoreFolders;
    private final List<String> folderPaths;
    private final Map<String, List<String>> fileTypeExtensions;

    private final Map<String, List<String>> fileTypeExclusions;

    /**
     * Default constructor with predefined configurations.
     */
    public AppConfig() {
        // Using the builder to set default values
        final var builder = new Builder()
                .setOutputFilePath("./output.txt");

        // Initialize default file type extensions
        builder.addFileType("java", List.of(".java", ".fxml"))
                .addFileType("typescript", List.of(".ts", ".tsx"))
                .addFileType("properties", List.of(".properties"))
                .addFileType("dart", List.of(".dart"))
                .addFileType("kotlin", List.of(".kt", ".xml"));

        // Initialize default file type exclusions
        builder.addFileExclusions("dart", List.of(".freezed.dart", ".g.dart"));

        // Initialize the instance
        this.outputFilePath = builder.outputFilePath;
        this.ignoreFolders = Collections.unmodifiableList(new ArrayList<>(builder.ignoreFolders));
        this.folderPaths = Collections.unmodifiableList(new ArrayList<>(builder.folderPaths));

        final Map<String, List<String>> typeExtMap = new HashMap<>();
        builder.fileTypeExtensions.forEach((key, value) -> typeExtMap.put(key, Collections.unmodifiableList(
                new ArrayList<>(value))));
        this.fileTypeExtensions = Collections.unmodifiableMap(typeExtMap);

        final Map<String, List<String>> typeExclMap = new HashMap<>();
        builder.fileTypeExclusions.forEach((key, value) -> typeExclMap.put(key, Collections.unmodifiableList(
                new ArrayList<>(value))));
        this.fileTypeExclusions = Collections.unmodifiableMap(typeExclMap);
    }

    /**
     * Private constructor used by the builder.
     *
     * @param builder the builder containing configuration values
     */
    private AppConfig(Builder builder) {
        this.outputFilePath = builder.outputFilePath;
        this.ignoreFolders = Collections.unmodifiableList(new ArrayList<>(builder.ignoreFolders));
        this.folderPaths = Collections.unmodifiableList(new ArrayList<>(builder.folderPaths));

        final Map<String, List<String>> typeExtMap = new HashMap<>();
        builder.fileTypeExtensions.forEach((key, value) -> typeExtMap.put(key, Collections.unmodifiableList(
                new ArrayList<>(value))));
        this.fileTypeExtensions = Collections.unmodifiableMap(typeExtMap);

        final Map<String, List<String>> typeExclMap = new HashMap<>();
        builder.fileTypeExclusions.forEach((key, value) -> typeExclMap.put(key, Collections.unmodifiableList(
                new ArrayList<>(value))));
        this.fileTypeExclusions = Collections.unmodifiableMap(typeExclMap);
    }

    /**
     * Get the file exclusions for a specific file type.
     * These are patterns that should be excluded even if they match extensions.
     *
     * @param fileType the file type (e.g., "java", "dart")
     * @return list of file exclusion patterns for the specified type or empty list if not found
     */
    public List<String> getFileExclusions(String fileType) {
        if ((fileType == null) || fileType.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return this.fileTypeExclusions.getOrDefault(fileType, Collections.emptyList());
    }

    /**
     * Get the file extensions for a specific file type.
     *
     * @param fileType the file type (e.g., "java", "dart")
     * @return list of file extensions for the specified type or empty list if not found
     */
    public List<String> getFileExtensions(String fileType) {
        if ((fileType == null) || fileType.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return this.fileTypeExtensions.getOrDefault(fileType, Collections.emptyList());
    }

    public List<String> getFolderPaths() {
        return this.folderPaths;
    }

    public List<String> getIgnoreFolders() {
        return this.ignoreFolders;
    }

    public String getOutputFilePath() {
        return this.outputFilePath;
    }

    /**
     * Get all registered file types.
     *
     * @return list of all file types
     */
    public List<String> getRegisteredFileTypes() {
        return new ArrayList<>(this.fileTypeExtensions.keySet());
    }

    /**
     * For compatibility with existing code - creates a mutable instance with updated folder paths.
     *
     * @param folderPaths the new list of folder paths
     */
    public void setFolderPaths(List<String> folderPaths) {
        throw new UnsupportedOperationException(
                "This AppConfig instance is immutable. Use toBuilder() to create a modified copy.");
    }

    /**
     * For compatibility with existing code - creates a mutable instance with updated ignore folders.
     *
     * @param ignoreFolders the new list of folders to ignore
     */
    public void setIgnoreFolders(List<String> ignoreFolders) {
        throw new UnsupportedOperationException(
                "This AppConfig instance is immutable. Use toBuilder() to create a modified copy.");
    }

    /**
     * For compatibility with existing code - creates a mutable instance with updated output file path.
     *
     * @param outputFilePath the new output file path
     */
    public void setOutputFilePath(String outputFilePath) {
        throw new UnsupportedOperationException(
                "This AppConfig instance is immutable. Use toBuilder() to create a modified copy.");
    }

    /**
     * Creates a new builder with values from this config.
     *
     * @return a new builder initialized with values from this config
     */
    public Builder toBuilder() {
        final var builder = new Builder()
                .setOutputFilePath(this.outputFilePath);

        // Add ignore folders
        for (final String folder : this.ignoreFolders) {
            builder.addIgnoreFolder(folder);
        }

        // Add folder paths
        for (final String path : this.folderPaths) {
            builder.addFolderPath(path);
        }

        // Add file type extensions
        this.fileTypeExtensions.forEach(builder::addFileType);

        // Add file type exclusions
        this.fileTypeExclusions.forEach(builder::addFileExclusions);

        return builder;
    }
}
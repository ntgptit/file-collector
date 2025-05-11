package com.example.filehandler.reader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Builder class for creating FileReader instances with fluent API.
 * Provides a more robust alternative to method chaining.
 */
public class FileReaderBuilder {
    private Path outputFile;
    private List<String> ignoreFolders = new ArrayList<>();

    /**
     * Adds a single folder to the ignore list.
     *
     * @param folder the folder path to ignore
     * @return this builder instance
     * @throws IllegalArgumentException if folder is null
     */
    public FileReaderBuilder addIgnoreFolder(String folder) {
        Objects.requireNonNull(folder, "Folder path cannot be null");
        this.ignoreFolders.add(Paths.get(folder).toAbsolutePath().toString());
        return this;
    }

    /**
     * Builds and returns a new FileReader instance.
     *
     * @return a new FileReader instance
     * @throws IllegalStateException if required parameters are not set
     */
    public FileReader build() {
        if (this.outputFile == null) {
            throw new IllegalStateException("Output file is required");
        }

        // Creating a new FileReaderImpl and configuring it with the builder parameters
        return new FileReaderImpl()
                .withOutputFile(this.outputFile)
                .withIgnoreFolders(this.ignoreFolders);
    }

    /**
     * Sets the folders to ignore during file search.
     *
     * @param ignoreFolders list of folder paths to ignore
     * @return this builder instance
     * @throws IllegalArgumentException if ignoreFolders is null
     */
    public FileReaderBuilder withIgnoreFolders(List<String> ignoreFolders) {
        Objects.requireNonNull(ignoreFolders, "Ignore folders list cannot be null");
        this.ignoreFolders = ignoreFolders.stream()
                .map(path -> Paths.get(path).toAbsolutePath().toString())
                .collect(Collectors.toList());
        return this;
    }

    /**
     * Sets the output file for the FileReader.
     *
     * @param outputFile the path to the output file
     * @return this builder instance
     * @throws IllegalArgumentException if outputFile is null
     */
    public FileReaderBuilder withOutputFile(Path outputFile) {
        this.outputFile = Objects.requireNonNull(outputFile, "Output file cannot be null");
        return this;
    }
}
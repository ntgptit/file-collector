package com.example.filehandler.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.filehandler.strategy.FileWriterStrategy;
import com.example.filehandler.util.FileUtil;

/**
 * Implementation of FileReader interface that reads files from folders and
 * writes their content to an output file.
 */
public class FileReaderImpl implements FileReader {
    private static final Logger logger = LoggerFactory.getLogger(FileReaderImpl.class);
    private Path outputFile;
    private List<String> ignoreFolders;

    /**
     * Creates a new FileReaderImpl with empty ignore folders list.
     */
    public FileReaderImpl() {
        this.ignoreFolders = new ArrayList<>();
    }

    /**
     * Checks if a file can be processed.
     *
     * @param file the file to check
     * @return true if the file can be processed, false otherwise
     */
    private boolean isProcessableFile(Path file) {
        try {
            return Files.exists(file) && (Files.size(file) > 0);
        } catch (final IOException e) {
            logger.error("Error checking file {}: {}", file, e.getMessage());
            return false;
        }
    }

    @Override
    public void readAndWriteFiles(List<String> folderPaths, List<String> fileExtensions, FileWriterStrategy strategy)
            throws IOException {
        readAndWriteFilesWithExclusions(folderPaths, fileExtensions, Collections.emptyList(), strategy);
    }

    @Override
    public void readAndWriteFilesWithExclusions(List<String> folderPaths, List<String> fileExtensions,
            List<String> fileExclusions, FileWriterStrategy strategy)
            throws IOException {
        validateReadAndWriteParameters(folderPaths, fileExtensions, strategy);

        final var files = readFilesWithExclusions(folderPaths, fileExtensions, fileExclusions);
        if (files.isEmpty()) {
            logger.info("No files found matching the specified criteria");
            return;
        }

        writeFilesContent(files, strategy);

        try {
            FileUtil.openFile(this.outputFile);
        } catch (final IOException e) {
            logger.warn("Unable to open output file: {}", e.getMessage());
        }
    }

    /**
     * Reads all lines from a file.
     *
     * @param file the file to read
     * @return the lines read from the file, or an empty list if an error occurs
     */
    private List<String> readFileLines(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (final IOException e) {
            logger.error("Error reading file {}: {}", file, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Reads files with specified extensions from given folder paths, excluding specific file patterns.
     *
     * @param folderPaths    list of folder paths to search in
     * @param fileExtensions list of file extensions to filter by
     * @param fileExclusions list of file patterns to exclude
     * @return list of file paths that match the criteria
     * @throws IOException if an I/O error occurs
     */
    private List<Path> readFilesWithExclusions(List<String> folderPaths, List<String> fileExtensions,
            List<String> fileExclusions) throws IOException {
        final List<Path> files = new ArrayList<>();

        for (final String folderPath : folderPaths) {
            final var folder = Paths.get(folderPath);
            if (!Files.exists(folder)) {
                logger.warn("Folder does not exist: {}", folderPath);
                continue;
            }

            files.addAll(FileUtil.findFiles(folder, fileExtensions, this.ignoreFolders, fileExclusions));
        }

        if (files.isEmpty()) {
            return Collections.emptyList();
        }

        sortFilesBySize(files);
        return files;
    }

    /**
     * Sorts files by size in descending order.
     *
     * @param files list of files to sort
     */
    private void sortFilesBySize(List<Path> files) {
        files.sort((path1, path2) -> {
            try {
                return Long.compare(Files.size(path2), Files.size(path1));
            } catch (final IOException e) {
                logger.error("Error comparing file sizes: {}", e.getMessage());
                return 0;
            }
        });
    }

    /**
     * Validates the parameters for reading and writing files.
     *
     * @param folderPaths    list of folder paths
     * @param fileExtensions list of file extensions
     * @param strategy       file writer strategy
     */
    private void validateReadAndWriteParameters(List<String> folderPaths, List<String> fileExtensions,
            FileWriterStrategy strategy) {
        if (this.outputFile == null) {
            throw new IllegalStateException("Output file must be set before reading files");
        }

        if ((folderPaths == null) || folderPaths.isEmpty()) {
            throw new IllegalArgumentException("Folder paths must not be empty");
        }

        if ((fileExtensions == null) || fileExtensions.isEmpty()) {
            throw new IllegalArgumentException("File extensions must not be empty");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Strategy must not be null");
        }
    }

    @Override
    public FileReader withIgnoreFolders(List<String> ignoreFolders) {
        if (ignoreFolders == null) {
            throw new IllegalArgumentException("Ignore folders list cannot be null");
        }
        this.ignoreFolders = ignoreFolders.stream()
                .map(path -> Paths.get(path).toAbsolutePath().toString())
                .collect(Collectors.toList());
        return this;
    }

    @Override
    public FileReader withOutputFile(Path outputFile) {
        if (outputFile == null) {
            throw new IllegalArgumentException("Output file path cannot be null");
        }
        this.outputFile = outputFile;
        return this;
    }

    /**
     * Writes the content of the specified files to the output file using the given strategy.
     *
     * @param files    list of files to process
     * @param strategy strategy to use for processing file content
     * @throws IOException if an I/O error occurs
     */
    private void writeFilesContent(List<Path> files, FileWriterStrategy strategy) throws IOException {
        for (final Path file : files) {
            if (!isProcessableFile(file)) {
                continue;
            }

            final var lines = readFileLines(file);
            if (lines.isEmpty()) {
                continue;
            }

            final var processedLines = strategy.processLines(file, lines);
            if (processedLines.isEmpty()) {
                continue;
            }

            writeToOutputFile(processedLines);
        }
    }

    /**
     * Writes lines to the output file.
     *
     * @param lines the lines to write
     */
    private void writeToOutputFile(List<String> lines) {
        try {
            Files.write(this.outputFile, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Files.write(this.outputFile, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        } catch (final IOException e) {
            logger.error("Error writing to output file: {}", e.getMessage());
        }
    }
}
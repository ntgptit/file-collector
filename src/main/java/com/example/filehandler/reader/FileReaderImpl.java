// src/main/java/com/example/filehandler/reader/FileReaderImpl.java
package com.example.filehandler.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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

    @Override
    public FileReader withOutputFile(Path outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    @Override
    public FileReader withIgnoreFolders(List<String> ignoreFolders) {
        this.ignoreFolders = ignoreFolders.stream()
                .map(path -> Paths.get(path).toAbsolutePath().toString())
                .collect(Collectors.toList());
        return this;
    }

    @Override
    public void readAndWriteFiles(List<String> folderPaths, List<String> fileExtensions, FileWriterStrategy strategy)
            throws IOException {
        if (this.outputFile == null) {
            throw new IllegalStateException("Output file must be set before reading files");
        }

        List<Path> files = this.readFiles(folderPaths, fileExtensions);
        this.writeFilesContent(files, strategy);

        try {
            FileUtil.openFile(this.outputFile);
        } catch (IOException e) {
            logger.warn("Unable to open output file: {}", e.getMessage());
        }
    }

    /**
     * Reads files with specified extensions from given folder paths.
     *
     * @param folderPaths    list of folder paths to search in
     * @param fileExtensions list of file extensions to filter by
     * @return list of file paths that match the criteria
     * @throws IOException if an I/O error occurs
     */
    private List<Path> readFiles(List<String> folderPaths, List<String> fileExtensions) throws IOException {
        List<Path> files = new ArrayList<>();

        for (String folderPath : folderPaths) {
            Path folder = Paths.get(folderPath);

            if (!Files.exists(folder)) {
                logger.warn("Folder does not exist: {}", folderPath);
                continue;
            }

            files.addAll(FileUtil.findFiles(folder, fileExtensions, this.ignoreFolders));
        }

        // Sort files by size in descending order
        files.sort((path1, path2) -> {
            try {
                return Long.compare(Files.size(path2), Files.size(path1));
            } catch (IOException e) {
                logger.error("Error comparing file sizes: {}", e.getMessage());
                return 0;
            }
        });

        return files;
    }

    /**
     * Writes the content of the specified files to the output file using the given strategy.
     *
     * @param files    list of files to process
     * @param strategy strategy to use for processing file content
     * @throws IOException if an I/O error occurs
     */
    private void writeFilesContent(List<Path> files, FileWriterStrategy strategy) throws IOException {
        for (Path file : files) {
            try {
                if (Files.size(file) > 0) {
                    List<String> lines = strategy.processLines(file, Files.readAllLines(file));

                    if (!lines.isEmpty()) {
                        Files.write(this.outputFile, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                        Files.write(this.outputFile, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
                    }
                }
            } catch (IOException e) {
                logger.error("Error processing file {}: {}", file, e.getMessage());
            }
        }
    }
}
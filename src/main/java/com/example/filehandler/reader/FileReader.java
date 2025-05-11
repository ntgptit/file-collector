package com.example.filehandler.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.example.filehandler.strategy.FileWriterStrategy;

/**
 * Interface for reading files from specified folders and writing their contents
 * using a defined strategy.
 */
public interface FileReader {
    /**
     * Reads files with specific extensions from given folders and writes their content
     * using the provided strategy.
     *
     * @param folderPaths    list of folder paths to search for files
     * @param fileExtensions list of file extensions to filter files
     * @param strategy       strategy to process and write file contents
     * @throws IOException              if an I/O error occurs
     * @throws IllegalStateException    if the output file is not set
     * @throws IllegalArgumentException if any parameter is invalid
     */
    void readAndWriteFiles(List<String> folderPaths, List<String> fileExtensions, FileWriterStrategy strategy)
            throws IOException;

    /**
     * Reads files with specific extensions from given folders, excluding files matching specific patterns,
     * and writes their content using the provided strategy.
     *
     * @param folderPaths    list of folder paths to search for files
     * @param fileExtensions list of file extensions to filter files
     * @param fileExclusions list of file patterns to exclude
     * @param strategy       strategy to process and write file contents
     * @throws IOException              if an I/O error occurs
     * @throws IllegalStateException    if the output file is not set
     * @throws IllegalArgumentException if any parameter is invalid
     */
    void readAndWriteFilesWithExclusions(
            List<String> folderPaths,
            List<String> fileExtensions,
            List<String> fileExclusions,
            FileWriterStrategy strategy)
            throws IOException;

    /**
     * Sets folders to be ignored during the file search.
     *
     * @param ignoreFolders list of folder paths to ignore
     * @return this FileReader instance for method chaining
     * @throws IllegalArgumentException if ignoreFolders is null
     */
    FileReader withIgnoreFolders(List<String> ignoreFolders);

    /**
     * Sets the output file path where the collected content will be written.
     *
     * @param outputFile the path to the output file
     * @return this FileReader instance for method chaining
     * @throws IllegalArgumentException if outputFile is null
     */
    FileReader withOutputFile(Path outputFile);
}
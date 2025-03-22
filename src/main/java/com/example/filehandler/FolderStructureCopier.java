//package com.example.filehandler;
//
//import java.io.IOException;
//import java.nio.file.FileVisitResult;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.SimpleFileVisitor;
//import java.nio.file.attribute.BasicFileAttributes;
//
//public class FolderStructureCopier {
//    public static void main(String[] args) {
//        String sourcePath = "C:\\Users\\ntgpt\\OneDrive\\workspace\\job-scheduling-system-ui\\src";
//        String destinationPath = "D:\\Project\\MyProject\\FE\\job-scheduling-system-ui\\src";
//
//        try {
//            copyFolderStructure(Paths.get(sourcePath), Paths.get(destinationPath));
//        } catch (IOException e) {
//            System.err.println("Error copying folder structure: " + e.getMessage());
//        }
//    }
//
//    private static void copyFolderStructure(Path sourcePath, Path destinationPath) throws IOException {
//        Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
//            @Override
//            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//                Path relativePath = sourcePath.relativize(dir);
//                Path newDir = destinationPath.resolve(relativePath);
//                Files.createDirectories(newDir);
//                return FileVisitResult.CONTINUE;
//            }
//
//            @Override
//            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                // Không cần sao chép các tệp tin, chỉ cần tạo cấu trúc thư mục
//                return FileVisitResult.CONTINUE;
//            }
//        });
//    }
//}

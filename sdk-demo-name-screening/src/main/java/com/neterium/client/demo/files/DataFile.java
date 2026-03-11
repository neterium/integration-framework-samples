package com.neterium.client.demo.files;

import java.nio.file.Path;

/**
 * DataFile
 *
 * @author Bernard Ligny
 */
public class DataFile {

    public static final String RUNNING_DIRECTORY = ".running";
    public static final String ERROR_DIRECTORY = ".error";
    public static final String DONE_DIRECTORY = ".done";
    public static final String UPLOAD_DIRECTORY = ".upload";

    private final Path inputFile;

    private DataFile(Path inputFile) {
        this.inputFile = inputFile;
    }

    public static DataFile from(String inputFile) {
        return new DataFile(Path.of(inputFile));
    }

    public Path getInputFile() {
        return inputFile;
    }

    public Path getRunningFile() {
        return resolve(RUNNING_DIRECTORY);
    }

    public Path getParkedFile() {
        return resolve(ERROR_DIRECTORY);
    }

    public Path getArchivedFile() {
        return resolve(DONE_DIRECTORY);
    }

    private Path resolve(String subDir) {
        return findRoot(inputFile)
                .resolve(subDir)
                .resolve(inputFile.getFileName());
    }

    @Override
    public String toString() {
        return inputFile.getFileName().toString();
    }


    private Path findRoot(Path path) {
        do {
            path = path.getParent();
        } while (path.getFileName().toFile().isHidden());
        return path;
    }

}

package org.CLI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Rmdir extends Command {

    @Override
    public String execute(PathObj pathObj, String args) throws IOException {
        List<List<String>> directories = FilesHelper.extractFilesFromString(pathObj, args);
        StringBuilder result = new StringBuilder();

        for (List<String> strings : directories) {
            Path directoryToDelete = pathObj.path.resolve(strings.get(1)).resolve(strings.get(0));

            if (Files.exists(directoryToDelete)) {
                if (Files.isDirectory(directoryToDelete)) {
                    if (isDirEmpty(directoryToDelete)) {
                        Files.delete(directoryToDelete);
                        result.append("Deleted directory: ").append(directoryToDelete.toString()).append("\n");
                    } else {
                        result.append("Directory is not empty: ").append(directoryToDelete.toString()).append("\n");
                    }
                } else {
                    result.append("Path is not a directory: ").append(directoryToDelete.toString()).append("\n");
                }
            } else {
                result.append("Directory does not exist: ").append(directoryToDelete.toString()).append("\n");
            }
        }

        return result.toString().trim();
    }

    private boolean isDirEmpty(Path directory) throws IOException {
        try (var stream = Files.newDirectoryStream(directory)) {
            return !stream.iterator().hasNext();
        }
    }

    @Override
    public String help() {
        return "Removes empty directories.";
    }
}

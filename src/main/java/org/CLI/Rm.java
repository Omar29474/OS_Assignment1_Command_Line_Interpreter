package org.CLI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Rm extends Command {

    @Override
    public String execute(PathObj pathObj, String args) throws IOException {
        List<List<String>> filesName = FilesHelper.extractFilesFromString(pathObj, args);
        StringBuilder result = new StringBuilder();

        for (List<String> strings : filesName) {
            Path pathToDelete = pathObj.path.resolve(strings.get(1)).resolve(strings.get(0));

            if (Files.exists(pathToDelete)) {
                if (Files.isDirectory(pathToDelete)) {
                    throw new IOException("Cannot delete directory: " + pathToDelete.toString() + ". Use rmdir command instead.");
                } else {
                    Files.delete(pathToDelete);
                    result.append("Deleted: ").append(pathToDelete.toString()).append("\n");
                }
            } else {
                throw new IOException("File does not exist: " + pathToDelete.toString());
            }
        }

        return result.toString();
    }

    @Override
    public String help() {
        return "Removes one or more files. Directories cannot be removed using this command.";
    }
}

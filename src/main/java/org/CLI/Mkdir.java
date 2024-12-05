package org.CLI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Mkdir extends Command {

    @Override
    public String execute(PathObj pathObj, String args) throws IOException {
        List<List<String>> directories = FilesHelper.extractFilesFromString(pathObj, args);
        StringBuilder result = new StringBuilder();

        for (List<String> strings : directories) {
            Path directoryToCreate = pathObj.path.resolve(strings.get(1)).resolve(strings.get(0));

            if (!Files.exists(directoryToCreate)) {
                Files.createDirectory(directoryToCreate);
                result.append("Created directory: ").append(directoryToCreate.toString()).append("\n");
            } else if (Files.isDirectory(directoryToCreate)) {
                result.append("Directory already exists: ").append(directoryToCreate.toString()).append("\n");
            } else {
                result.append("A file with this name already exists: ").append(directoryToCreate.toString()).append("\n");
            }
        }

        return result.toString().trim();
    }

    @Override
    public String help() {
        return "Creates new directories.";
    }
}

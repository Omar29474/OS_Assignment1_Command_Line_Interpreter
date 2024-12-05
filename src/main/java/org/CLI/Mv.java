package org.CLI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Mv extends Command {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String execute(PathObj pathObj, String args) throws IOException {
        if (args.trim().equals("help")) {
            return helpMessage();
        }

        String[] argArray = args.trim().split("\\s+");

        if (argArray.length < 2) {
            throw new IOException("Invalid arguments. Usage: mv <source> <destination>");
        }

        List<List<String>> filesName = FilesHelper.extractFilesFromString(pathObj, args);
        List<Path> sources = new ArrayList<>();
        Path destination = null;

        for (int i = 0; i < filesName.size(); i++) {
            List<String> fileDetails = filesName.get(i);
            Path filePath = Path.of(fileDetails.get(1)).resolve(fileDetails.get(0));
            if (i == filesName.size() - 1) {
                destination = filePath;
            } else {
                sources.add(filePath);
            }
        }

        validateDestination(destination, sources.size());

        StringBuilder result = new StringBuilder();
        for (Path source : sources) {
            result.append(moveOrRename(source, destination));
        }

        return result.toString();
    }


    private void validateDestination(Path destination, int sourceCount) throws IOException {
        if (sourceCount > 1 && (!Files.isDirectory(destination) || !Files.exists(destination))) {
            throw new IOException("Error: When moving multiple sources, the destination must be an existing directory.");
        }
    }

    private String moveOrRename(Path source, Path destination) throws IOException {
        if (!Files.exists(source)) {
            throw new IOException("Source file does not exist: " + source);
        }

        Path target = getPath(source, destination);
        if (source.equals(target)) {
            return "This name is the same as the previous. No changes were made to " + source.toAbsolutePath() + ".\n";
        }
        if (Files.exists(target) && !confirmOverwrite(target)) {
            return "Skipped: " + source.toAbsolutePath() + "\n";
        }

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);

        if (source.getParent().equals(target.getParent())) {
            return "Renamed: " + source.toAbsolutePath() + " to " + target.toAbsolutePath() + "\n";
        } else {
            return "Moved: " + source.toAbsolutePath() + " to " + target.toAbsolutePath() + "\n";
        }
    }

    private Path getPath(Path source, Path destination) {
        return Files.isDirectory(destination) ? destination.resolve(source.getFileName()) : destination;
    }

    private boolean confirmOverwrite(Path target) {
        System.out.print("Overwrite " + target + "? (y/n): ");
        return scanner.nextLine().trim().equalsIgnoreCase("y");
    }

    private String helpMessage() {
        return """
                Moves or renames files or directories.

                Usage:
                  mv <source> <destination>               - Renames a file or directory if <destination> is a file or new name.
                  mv <source> <directory>                 - Moves a single file or directory to the specified directory.
                  mv <source1> <source2> ... <directory>  - Moves multiple files to the specified directory.

                Note:
                  If the last argument is not a directory and multiple sources are given, an error will occur.
                """;
    }

    @Override
    public String help() {
        return "Moves or renames files or directories.";
    }
}

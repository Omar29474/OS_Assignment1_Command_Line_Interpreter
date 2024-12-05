package org.CLI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ls extends Command {

    @Override
    public String execute(PathObj pathObj, String args) throws IOException {
        if (args.trim().equals("help")) {
            return helpMessage();
        }

        StringBuilder result = new StringBuilder();
        boolean showAll = false;
        boolean reverseOrder = false;

        Path pathToList = pathObj.path;
        String[] argArray = args.trim().split("\\s+");
        String pathArg = "";

        for (String arg : argArray) {
            if (arg.startsWith("-")) {
                for (char c : arg.substring(1).toCharArray()) {
                    switch (c) {
                        case 'a':
                            showAll = true;
                            break;
                        case 'r':
                            reverseOrder = true;
                            break;
                        default:
                            throw new IOException("Invalid option: -" + c);
                    }
                }
            } else {
                pathArg = arg;
            }
        }

        if (!pathArg.isEmpty()) {
            pathToList = Paths.get(pathArg);
            if (!Files.exists(pathToList)) {
                throw new IOException("The specified path does not exist: " + pathToList);
            }
        }

        if (!Files.isDirectory(pathToList)) {
            throw new IOException("The specified path is not a directory: " + pathToList);
        }

        final boolean isShowAll = showAll;
        final boolean isReverseOrder = reverseOrder;

        try (Stream<Path> paths = Files.list(pathToList)) {
            Stream<String> filePaths = paths
                    .filter(path -> isShowAll || !path.getFileName().toString().startsWith("."))
                    .map(path -> path.getFileName().toString() + (Files.isDirectory(path) ? "/" : ""));

            List<String> sortedResults = filePaths
                    .sorted((path1, path2) -> {
                        String name1 = path1.replace("/", "");
                        String name2 = path2.replace("/", "");
                        int comparison = name1.compareTo(name2);

                        if (comparison == 0) {
                            return path1.endsWith("/") ? -1 : 1;
                        }

                        return isReverseOrder ? -comparison : comparison;
                    })
                    .collect(Collectors.toList());

            if (isShowAll) {
                if (isReverseOrder) {
                    sortedResults.add("../");
                    sortedResults.add("./");
                } else {
                    sortedResults.add(0, "./");
                    sortedResults.add(1, "../");
                }
            }

            result.append(String.join("\n", sortedResults));
        }

        return result.toString();
    }

    private String helpMessage() {
        return "Lists files and directories in the specified directory.\n"
                + "-a : Includes hidden files in the listing.\n"
                + "-r : Lists files in reverse alphabetical order.";
    }

    @Override
    public String help() {
        return "Lists files and directories in the specified directory.";
    }
}

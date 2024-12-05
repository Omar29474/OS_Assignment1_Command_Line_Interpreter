package org.CLI;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Cat extends Command {

    @Override
    public String execute(PathObj pathObj, String args) throws Exception {
        String content = "";

        if (args.isEmpty()) {
            System.out.println("Enter content (type 'EXIT' on a new line to finish):");
            Scanner scanner = new Scanner(System.in);
            StringBuilder userInput = new StringBuilder();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().equalsIgnoreCase("EXIT")) break;
                userInput.append(line).append("\n");
            }

            content = userInput.toString().trim();
            if (content.isEmpty()) {
                return "No content entered.";
            }
            return content;
        }

        List<List<String>> filesName = FilesHelper.extractFilesFromString(pathObj, args);

        for (List<String> strings : filesName) {
            Path dist = pathObj.path.resolve(strings.get(1));
            String fileName = strings.get(0);
            Path fullName = dist.resolve(fileName);
            File file = fullName.toFile();

            if (file.exists()) {
                content += new String(Files.readAllBytes(fullName));
            } else {
                throw new Exception("file <" + fileName + "> not found");
            }
        }
        return content;
    }

    @Override
    public String help() {
        return "Concatenates the content of the files and prints it. If no files are specified, input is read from the user.";
    }
}

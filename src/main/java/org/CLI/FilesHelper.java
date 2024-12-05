package org.CLI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class FilesHelper {
    public static List<List<String>> extractFilesFromString(PathObj pathobj, String args) throws IOException {
        Pattern pattern = Pattern.compile("\"([^\"]+)\"|(\\S+)");
        Matcher files = pattern.matcher(args);
        List<List<String>> filesName = new ArrayList<>();
        while (files.find()) {
            String arg = files.group(1) != null ? files.group(1) : files.group(2);
            Path path = Paths.get(arg);
            Path fileName = path.getFileName();
            Path dir;

            if (path.getParent() != null) {
                if (path.getParent().isAbsolute())
                    dir = path.getParent();
                else
                    dir = pathobj.path.resolve(path.getParent());
            } else {
                dir = pathobj.path;
            }

            if (!Files.exists(pathobj.path.resolve(dir))) {
                throw new IOException(dir + " does not exist");
            }
            List<String> fileDetails = new ArrayList<>();
            fileDetails.add(fileName.toString());
            fileDetails.add(dir.toString());
            filesName.add(fileDetails);
        }
        return filesName;
    }
}

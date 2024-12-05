package org.CLI;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class Touch extends Command{

    @Override
    public String execute(PathObj pathobj, String args) throws Exception {
        if(args.isEmpty()) throw new Exception("touch <file1> <file2>");
        List<List<String>> filesName = FilesHelper.extractFilesFromString(pathobj,args);

        for (List<String> strings : filesName) {
            Path dist = pathobj.path.resolve(strings.get(1));
            String fileName = strings.get(0);
            Path fullName = dist.resolve(fileName);
            File file = fullName.toFile();
            if (file.exists()) {
                file.setLastModified(System.currentTimeMillis());
            } else {
                file.createNewFile();
            }
        }
        return "";
    }

    @Override
    public String help() {
        return "Creates a file with each given name";
    }
}

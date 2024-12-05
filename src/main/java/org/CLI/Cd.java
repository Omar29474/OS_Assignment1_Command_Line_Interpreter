package org.CLI;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Cd extends Command{

    @Override
    public String execute(PathObj pathobj, String args) throws Exception {
        if(!args.isEmpty()) {
            List<List<String>> d = FilesHelper.extractFilesFromString(pathobj,args);
            if(d.size() > 1) {
                throw new Exception("Not a valid path");
            }
            Path path;
            path = Paths.get(d.getFirst().get(1),d.getFirst().get(0));
            if(Files.isDirectory(path) && Files.exists(path)){
                pathobj.path = path.normalize();
            }else{
                throw new Exception("Not a valid path");
            }
            return "";
        }else{
            return pathobj.path.toString() + "\n";
        }
    }

    @Override
    public String help() {
        return "Displays the name of or changes the current directory.";
    }
}
package org.CLI;

import java.io.IOException;

public class Pwd extends Command {

    @Override
    public String execute(PathObj pathObj, String args) throws IOException {
        return pathObj.getPath().toString() + "\n";
    }

    @Override
    public String help() {
        return "Print the current working directory.";
    }
}

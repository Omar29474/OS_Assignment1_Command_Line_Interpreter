package org.CLI;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        // Declare commands
        CommandFactory.add("cd",new Cd());
        CommandFactory.add("ls", new Ls());
        CommandFactory.add("mv", new Mv());
        CommandFactory.add("rm", new Rm());
        CommandFactory.add("pwd", new Pwd());
        CommandFactory.add("mkdir", new Mkdir());
        CommandFactory.add("rmdir", new Rmdir());
        CommandFactory.add("touch", new Touch());
        CommandFactory.add("cat", new Cat());

        CLI cli = new CLI(Paths.get("").toAbsolutePath());
        cli.run();
    }
}
package org.CLI;

public abstract class Command {
    public abstract String execute(PathObj pathobj, String args) throws Exception;
    public abstract String help();


}


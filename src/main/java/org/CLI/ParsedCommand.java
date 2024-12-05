package org.CLI;

public class ParsedCommand {
    public String command;
    public String args;
    public String output;
    public String output_type;
    public String input;
    public String input_type;

    @Override
    public String toString() {
        return "ParsedCommand [command=" + command + ", args=" + args + ", output=" + output + ", input="+ input + "]";
    }
}

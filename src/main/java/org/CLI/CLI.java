package org.CLI;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CLI {
    PathObj pathobj;
    Scanner scanner = new Scanner(System.in);


    CLI(Path path){
        pathobj = new PathObj(path);
        this.pathobj.path = path;
    }

    public void run(){
        while(true){
            System.out.print(this.pathobj.path + " ");
            String line = scanner.nextLine();
            if(line.equals("exit")) return;
            if(line.equals("help")) {
                help();
                continue;
            };
            List<ParsedCommand> parsedCommands;
            try {
                parsedCommands = this.parseCommand(line);
                executeCommands(parsedCommands);
            }catch (Exception e){
                System.out.println(e.getClass().getName() + ":");
                System.out.println("\t"+e.getMessage());
            }

        }
    }

    public List<ParsedCommand> parseCommand(String line) throws Exception {
        List<ParsedCommand> parsedCommands = new ArrayList<>();
        String[] commands = line.split("\\|");
        for(int i = 0 ; i < commands.length ; i++){
//            System.out.println("Commands "+ (i+1) + " :-");
            String[] words = commands[i].trim().split("\\s+");
            if(words[0].isEmpty()){
                throw new Exception("Invalid format");
            }
//            System.out.println("\tCommand name :- "+words[0]);
            ParsedCommand pc = new ParsedCommand();
            pc.command = words[0].toLowerCase();
            String arguments = "";
            String input = "";
            String output = "";
            for(int j = 1 ; j < words.length ; j++){
                if(words[j].startsWith("<") ){
                    if(i > 0 ) throw new Exception("Invalid format you can't put input after pipe");
                    if(j+1 < words.length) {
                        input = words[j] + " " + words[++j];
                    }else{
                        throw new Exception("Invalid format no input file");
                    }
                }else if(words[j].startsWith(">")){
                    if(j+1 < words.length)
                        if(i+1 >= commands.length)
                            output = words[j] + " " + words[++j];
                        else{
                            throw new Exception("Invalid format you can't put pipe after output");
                        }
                    else{
                        throw new Exception("Invalid format no output file");
                    }
                }else {
                    if(input.isEmpty() && output.isEmpty())
                        arguments += words[j] + " ";
                    else{
                        throw new Exception("Invalid format arguments for redirector");
                    }
                }
            }
            // Everything is good
            pc.args = arguments.trim();
            if(!input.isEmpty()){
                String[] splits = input.split(" ");
                pc.input = splits[1];
                if(splits[0].equals("<")){
                    pc.input_type = "input";
                }else{
                    throw new Exception("Invalid input redirector");
                }
            }

            if(!output.isEmpty()){
                String[] splits = output.split(" ");
                pc.output = splits[1];
                if(splits[0].equals(">")){
                    pc.output_type = "output";
                }else if(splits[0].equals(">>")){
                    pc.output_type = "append";
                }else{
                    throw new Exception("Invalid output redirector");
                }
            }

            parsedCommands.add(pc);
        }
        return parsedCommands;
    }


    public String readFile(String path) throws Exception{
        Path file = Paths.get(path);
        Path desti = pathobj.path;
        if(file.isAbsolute()){
            if(Files.exists(file) && !Files.isDirectory(file) && Files.isReadable(file))
                desti = file;
            else
                throw new Exception("File ("+path+") does not exist");
        }else{
            desti = desti.resolve(file);
            if(!(Files.exists(desti) && !Files.isDirectory(desti) && Files.isReadable(desti)))
                throw new Exception("File ("+path+") does not exist");
        }
        // Valid and readable file
        return new String(Files.readAllBytes(desti));
    }

    public void writeFile(String path,String content,boolean append) throws Exception{
        Path file = Paths.get(path);
        Path desti = pathobj.path;
        String oldContent = "";
        if(Files.isDirectory(file))
            throw new Exception("("+path+") is not a file");

        if(file.isAbsolute())
            desti = file;
        else
            desti = desti.resolve(file);


        if(!Files.exists(desti.getParent()))
            throw new Exception("Path ("+desti+") is not exist");

        if(append){
            try {
                oldContent = readFile((desti.toString()));
            }catch (Exception _){};
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(desti.toString()));
        writer.write(oldContent + content);
        writer.close();
    }

    public void executeCommands(List<ParsedCommand> ParsedCommands) throws Exception {
        if(ParsedCommands.isEmpty()) return;
        for(ParsedCommand pc : ParsedCommands){
            if(!CommandFactory.exists(pc.command))
                throw new Exception(pc.command + " is not recognized as an internal or external command");
        }

        String result = "";
        for(ParsedCommand pc : ParsedCommands){
            // piping
            pc.args += " " + result;
            pc.args = pc.args.trim();
            if(pc.input != null && !pc.input.isEmpty()){
                String content = readFile(pc.input);
                pc.args += " " + content;
                pc.args = pc.args.trim();
            }
            Command cmd = CommandFactory.get(pc.command);
            result = cmd.execute(this.pathobj, pc.args);
            if(pc.output != null && !pc.output.isEmpty()){
                writeFile(pc.output,result, pc.output_type.equals("append"));
                result = "";
            }
        }

        if(!result.isEmpty())
            System.out.println(result);

    }

    public void help() {
        System.out.println("-------\t\t-----------");
        System.out.println("COMMAND\t\tDESCRIPTION");
        System.out.println("-------\t\t-----------");

        for (String commandName : CommandFactory.commands()) {
            String description = CommandFactory.get(commandName).help();
            System.out.printf("%-10s\t%s%n", commandName.toUpperCase(), description);
        }
    }

}

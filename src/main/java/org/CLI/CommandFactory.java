package org.CLI;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class CommandFactory {
    private CommandFactory(){};
    private static final Map<String, Command> commandsMap = new HashMap<>();

    public static void add (String name , Command cl){
        commandsMap.put(name , cl);
    }

    public static Set<String> commands(){
        return commandsMap.keySet();
    }

    public static Command get(String name ){
        return commandsMap.get(name);
    }

    public static boolean exists(String name ){
        return commandsMap.containsKey(name);
    }
}

package pl.miloszgilga;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;


public enum AvailableCommands {
    MUSIC_PLAY("play"),
    MUSIC_SKIP("skip"),
    HELP("help");

    private final String commandName;

    AvailableCommands(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public static List<String> getAllCommands() {
        return Arrays
                .stream(AvailableCommands.values()).map(AvailableCommands::getCommandName)
                .collect(Collectors.toList());
    }
}
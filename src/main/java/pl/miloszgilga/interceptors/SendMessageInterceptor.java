package pl.miloszgilga.interceptors;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;

import pl.miloszgilga.AvailableCommands;
import pl.miloszgilga.exceptions.UnrecognizedCommandException;
import static pl.miloszgilga.FranekBot.DEF_PREFIX;
import static pl.miloszgilga.AvailableCommands.getAllCommands;


public class SendMessageInterceptor {

    private final List<String> allCommands = getAllCommands();
    private static SendMessageInterceptor instance = null;

    private SendMessageInterceptor() { }

    public List<String> validateRequestWithCommandType(MessageReceivedEvent event, AvailableCommands command) {
        try {
            if (!event.getAuthor().isBot() && event.getMessage().getContentRaw().contains(DEF_PREFIX)) {
                List<String> prefixAndArgs = Arrays.stream(event.getMessage().getContentRaw().split(" "))
                        .collect(Collectors.toList());

                String commandName = prefixAndArgs.get(0).replace(DEF_PREFIX, "");
                if (allCommands.stream().noneMatch(el -> el.equals(commandName))) {
                    throw new UnrecognizedCommandException(event);
                }
                if (command.getCommandName().equals(commandName)) {
                    return prefixAndArgs;
                }
            }
        } catch (UnrecognizedCommandException ex) {
            System.out.println(ex.getMessage());
        }
        return new ArrayList<>();
    }

    public static SendMessageInterceptor getSingletonInstance() {
        if (instance == null) {
            instance = new SendMessageInterceptor();
        }
        return instance;
    }
}
package pl.miloszgilga.executers;

import ca.tristan.jdacommands.ICommand;
import ca.tristan.jdacommands.ExecuteArgs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;


public class MuteUserExecutor implements ICommand {

    @Override
    public void execute(ExecuteArgs executeArgs) {
        if (executeArgs.getArgs().length > 0) {
            Member user = executeArgs.getGuild().getMemberById(executeArgs.getArgs()[1]
                    .replace("<@", "")
                    .replace(">", ""));
            Role mutedRole = executeArgs.getGuild().getRolesByName("Muted", true).get(0);
            if (user != null) {
                if (!user.getRoles().contains(mutedRole)) {
                    executeArgs.getGuild().addRoleToMember(user, mutedRole).queue();
                } else {
                    executeArgs.getGuild().removeRoleFromMember(user, mutedRole).queue();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String helpMessage() {
        return null;
    }

    @Override
    public boolean needOwner() {
        return false;
    }
}
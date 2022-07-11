package pl.miloszgilga;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import io.github.cdimascio.dotenv.Dotenv;
import javax.security.auth.login.LoginException;

import static pl.miloszgilga.AvailableCommands.HELP;

import pl.miloszgilga.executors.AudioPlayCommandExecutor;
import pl.miloszgilga.executors.AudioSkippedCommandExecutor;


public class FranekBot {

    private static final String BOT_ID = Dotenv.load().get("BOT_ID");
    public static final String DEV_GUILD = Dotenv.load().get("DEV_GUILD");
    public static final String PROD_GUILD = Dotenv.load().get("PROD_GUILD");

    public static final String DEF_PREFIX = "$";

    public static void main(String[] args) throws LoginException {
        JDABuilder
                .createDefault(BOT_ID)
                .enableCache(CacheFlag.VOICE_STATE)
                .setActivity(Activity.listening(DEF_PREFIX + HELP.getCommandName()))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(
                        new AudioPlayCommandExecutor(),
                        new AudioSkippedCommandExecutor()
                )
                .build();
    }
}
package pl.miloszgilga;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import ca.tristan.jdacommands.JDACommands;
import io.github.cdimascio.dotenv.Dotenv;

import javax.security.auth.login.LoginException;

import pl.miloszgilga.executers.MuteUserExecutor;
import pl.miloszgilga.executers.AudioCommandExecuter;


public class FranekBot {

    private static final String BOT_ID = Dotenv.load().get("BOT_ID");

    private static final String DEF_PREFIX = "~";
    private static final String BOT_STATUS = DEF_PREFIX + "help";

    public static void main(String[] args) throws LoginException {
        JDACommands jdaCommands = new JDACommands(DEF_PREFIX);
        jdaCommands.registerCommand(new AudioCommandExecuter());
        jdaCommands.registerCommand(new MuteUserExecutor());

        JDABuilder
                .createDefault(BOT_ID)
                .enableCache(CacheFlag.VOICE_STATE)
                .setActivity(Activity.listening(BOT_STATUS))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(jdaCommands)
                .build();
    }
}
package pl.miloszgilga.executers;

import ca.tristan.jdacommands.ExecuteArgs;
import ca.tristan.jdacommands.ICommand;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URI;
import java.net.URISyntaxException;

import pl.miloszgilga.audioplayer.PlayerManager;

import static pl.miloszgilga.messagesconfig.AudioPlayerMessages.HELP_INFO_AUDIO_BOT;
import static pl.miloszgilga.messagesconfig.AudioPlayerMessages.RUN_AUDIO_BOT;


public class AudioCommandExecuter implements ICommand {

    private final PlayerManager playerManager = PlayerManager.getInstance();

    @Override
    public void execute(ExecuteArgs executeArgs) {
        if (executeArgs.getArgs().length > 1) {
            if (!executeArgs.getMemberVoiceState().inAudioChannel()) {
                executeArgs.getTextChannel().sendMessage(RUN_AUDIO_BOT).queue();
                return;
            }

            if (!executeArgs.getSelfVoiceState().inAudioChannel()) {
                final AudioManager audioManager = executeArgs.getGuild().getAudioManager();
                final VoiceChannel memberChannel = (VoiceChannel) executeArgs.getMemberVoiceState().getChannel();
                audioManager.openAudioConnection(memberChannel);
            }

            String link = executeArgs.getArgs()[1];
            if (!isUrl(link)) {
                link = "ytsearch:" + link + " audio";
            }
            playerManager.loadAndPlay(executeArgs.getTextChannel(), link);
        } else {
            executeArgs.getTextChannel().sendMessage(HELP_INFO_AUDIO_BOT).queue();
        }
    }

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String helpMessage() {
        return HELP_INFO_AUDIO_BOT;
    }

    @Override
    public boolean needOwner() {
        return false;
    }
}
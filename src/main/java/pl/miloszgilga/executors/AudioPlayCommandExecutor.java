package pl.miloszgilga.executors;

import jdk.jfr.Description;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.exceptions.UserOnVoiceChannelNotFoundException;
import pl.miloszgilga.interceptors.SendMessageInterceptor;
import pl.miloszgilga.exceptions.IllegalCommandArgumentsException;
import static pl.miloszgilga.FranekBot.DEF_PREFIX;
import static pl.miloszgilga.AvailableCommands.MUSIC_PLAY;
import static pl.miloszgilga.Utils.isUrl;


public class AudioPlayCommandExecutor extends ListenerAdapter {

    private final SendMessageInterceptor interceptor = SendMessageInterceptor.getSingletonInstance();
    private final PlayerManager playerManager = PlayerManager.getSingletonInstance();

    @Override
    @Description("command: <[prefix]play [music link or description]>")
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final List<String> allArgs = interceptor.validateRequestWithCommandType(event, MUSIC_PLAY);
        if (!allArgs.isEmpty()) {
            try {
                if (allArgs.size() < 2) {
                    throw new IllegalCommandArgumentsException(event, String.format(
                            "`%s [link lub nazwa piosenki]`", DEF_PREFIX + MUSIC_PLAY.getCommandName()));
                }
                if (!Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).inAudioChannel()) {
                    throw new UserOnVoiceChannelNotFoundException(event, "Aby możliwe było odtworzenie piosenki, " +
                            "musisz znajdować się na kanale głosowym.");
                }

                final AudioManager audioManager = event.getGuild().getAudioManager();
                final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();
                audioManager.openAudioConnection(memberChannel);

                allArgs.remove(0);
                String withoutPrefix  = String.join(" ", allArgs);
                if (!isUrl(withoutPrefix) && allArgs.size() > 2) {
                    withoutPrefix = "ytsearch: " + withoutPrefix + " audio";
                } else {
                    withoutPrefix = withoutPrefix.replaceAll(" ", "");
                }
                playerManager.loadAndPlay(event.getTextChannel(), withoutPrefix);

            } catch (UserOnVoiceChannelNotFoundException | IllegalCommandArgumentsException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
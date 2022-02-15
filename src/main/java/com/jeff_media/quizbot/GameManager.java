package com.jeff_media.quizbot;

import com.jeff_media.quizbot.data.Game;
import lombok.Getter;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GameManager extends ListenerAdapter {

    private static final QuizBot main = QuizBot.getInstance();
    @Getter private Map<String,Game> currentGames;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getChannelType() != ChannelType.TEXT) return;
        String channelId = event.getTextChannel().getId();
        Game game = null;
        for(Map.Entry<String,Game> entry : currentGames.entrySet()) {
            if(entry.getKey().equals(channelId)) {
                game = entry.getValue();
                game.handleMessage(event);
                break;
            }
        }
        if(game != null) {
            if(game.isFinished()) {
                currentGames.remove(channelId);
            }
        }
    }
}

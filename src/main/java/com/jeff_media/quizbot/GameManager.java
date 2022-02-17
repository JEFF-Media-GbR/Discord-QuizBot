package com.jeff_media.quizbot;

import com.jeff_media.quizbot.data.Game;
import com.jeff_media.quizbot.exceptions.CategoryNotFoundException;
import lombok.Getter;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GameManager extends ListenerAdapter {

    private static final QuizBot main = QuizBot.getInstance();
    @Getter private Map<String,Game> currentGames = new HashMap<>();

    {
        System.out.println("GameManager started, listening for messages...");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        if(event.getChannelType() != ChannelType.TEXT) return;

        TextChannel channel = event.getTextChannel();
        String channelId = channel.getId();
        Game game = null;

        Command command = Command.fromMessage(event.getMessage().getContentRaw());
        String[] split = event.getMessage().getContentRaw().split(" ");
        if(command != null) {
            switch (command) {
                case HELP -> sendHelpMessage(channel);
                case STOP -> stopGame(event.getAuthor(), channelId);
                case START -> {
                    if(split.length < 3) {
                        sendErrorMessage(channel);
                    } else {
                        startGame(event.getAuthor(), split[2], channelId,0);
                    }
                }
                default -> sendErrorMessage(channel);
            }

            return;
        }

        for(Map.Entry<String,Game> entry : currentGames.entrySet()) {
            if(entry.getKey().equals(channelId)) {
                System.out.println("There's a game running in this channel, forwarding answer to the game...");
                game = entry.getValue();
                game.handleMessage(event.getAuthor(),event.getMessage().getContentRaw());
                break;
            }
        }
        /*if(game != null) {
            if(game.isFinished()) {
                System.out.println("The game has finished. Removing it...");
                Messages.sendWin(channelId,event.getAuthor());
                game.stop();
                currentGames.remove(channelId);
            }
        }*/
    }

    private void startGame(User user, String category, String channelId, int threshold) {
        if(currentGames.containsKey(channelId)) {
            Messages.sendError(channelId, "Quiz already running",String.format("Silly %s! There's already a quiz running in this channel.",user.getAsMention()));
            return;
        }
        try {
            Game game = startGame(channelId, category, threshold);
            currentGames.put(channelId, game);
            Messages.sendEmbed(channelId, Color.YELLOW,"New quiz started!",String.format("%s started a new quiz: %s",user.getAsMention(),category));
            game.sendNextQuestion();
        } catch (CategoryNotFoundException e) {
            Messages.sendError(channelId, "Could not start quiz",String.format("Silly %s! There is no quiz called %s",user.getAsMention(),category));
        }
    }

    private Game startGame(String channelId, String category, int threshold) throws CategoryNotFoundException {
        File file = new File("categories",category+".yml");
        if(!file.exists()) {
            throw new CategoryNotFoundException();
        }
        return Game.fromFile(channelId, file, threshold);
    }

    private void stopGame(User user, String channelId) {
        if(!currentGames.containsKey(channelId)) {
            Messages.sendError(channelId,"No quiz running",String.format("Silly %s! You can't stop this quiz because no quiz is running.",user.getAsMention()));
        } else {
            Messages.sendError(channelId,"Quiz stopped",String.format("%s has stopped the current quiz.",user.getAsMention()));
            currentGames.get(channelId).stop();
            currentGames.remove(channelId);
        }
    }

    private void sendErrorMessage(TextChannel channel) {
        channel.sendMessage("(insert error message)").queue();
    }

    private void sendHelpMessage(TextChannel channel) {
        channel.sendMessage("(insert help message)").queue();
    }
}

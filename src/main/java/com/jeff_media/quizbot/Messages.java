package com.jeff_media.quizbot;

import com.jeff_media.quizbot.data.Game;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class Messages {

    private static final QuizBot main = QuizBot.getInstance();

    public static void sendEmbed(String channel, Color color, String title, String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        if(color != null) {
            builder.setColor(color);
        }
        builder.setDescription(message);
        main.getJda().getTextChannelById(channel).sendMessageEmbeds(builder.build()).queue();
    }

    public static void sendError(String channel, String title, String message) {
        sendEmbed(channel, Color.RED, title, message);
    }

    public static void sendQuestion(Game game) {
        sendEmbed(game.getChannel(), null, "Question #" + game.getCurrentQuestionNumber(), game.getCurrentQuestion().getQuestion());
    }

}

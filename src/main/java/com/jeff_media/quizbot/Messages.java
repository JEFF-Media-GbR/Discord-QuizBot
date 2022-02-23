package com.jeff_media.quizbot;

import com.jeff_media.quizbot.data.Game;
import com.jeff_media.quizbot.data.Question;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
        sendEmbed(game.getChannel(), null, "Question #" + game.getCurrentQuestionNumber()+1, game.getCurrentQuestion().getQuestion());
    }

    public static void sendCorrect(String channel, User author) {
        sendEmbed(channel, Color.GREEN, "Correct!", String.format("That's correct, %s!",author.getAsMention()));
    }

    public static void sendWin(String channel, Game game, User author) {
        Map.Entry<User,List<Question>> winningEntry = game.getAnsweredQuestions().entrySet().stream().max(new Comparator<Map.Entry<User, List<Question>>>() {
            @Override
            public int compare(Map.Entry<User, List<Question>> o1, Map.Entry<User, List<Question>> o2) {
                return Integer.compare(o1.getValue().size(), o2.getValue().size());
            }
        }).orElse(null);
        if(winningEntry != null) {

        }
        sendEmbed(channel, Color.GREEN,"GG " + author.getAsMention(), author.getAsMention() + " has won this quiz!");
    }
}

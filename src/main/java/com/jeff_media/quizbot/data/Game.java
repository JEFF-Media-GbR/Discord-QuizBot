package com.jeff_media.quizbot.data;

import com.jeff_media.quizbot.Messages;
import com.jeff_media.quizbot.utils.FileUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@ToString
public class Game {

    @Getter
    private final String channel;
    @Getter
    private final List<Question> questions;
    @Getter
    private final Map<User, List<Question>> answeredQuestions = new HashMap<>();
    @Getter
    private final int winThreshold;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Getter
    private ScheduledFuture<?> task;
    @Getter
    private int currentQuestionNumber = -1;

    public static Game fromFile(String channel, File file, int winThreshold) {
        Map<String, Object> yaml = FileUtils.loadYaml(file);
        List<Question> questions = getQuestions((List<Map<String, Object>>) yaml.get("questions"));
        if (winThreshold == 0) {
            winThreshold = (int) yaml.getOrDefault("win-threshold", 1);
        }
        return new Game(channel, questions, winThreshold);
    }

    private static List<Question> getQuestions(List<Map<String, Object>> list) {
        ArrayList<Question> questions = new ArrayList<>();
        for (Map<String, Object> question : list) {
            questions.add(new Question(question));
        }
        Collections.shuffle(questions);
        return questions;
    }

    public void sendNextQuestion() {

        if (task != null) {
            task.cancel(true);
        }

        if (!hasQuestionsLeft()) {
            Messages.sendEmbed(channel, Color.BLACK, "That's it", "Quiz has ended.");
            return;
        }


        getNextQuestion();



        Messages.sendQuestion(this);

        Runnable runnable = () -> {
            //sendNextQuestion();
            handleMessage(null,null);
        };

        task = scheduler.schedule(runnable, 15, TimeUnit.SECONDS);
    }

    public Question getNextQuestion() {
        if (!hasQuestionsLeft()) {
            throw new RuntimeException("No more questions left");
        }
        currentQuestionNumber++;
        return questions.get(currentQuestionNumber);
    }

    public Question getCurrentQuestion() {
        return questions.get(currentQuestionNumber);
    }

    public boolean hasQuestionsLeft() {
        return currentQuestionNumber < questions.size() - 1;
    }

    public void addCorrectAnswer(User user) {
        if (answeredQuestions.containsKey(user)) {
            answeredQuestions.get(user).add(getCurrentQuestion());
        } else {
            List<Question> list = new ArrayList<>();
            list.add(getCurrentQuestion());
            answeredQuestions.put(user, list);
        }
    }

    public void handleMessage(@Nullable User user, @Nullable String message) {
        //System.out.println("Game handling message: " + message);
        Question question = getCurrentQuestion();
        if (question == null) return;
        //System.out.println("Got answer: " + event.getMessage().getContentRaw());

        if (user == null && message == null) {
            Messages.sendEmbed(channel, Color.BLACK, "Noone?", "Correct answer would have been: " + getCurrentQuestion().getCorrectAnswer());
            return;
        }

        if (question.isCorrect(message)) {
            System.out.println("Correct answer!");
            addCorrectAnswer(user);
            Messages.sendCorrect(channel, user);
            if (!isFinished()) {
                sendNextQuestion();
            } else {
                Messages.sendWin(channel, this, user);
            }
        } else {
            System.out.println("Wrong answer, correct would have been: " + question.getCorrectAnswer());
        }
    }

    public boolean isFinished() {
        System.out.println("Checking whether game is finished...");
        if (!hasQuestionsLeft()) {
            System.out.println("  Game is finished, we don't have questions left.");
            return true;
        }
        for (Map.Entry<User, List<Question>> entry : answeredQuestions.entrySet()) {
            if (entry.getValue().size() >= winThreshold) {
                System.out.println("  Game has finished, threshold has been reached.");
                return true;
            }
        }
        return false;
    }

    public void stop() {
        if (task != null) task.cancel(true);
    }

    public User getWinner() {
        Map.Entry<User,List<Question>> winner = answeredQuestions.entrySet().stream().max(Comparator.comparingInt(o -> o.getValue().size())).orElse(null);
        if(winner == null) return null;
        return winner.getKey();
    }
}

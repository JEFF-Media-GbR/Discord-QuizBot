package com.jeff_media.quizbot.data;

import com.jeff_media.quizbot.utils.FileUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.*;

@RequiredArgsConstructor
@ToString
public class Game {

    @Getter private final String channel;
    @Getter private final List<Question> questions;
    @Getter private final Map<Member,List<Question>> answeredQuestions = new HashMap<>();
    @Getter private final int winThreshold;
    @Getter private int currentQuestionNumber = 0;
    //@Getter private Question currentQuestion = null;

    public static Game fromFile(String channel, File file, int winThreshold) {
        Map<String,Object> yaml = FileUtils.loadYaml(file);
        List<Question> questions = getQuestions((List<Map<String, Object>>) yaml.get("questions"));
        if(winThreshold == 0) {
            winThreshold = (int) yaml.getOrDefault("win-threshold",1);
        }
        return new Game(channel, questions, winThreshold);
    }

    private static List<Question> getQuestions(List<Map<String,Object>> list) {
        ArrayList<Question> questions = new ArrayList<>();
        for(Map<String,Object> question : list) {
            questions.add(new Question(question));
        }
        Collections.shuffle(questions);
        return questions;
    }

    public Question getNextQuestion() {
        if(!hasQuestionsLeft()) {
            throw new RuntimeException("No more questions left");
        }
        return questions.get(currentQuestionNumber++);
    }

    public Question getCurrentQuestion() {
        return questions.get(currentQuestionNumber);
    }

    public boolean hasQuestionsLeft() {
        return currentQuestionNumber < questions.size();
    }

    public void addCorrectAnswer(Member member) {
        if(answeredQuestions.containsKey(member)) {
            answeredQuestions.get(member).add(getCurrentQuestion());
        } else {
            List<Question> list = new ArrayList<>();
            list.add(getCurrentQuestion());
            answeredQuestions.put(member, list);
        }
    }

    public void handleMessage(MessageReceivedEvent event) {

    }

    public boolean isFinished() {
        if(!hasQuestionsLeft()) return true;
        for(Map.Entry<Member,List<Question>> entry : answeredQuestions.entrySet()) {
            if(entry.getValue().size() >= winThreshold) return true;
        }
        return false;
    }

}

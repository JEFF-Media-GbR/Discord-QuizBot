package com.jeff_media.quizbot.data;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ToString
public class Question {

    @Getter private final String question;
    @Getter private final List<?> correctAnswers;

    public Question(Map<String,Object> map) {
        this.question = (String) map.get("question");
        this.correctAnswers = (List<?>) map.get("answers");
    }

    public boolean isCorrect(String input) {
        input = input.toLowerCase(Locale.ROOT);
        for(Object answer : correctAnswers) {
            if(input.contains(String.valueOf(answer).toLowerCase(Locale.ROOT))) return true;
        }
        return false;
    }

    public String getCorrectAnswer() {
        return String.valueOf(correctAnswers.get(0));
    }

}

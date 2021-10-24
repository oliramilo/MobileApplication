package com.curtin.mathtest.Model;

import java.util.List;

public class Question {
    private String question;
    private int[] possibleAnswers;
    private int answer;
    private int timeRemaining;
    public Question(String question, int answer, int[] possibleAnswers, int timeRemaining) {
        this.question = question;
        this.possibleAnswers = possibleAnswers;
        this.answer = answer;
        this.timeRemaining = timeRemaining;
    }

    public String getQuestion() {
        return question;
    }

    public int[] getPossibleAnswers() {
        return possibleAnswers;
    }

    public int getAnswer() {
        return answer;
    }

    public int getTimeToAnswer() {
        return timeRemaining;
    }

}

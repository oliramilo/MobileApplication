package com.curtin.mathtest.Model;

public class Answer {
    private Question question;
    private String answer;
    private boolean skipped;
    public Answer(Question question,String answer, boolean skipped) {
        this.question = question;
        this.answer = answer;
        this.skipped = skipped;
    }

    public Question getQuestion() {
        return this.question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public boolean isCorrect() {
        boolean correct = false;
        try {
            correct = question.getAnswer() == Integer.parseInt(answer);
        }
        catch(NumberFormatException ignored) {
        }
        return correct;
    }

    public boolean isSkipped() {
        return skipped;
    }

}

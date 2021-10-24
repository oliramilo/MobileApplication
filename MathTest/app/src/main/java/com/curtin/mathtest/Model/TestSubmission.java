package com.curtin.mathtest.Model;

public class TestSubmission {
    private String user;
    private String date;
    private String timeStarted;
    private String duration;
    private int score;

    public TestSubmission(String user, String date, String timeStarted, String duration,int score) {
        this.user = user;
        this.date = date;
        this.timeStarted = timeStarted;
        this.duration = duration;
        this.score = score;
    }

    public String getDateStarted() {
         return date;
    }
    public String getTimeStarted() {
        return timeStarted;
    }
    public String getTestDuration() {
        return duration;
    }
    public String getUser() {
        return this.user;
    }
    public int getScore() {
        return this.score;
    }
    public String toString() {
        return  "Contact: " + user + "\n" + "Date started: "
                + date + "\n" + "Time started: " + timeStarted + "\n" +
                "Duration (Seconds): " + duration + "\n" + "Score: " + score;
    }
}

package booksystem;

import java.util.List;

public class SurveyResponses {
    private int userID;
    private int questionID;
    private String response;
    private int answerOptionID;

    public SurveyResponses(int userID, int questionID, int answerOptionID, String response){
        this.userID = userID;
        this.questionID = questionID;
        this.answerOptionID = answerOptionID;
        this.response = response;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getAnswerOptionID(){return answerOptionID;}

    public void setAnswerOptionID(int answerOptionID) {
        this.answerOptionID = answerOptionID;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "SurveyResponse{" +
                "userId=" + userID +
                ", questionId=" + questionID +
                ", response='" + response + '\'' +
                '}';
    }
}

package booksystem;

import java.util.List;

public class SurveyResponses {
    private int userID;
    private int questionID;
    private String response;

    public SurveyResponses(int userID, int questionID, String response){
        this.userID = userID;
        this.questionID = questionID;
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

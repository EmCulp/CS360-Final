package booksystem;

import java.util.List;

public class SurveySubmission {
    private int userId;
    private List<SurveyResponse> responses;

    public SurveySubmission(int userId, List<SurveyResponse> responses){
        this.userId =  userId;
        this.responses = responses;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<SurveyResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<SurveyResponse> responses) {
        this.responses = responses;
    }
}

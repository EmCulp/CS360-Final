package booksystem;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SurveyResponse implements Serializable {
    private int userID;

    @SerializedName("question_id")
    private int qid;
    private List<String> answer;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "SurveyResponse{" +
                "userID=" + userID +
                ", qid=" + qid +
                ", answer=" + answer +
                '}';
    }
}

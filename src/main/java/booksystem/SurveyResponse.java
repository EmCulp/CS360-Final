/*******************************************************************
 * SurveyResponse *
 * *
 * PROGRAMMER: [Emily] *
 * COURSE: [CS360 - Analysis / Algorithms] *
 * DATE: [2025-05-06] *
 * REQUIREMENT: Final *
 * *
 * DESCRIPTION: *
 * The SurveyResponse class represents an individual user's response to a survey question. It contains the user ID, question ID, *
 * and a list of answers provided by the user. The class is serializable and uses the Gson library annotations for JSON parsing. *
 * It includes getter and setter methods for each field and overrides the `toString` method for easy representation. *
 * *
 * COPYRIGHT: This code is copyright (C) 2025 Emily *
 * *
 * CREDITS: *
 * Sources used: [List any sources used] *
 * *
 *******************************************************************/

package booksystem;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SurveyResponse implements Serializable {
    private int userID;

    @SerializedName("question_id")
    private int qid;
    private List<String> answer;

    /**********************************************************
     * METHOD: getUserID *
     * DESCRIPTION: This method returns the user ID of the person who submitted the response. *
     * PARAMETERS: None *
     * RETURN VALUE: int - The user ID. *
     **********************************************************/
    public int getUserID() {
        return userID;
    }


    /**********************************************************
     * METHOD: setUserID *
     * DESCRIPTION: This method sets the user ID for the response. *
     * PARAMETERS: int userID - The user ID to be set. *
     * RETURN VALUE: None *
     **********************************************************/
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**********************************************************
     * METHOD: getQid *
     * DESCRIPTION: This method returns the question ID of the survey question being answered. *
     * PARAMETERS: None *
     * RETURN VALUE: int - The question ID. *
     **********************************************************/
    public int getQid() {
        return qid;
    }

    /**********************************************************
     * METHOD: setQid *
     * DESCRIPTION: This method sets the question ID for the survey response. *
     * PARAMETERS: int qid - The question ID to be set. *
     * RETURN VALUE: None *
     **********************************************************/
    public void setQid(int qid) {
        this.qid = qid;
    }


    /**********************************************************
     * METHOD: getAnswer *
     * DESCRIPTION: This method returns the list of answers provided by the user for the question. *
     * PARAMETERS: None *
     * RETURN VALUE: List<String> - A list of answers. *
     **********************************************************/
    public List<String> getAnswer() {
        return answer;
    }

    /**********************************************************
     * METHOD: setAnswer *
     * DESCRIPTION: This method sets the list of answers provided by the user. *
     * PARAMETERS: List<String> answer - The list of answers to be set. *
     * RETURN VALUE: None *
     **********************************************************/
    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    /**********************************************************
     * METHOD: toString *
     * DESCRIPTION: This method provides a string representation of the SurveyResponse object. It includes the user ID, *
     * question ID, and the list of answers. *
     * PARAMETERS: None *
     * RETURN VALUE: String - A string representation of the SurveyResponse object. *
     **********************************************************/
    @Override
    public String toString() {
        return "SurveyResponse{" +
                "userID=" + userID +
                ", qid=" + qid +
                ", answer=" + answer +
                '}';
    }
}

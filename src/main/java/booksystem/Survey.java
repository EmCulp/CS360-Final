/*******************************************************************
 * Survey *
 * *
 * PROGRAMMER: [Emily] *
 * COURSE: [CS360 - Analysis / Algorithms] *
 * DATE: [2025-05-06] *
 * REQUIREMENT: Final *
 * *
 * DESCRIPTION: *
 * The Survey class represents a survey containing a list of survey responses. It implements the Serializable *
 * interface, allowing it to be serialized and deserialized for storage or transmission. This class has a single field, *
 * `resp`, which is a list of `SurveyResponse` objects. The class provides getter and setter methods for accessing and *
 * modifying the list of responses. *
 * *
 * COPYRIGHT: This code is copyright (C) 2025 Emily *
 * *
 * CREDITS: *
 * ChatGPT *
 * *
 *******************************************************************/

package booksystem;

import java.io.Serializable;
import java.util.List;

public class Survey implements Serializable {
    private List<SurveyResponse> resp;

    /**********************************************************
     * METHOD: getResp *
     * DESCRIPTION: This method returns the list of survey responses. *
     * PARAMETERS: None *
     * RETURN VALUE: List<SurveyResponse> - The list of SurveyResponse objects representing the responses to the survey. *
     **********************************************************/
    public List<SurveyResponse> getResp() {
        return resp;
    }

    /**********************************************************
     * METHOD: setResp *
     * DESCRIPTION: This method sets the list of survey responses for the survey. *
     * PARAMETERS: List<SurveyResponse> resp - The list of SurveyResponse objects to be set as the responses. *
     * RETURN VALUE: None *
     **********************************************************/
    public void setResp(List<SurveyResponse> resp) {
        this.resp = resp;
    }
}

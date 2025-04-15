package booksystem;

import java.util.List;

public class SurveyController {
    public void handleSurveySubmission(String json){
        List<SurveyResponses> responses = SurveyParser.parseJson(json);
        SurveyDAO dao = new SurveyDAO();
        dao.saveSurveyResponses(responses);
    }
}

package booksystem;

import java.io.Serializable;
import java.util.List;

public class Survey implements Serializable {
    private List<SurveyResponse> resp;

    public List<SurveyResponse> getResp() {
        return resp;
    }

    public void setResp(List<SurveyResponse> resp) {
        this.resp = resp;
    }
}

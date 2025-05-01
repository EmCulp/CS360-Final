package booksystem;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;

public class SurveyResponsesDeserializer implements JsonDeserializer<SurveyResponse> {

    @Override
    public SurveyResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        SurveyResponse surveyResponse = new SurveyResponse();

        // Deserialize the question_id
        surveyResponse.setQid(jsonObject.get("question_id").getAsInt());

        // Handle the answer field - could be a single string or a list of strings
        JsonElement answerElement = jsonObject.get("answer");
        if (answerElement.isJsonArray()) {
            // If the answer is an array (multiple choices)
            surveyResponse.setAnswer(context.deserialize(answerElement, List.class));
        } else {
            // If the answer is a single string (just one choice)
            List<String> answerList = new ArrayList<>();
            answerList.add(answerElement.getAsString());
            surveyResponse.setAnswer(answerList);
        }

        return surveyResponse;
    }
}


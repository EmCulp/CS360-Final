package booksystem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SurveyParser {
    public static List<SurveyResponses> parseJson(String json){
        Gson gson = new Gson();
        Type listType = new TypeToken<List<SurveyResponses>>(){}.getType();
        return gson.fromJson(json, listType);
    }
}

package booksystem;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static spark.Spark.post;

public class SurveyController {
    private SurveyDAO surveyDAO;

    public SurveyController(){
        surveyDAO = new SurveyDAO();
    }

    public void handleSurveySubmission(String json, Integer userId) throws SQLException {
        if (!isValidUserId(userId)) {
            System.out.println("Invalid user_id: " + userId);
            return;
        }

        SurveyDAO dao = new SurveyDAO();

        int submissionId = dao.insertSubmission(userId);

        Gson gson = new Gson();
        Survey responses = gson.fromJson(json, Survey.class);

        for (SurveyResponse response : responses.getResp()) {
            System.out.println("Parsed response -> Question ID: " + response.getQid() + ", Answer: " + response.getAnswer());
            if (response.getQid() <= 0) {
                System.out.println("Invalid question_id: " + response.getQid());
                continue;
            }

            int correctedQuestionId = response.getQid();
            // Process the answers once, passing the entire list at once
            dao.saveSurveyResponses(response, correctedQuestionId, userId, submissionId);
        }
        dao.saveUserPreferences(userId, submissionId);
    }

    public boolean isValidUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // If the count is greater than 0, the user_id exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Return false if there's an issue or the user_id doesn't exist
    }

}

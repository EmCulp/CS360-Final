
/*******************************************************************
 * SurveyController *
 * *
 * PROGRAMMER: [Emily] *
 * COURSE: [CS360 - Analysis / Algorithms] *
 * DATE: [2025-05-06] *
 * REQUIREMENT: Final *
 * *
 * DESCRIPTION: *
 * The SurveyController class handles the logic for submitting survey data. It communicates with the SurveyDAO class to *
 * manage survey submissions, validate user IDs, and process responses. It includes methods for handling survey submissions, *
 * validating user IDs, and interacting with the database. The class uses the Gson library to parse JSON data into Java objects. *
 * *
 * COPYRIGHT: This code is copyright (C) 2025 Emily *
 * *
 * CREDITS: *
 * Sources used: [List any sources used] *
 * *
 *******************************************************************/

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

    /**********************************************************
     * METHOD: SurveyController *
     * DESCRIPTION: Constructor for the SurveyController class. Initializes the SurveyDAO instance. *
     * PARAMETERS: None *
     * RETURN VALUE: None *
     **********************************************************/
    public SurveyController(){
        surveyDAO = new SurveyDAO();
    }

    /**********************************************************
     * METHOD: handleSurveySubmission *
     * DESCRIPTION: This method handles survey submissions by parsing the provided JSON, validating the user ID, and saving the *
     * responses into the database. It checks the validity of the user ID and saves the responses for each question. It also *
     * saves user preferences. *
     * PARAMETERS: String json - The JSON string containing the survey responses. *
     *            Integer userId - The ID of the user submitting the survey. *
     * RETURN VALUE: None *
     * EXCEPTIONS: SQLException if an error occurs during database interaction. *
     **********************************************************/
    public void handleSurveySubmission(String json, Integer userId) throws SQLException {
        if (!isValidUserId(userId)) {
            System.out.println("Invalid user_id: " + userId);
            return;
        }

        SurveyDAO dao = new SurveyDAO();

        int submissionId = dao.getNextSubmissionId(userId);
        dao.insertSubmission(userId, submissionId);
        System.out.println("Submission ID: " +submissionId);

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

    /**********************************************************
     * METHOD: isValidUserId *
     * DESCRIPTION: This method checks if the provided user ID exists in the database. It queries the "users" table to see if *
     * the user ID is valid. *
     * PARAMETERS: int userId - The ID of the user to be checked. *
     * RETURN VALUE: boolean - Returns true if the user ID exists, false otherwise. *
     * EXCEPTIONS: SQLException if an error occurs during database interaction. *
     **********************************************************/
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

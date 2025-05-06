/*******************************************************************
 * Book Recommendation System - SurveyDAO Class *
 * *
 * PROGRAMMER: Emily Culp *
 * COURSE: CS360 - Analysis / Algorithms *
 * DATE: May 6, 2025 *
 * REQUIREMENT: Final *
 * *
 * DESCRIPTION: *
 * This file contains the SurveyDAO class, which manages database operations *
 * related to survey responses, including saving user preferences, retrieving *
 * submission IDs, and mapping answer options to database values. *
 * *
 * COPYRIGHT: This code is copyright (C) 2025 Emily Culp *
 * *
 * CREDITS: *
 * ChatGPT by OpenAI was used for generating documentation. *
 * *
 *******************************************************************/

package booksystem;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyDAO {
    private final Connection conn;

    /**********************************************************
     * METHOD: SurveyDAO (Constructor) *
     * DESCRIPTION: Initializes the SurveyDAO object. Typically used to setup any DAO-specific state. *
     * PARAMETERS: None *
     * RETURN VALUE: None *
     **********************************************************/
    public SurveyDAO(){
        try {
            this.conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**********************************************************
     * METHOD: saveSurveyResponses *
     * DESCRIPTION: Saves the survey response to the database for a specific question and user. *
     * PARAMETERS: SurveyResponse response - the response object containing selected answers *
     *             int questionId - ID of the question being answered *
     *             int userId - ID of the user submitting the response *
     *             int submissionId - unique ID for this survey submission *
     * RETURN VALUE: void *
     **********************************************************/
    public void saveSurveyResponses(SurveyResponse response, int questionId, int userId, int submissionId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get all valid options for this question from DB
            Map<String, Integer> validOptions = getAnswerOptionsMap(conn, questionId);

            for (String answer : response.getAnswer()) {
                Integer answerOptionId = validOptions.get(answer.trim());  // Directly use the option_id

                if (answerOptionId == null) {
                    System.out.println("Answer option not found in surveyansweroptions table for answer: " + answer);
                    continue;  // Skip this answer if it doesn't exist
                }

                // Insert the answer
                String insertQuery = "INSERT INTO usersurveyanswers (user_id, question_id, answer_option_id, submission_id) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, questionId);
                    insertStmt.setInt(3, answerOptionId);
                    insertStmt.setInt(4, submissionId);
                    insertStmt.executeUpdate();
                    System.out.println("Successfully inserted survey response: " + answer);
                }
            }
        }
    }

    /**********************************************************
     * METHOD: getAnswerOptionsMap *
     * DESCRIPTION: Retrieves a mapping of answer text to answer option IDs for a given question. *
     * PARAMETERS: Connection conn - the database connection *
     *             int questionId - the ID of the question to retrieve options for *
     * RETURN VALUE: Map<String, Integer> - a mapping of answer text to corresponding option IDs *
     **********************************************************/
    private Map<String, Integer> getAnswerOptionsMap(Connection conn, int questionId) throws SQLException {
        Map<String, Integer> optionsMap = new HashMap<>();

        String query = "SELECT option_id FROM surveyansweroptions WHERE question_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Get the option_id directly
                    int optionId = rs.getInt("option_id");
                    optionsMap.put(String.valueOf(optionId), optionId); // Use option_id as the key
                }
            }
        }
        return optionsMap;
    }

    /**********************************************************
     * METHOD: getNextSubmissionId *
     * DESCRIPTION: Generates and returns the next available submission ID for a user. *
     * PARAMETERS: int userId - the ID of the user for whom the ID is being generated *
     * RETURN VALUE: int - the next available submission ID *
     **********************************************************/
    public int getNextSubmissionId(int userId) {
        String sql = "SELECT COALESCE(MAX(submission_id), 0) + 1 FROM usersurveyanswers WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Start at 1 if nothing exists
    }

    /**********************************************************
     * METHOD: saveUserPreferences *
     * DESCRIPTION: Saves user preferences associated with a specific survey submission. *
     * PARAMETERS: int userId - the user's ID *
     *             int submissionId - the submission ID linked to the user's survey *
     * RETURN VALUE: void *
     **********************************************************/
    public void saveUserPreferences(int userId, int submissionId) throws SQLException {
        String insertQuery =
                "INSERT INTO userpreferences (user_id, submission_id, genre, tone, pace, protagonist, " +
                        "ending, action_dev, romance, twist, supernatural, setting, length, style, theme) " +
                        "SELECT ?, ?, " +
                        "MAX(CASE WHEN usersurveyanswers.question_id = 1 THEN surveyansweroptions.option_text ELSE NULL END) AS genre, " +
                        "GROUP_CONCAT(CASE WHEN usersurveyanswers.question_id = 2 THEN surveyansweroptions.option_text ELSE NULL END SEPARATOR ' / ') AS tone, " +
                        "MAX(CASE WHEN usersurveyanswers.question_id = 3 THEN surveyansweroptions.option_text ELSE NULL END) AS pace, " +
                        "MAX(CASE WHEN usersurveyanswers.question_id = 4 THEN surveyansweroptions.option_text ELSE NULL END) AS protagonist, " +
                        "MAX(CASE WHEN usersurveyanswers.question_id = 5 THEN surveyansweroptions.option_text ELSE NULL END) AS ending, " +
                        "MAX(CASE WHEN usersurveyanswers.question_id = 6 THEN surveyansweroptions.option_text ELSE NULL END) AS action_dev, " +
                        "MAX(CASE WHEN usersurveyanswers.question_id = 7 THEN surveyansweroptions.option_text ELSE NULL END) AS romance, " +
                        "MAX(CASE WHEN usersurveyanswers.question_id = 8 THEN surveyansweroptions.option_text ELSE NULL END) AS twist, " +
                        "MAX(CASE WHEN usersurveyanswers.question_id = 9 THEN surveyansweroptions.option_text ELSE NULL END) AS supernatural, " +
                        "GROUP_CONCAT(CASE WHEN usersurveyanswers.question_id = 10 THEN surveyansweroptions.option_text ELSE NULL END SEPARATOR ' / ') AS setting, " +
                        "MAX(CASE WHEN usersurveyanswers.question_id = 11 THEN surveyansweroptions.option_text ELSE NULL END) AS length, " +
                        "GROUP_CONCAT(CASE WHEN usersurveyanswers.question_id = 12 THEN surveyansweroptions.option_text ELSE NULL END SEPARATOR ' / ') AS style, " +
                        "GROUP_CONCAT(CASE WHEN usersurveyanswers.question_id = 13 THEN surveyansweroptions.option_text ELSE NULL END SEPARATOR ' / ') AS theme " +
                        "FROM usersurveyanswers " +
                        "JOIN surveyansweroptions ON usersurveyanswers.answer_option_id = surveyansweroptions.option_id " +
                        "WHERE usersurveyanswers.user_id = ? AND usersurveyanswers.submission_id = ? " +
                        "GROUP BY usersurveyanswers.user_id, usersurveyanswers.submission_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, submissionId);
            stmt.setInt(3, userId);
            stmt.setInt(4, submissionId);
            stmt.executeUpdate();
        }
    }

    /**********************************************************
     * METHOD: insertSubmission *
     * DESCRIPTION: Inserts a new record into the database for a user survey submission. *
     * PARAMETERS: int userId - the ID of the user submitting the survey *
     *             int submissionId - unique ID for the survey submission *
     * RETURN VALUE: void *
     **********************************************************/
    public void insertSubmission(int userId, int submissionId) throws SQLException {
        String query = "INSERT INTO submissions (submission_id, user_id, submitted_at) VALUES (?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, submissionId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

}

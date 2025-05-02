package booksystem;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyDAO {
    private final Connection conn;

    public SurveyDAO(){
        try {
            this.conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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

    public Map<Integer, String> getUserAnswersWithText(int userId){
        Map<Integer, String> answers = new HashMap<>();

        String sql = """
            SELECT surveyansweroptions.question_id, surveyansweroptions.option_text
            FROM usersurveyanswers
            JOIN surveyansweroptions ON usersurveyanswers.answer_option_id = surveyansweroptions.option_id
            WHERE usersurveyanswers.user_id = ?
        """;


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                answers.put(rs.getInt("question_id"), rs.getString("option_text"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answers;
    }

    public void insertUserPreferences(int userId, int submissionID, Map<String, String> preferences) {
        String sql = """
        INSERT INTO userpreferences (user_id, submission_id, genre, tone, pace, protagonist, ending,
        `action_dev`, romance, twist, supernatural, setting, length, style, theme)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, submissionID);
            int i = 3;
            for (String key : List.of("genre", "tone", "pace", "protagonist", "ending",
                    "action_dev", "romance", "twist", "supernatural",
                    "setting", "length", "style", "theme")) {
                stmt.setString(i++, preferences.getOrDefault(key, "NA"));
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveUserPreferences(int userId, int submissionId) throws SQLException{
        Map<Integer, String> userAnswers = getUserAnswersWithText(userId);

        Map<String, String> preferences = new HashMap<>();
        preferences.put("Genre", "NA");
        preferences.put("Tone", "NA");
        preferences.put("Pace", "NA");
        preferences.put("Protagonist", "NA");
        preferences.put("Ending", "NA");
        preferences.put("Action_Dev", "NA");
        preferences.put("Romance", "NA");
        preferences.put("Twist", "NA");
        preferences.put("Supernatural", "NA");
        preferences.put("Setting", "NA");
        preferences.put("Length", "NA");
        preferences.put("Style", "NA");
        preferences.put("Theme", "NA");

        for(Map.Entry<Integer, String> entry : userAnswers.entrySet()){
            String category = getCategoryByQuestionId(entry.getKey());
            if(!category.equals("Unknown")){
                preferences.put(category, entry.getValue());
            }
        }
        insertUserPreferences(userId, submissionId, preferences);
    }

    private String getCategoryByQuestionId(int questionId){
        switch (questionId) {
            case 1: return "Genre";
            case 2: return "Tone";
            case 3: return "Pace";
            case 4: return "Protagonist";
            case 5: return "Ending";
            case 6: return "Action_Dev";
            case 7: return "Romance";
            case 8: return "Twist";
            case 9: return "Supernatural";
            case 10: return "Setting";
            case 11: return "Length";
            case 12: return "Style";
            case 13: return "Theme";
            default: return "Unknown";
        }
    }

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

}

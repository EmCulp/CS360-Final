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

    public void saveSurveyResponses(SurveyResponse response, int questionId, int userId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get all valid options for this question from DB
            Map<String, Integer> validOptions = getAnswerOptionsMap(conn, questionId);

            for (String answer : response.getAnswer()) {
                Integer answerOptionId = validOptions.get(answer.trim());  // Directly use the option_id

                if (answerOptionId == null) {
                    System.out.println("Answer option not found in surveyansweroptions table for answer: " + answer);
                    continue;  // Skip this answer if it doesn't exist
                }

                // Check if this exact response already exists
                String checkQuery = "SELECT COUNT(*) FROM usersurveyanswers WHERE user_id = ? AND question_id = ? AND answer_option_id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, userId);
                    checkStmt.setInt(2, questionId);
                    checkStmt.setInt(3, answerOptionId);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            System.out.println("User has already answered this option for this question: " + answer);
                            continue;  // Skip the duplicate answer
                        }
                    }
                }

                // Insert the answer
                String insertQuery = "INSERT IGNORE INTO usersurveyanswers (user_id, question_id, answer_option_id) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, questionId);
                    insertStmt.setInt(3, answerOptionId);
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


    public int getAnswerOptionId(Connection conn, String answer, int questionId) throws SQLException {
        String query = "SELECT option_id FROM surveyansweroptions WHERE option_text = ? AND question_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, answer);
            stmt.setInt(2, questionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("option_id");
                }
            }
        }
        return -1; // Not found
    }

}

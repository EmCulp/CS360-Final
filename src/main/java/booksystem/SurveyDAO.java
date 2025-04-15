package booksystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SurveyDAO {
    private final Connection conn;

    public SurveyDAO(){
        try {
            this.conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveSurveyResponses(List<SurveyResponses> responses){
        String insertsql = "INSERT INTO SurveyResponses (user_id, question_id, answer_option_id) VALUES (?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(insertsql)){
            for(SurveyResponses response : responses){
                ps.setInt(1, response.getUserID());
                ps.setInt(2, response.getQuestionID());
                ps.setInt(3, response.getAnswerOptionID());
                ps.executeUpdate();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}

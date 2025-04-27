package booksystem;

import java.sql.*;

public class UserDAO {
    public static void register(String name, String email, String password) throws Exception{
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, name);
        stmt.setString(2, email);
        stmt.setString(3, password);
        stmt.executeUpdate();
    }

    public static User authenticate(Connection conn, String email, String password) throws SQLException{
        String sql = "SELECT * FROM users WHERE email = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if(rs.next()){
            String storedPass = rs.getString("password");
            if(storedPass.equals(password)){
                return new User(rs.getInt("user_id"), rs.getString("name"), email, storedPass);
            }
        }
        return null;
    }
}

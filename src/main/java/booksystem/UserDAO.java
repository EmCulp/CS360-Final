package booksystem;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {
    public static void register(String name, String email, String password, String username) throws Exception{
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO users (name, email, password, username) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        stmt.setString(1, name);
        stmt.setString(2, email);
        stmt.setString(3, hashedPassword);
        stmt.setString(4, username);
        stmt.executeUpdate();
    }

    public static User authenticate(Connection conn, String email, String password) throws SQLException{
        String sql = "SELECT * FROM users WHERE email = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if(rs.next()){
            String storedPass = rs.getString("password");
            if(BCrypt.checkpw(password, storedPass)){
                return new User(rs.getInt("user_id"), rs.getString("name"), email, storedPass);
            }
        }
        return null;
    }
}

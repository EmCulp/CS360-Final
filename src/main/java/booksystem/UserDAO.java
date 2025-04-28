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

    public static User authenticate(Connection conn, String username, String password) throws SQLException{
//        if(username == null || username.trim().isEmpty()){
//            System.out.println("Invalid username input");
//            return null;
//        }

        String sql = "SELECT * FROM users WHERE username = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);

        System.out.println("Executing query: " +sql+ " with username: " +username);

        ResultSet rs = stmt.executeQuery();

        if(rs.next()){
            String storedPass = rs.getString("password");
            if(BCrypt.checkpw(password, storedPass)){
                return new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("email"), storedPass, username);
            }else{
                System.out.println("Password mismatch");
            }
        }else{
            System.out.println("No user found with that username");
        }
        return null;
    }

}

/*******************************************************************
 * UserDAO *
 * *
 * PROGRAMMER: [Emily] *
 * COURSE: [CS360 - Analysis / Algorithms] *
 * DATE: [2025-05-06] *
 * REQUIREMENT: Final *
 * *
 * DESCRIPTION: *
 * The UserDAO class provides database access methods for user-related operations, including registering a user and authenticating a user. *
 * The class uses BCrypt for password hashing and verification. It interacts with the "users" table in the database. *
 * The `register` method allows creating a new user with hashed password storage, while the `authenticate` method checks user credentials *
 * during login attempts. *
 * *
 * COPYRIGHT: This code is copyright (C) 2025 Emily *
 * *
 * CREDITS: *
 * Sources used: [List any sources used] *
 * *
 *******************************************************************/

package booksystem;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {
    /**********************************************************
     * METHOD: register *
     * DESCRIPTION: This method registers a new user in the database with a hashed password. The user information is inserted into the users table. *
     * PARAMETERS: *
     * - name (String): The user's full name. *
     * - email (String): The user's email address. *
     * - password (String): The user's plain-text password. *
     * - username (String): The user's chosen username. *
     * RETURN VALUE: None *
     * EXCEPTIONS: Throws an Exception if there is an issue with the database connection or SQL execution. *
     **********************************************************/
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

    /**********************************************************
     * METHOD: authenticate *
     * DESCRIPTION: This method authenticates a user by checking their username and password against the database. *
     * If the username exists and the password matches, it returns a User object. Otherwise, it returns null. *
     * PARAMETERS: *
     * - conn (Connection): The database connection. *
     * - username (String): The user's username to be authenticated. *
     * - password (String): The user's plain-text password to be verified. *
     * RETURN VALUE: User - A User object if authentication is successful, otherwise null. *
     * EXCEPTIONS: Throws SQLException if there is an issue with the database query or connection. *
     **********************************************************/
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

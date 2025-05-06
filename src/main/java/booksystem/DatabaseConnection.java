/*******************************************************************
 * DatabaseConnection *
 * *
 * PROGRAMMER: [Emily] *
 * COURSE: [CS360 - Analysis / Algorithms *
 * DATE: [2025-05-06] *
 * REQUIREMENT: Final *
 * *
 * DESCRIPTION: *
 * The DatabaseConnection class is responsible for managing the connection to the database. It provides a static *
 * method for retrieving a database connection that can be used for executing SQL queries and updates. The method *
 * ensures that the connection is established using the appropriate database URL, username, and password. *
 * *
 * COPYRIGHT: This code is copyright (C) 2025 Emily *
 * *
 * CREDITS: *
 * ChatGPT *
 * *
 *******************************************************************/

package booksystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/CS360Final";
    private static final String USER = "root";
    private static final String PASSWORD = "2024mySQL!";

    /**********************************************************
     * METHOD: getConnection *
     * DESCRIPTION: This method establishes a connection to the database using predefined database credentials. *
     * It loads the necessary JDBC driver, creates a connection to the database using the provided URL, username, *
     * and password, and returns the active database connection. The method throws an SQLException if the connection *
     * cannot be established. *
     * PARAMETERS: None *
     * RETURN VALUE: Connection - The active connection to the database. *
     * EXCEPTION THROWN: SQLException - If the connection cannot be established. *
     **********************************************************/
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


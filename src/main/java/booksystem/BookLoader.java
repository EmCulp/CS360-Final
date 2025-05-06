/*******************************************************************
 * BookLoader *
 * *
 * PROGRAMMER: [Emily] *
 * COURSE: [CS360 - Analysis / Algorithms *
 * DATE: [2025-05-06] *
 * REQUIREMENT: Final *
 * *
 * DESCRIPTION: *
 * The BookLoader class is responsible for loading book data from a CSV file and inserting it into a database. *
 * The data is expected to have specific fields, such as title, author, genre, and more. The method read the CSV file, *
 * parses the data, and inserts each book record into the database. *
 * *
 * COPYRIGHT: This code is copyright (C) 2025 Emily *
 * *
 * CREDITS: *
 * ChatGPT
 * *
 *******************************************************************/

package booksystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class BookLoader {
    /**********************************************************
     * METHOD: loadBooks *
     * DESCRIPTION: This method reads a CSV file containing book data and inserts each record into the database. *
     * The CSV file must have a header row, and each subsequent row should contain data corresponding to the book's *
     * title, author, genre, and other attributes. The method parses each row, sets the appropriate fields in a *
     * SQL INSERT statement, and executes the query to store the data in the database. *
     * PARAMETERS: [filePath] - The path to the CSV file containing book data. *
     *            [conn] - The database connection used to execute the SQL query. *
     * RETURN VALUE: None *
     **********************************************************/
    public static void loadBooks(String filePath, Connection conn) {
        String sql = "INSERT INTO Books (title, author, genre, tone, protagonist_type, ending_type, pase_type, romance_level, action_or_development, length, writing_style, supernatural, themes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                ps.setString(1, data[0]); // title
                ps.setString(2, data[1]); // author
                ps.setString(3, data[2]); // genre
                ps.setString(4, data[3]); // tone
                ps.setString(5, data[4]); // protagonist_type
                ps.setString(6, data[5]); // ending_type
                ps.setString(7, data[6]); // pase_type
                ps.setString(8, data[7]); // romance_level
                ps.setString(9, data[8]); // action_or_development
                ps.setString(10, data[9]); // length
                ps.setString(11, data[10]); // writing_style
                ps.setBoolean(12, Boolean.parseBoolean(data[11])); // supernatural
                ps.setString(13, data[12]); // themes

                ps.executeUpdate();
            }
            System.out.println("Books loaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

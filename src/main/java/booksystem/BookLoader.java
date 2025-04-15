package booksystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class BookLoader {
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

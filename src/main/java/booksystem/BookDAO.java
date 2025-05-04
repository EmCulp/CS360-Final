package booksystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<Book> getTopRecommendedBooks(int userId, Connection conn) throws SQLException {
        List<Book> recommendedBooks = new ArrayList<>();

        String query = "SELECT DISTINCT b.*, bc.cover_url, " +
                "((b.genre = u.genre) + (b.tone = u.tone) + (b.pace = u.pace) + " +
                "(b.protagonist = u.protagonist) + (b.ending = u.ending) + " +
                "(b.action_or_development = u.action_dev) + (b.romance = u.romance) + " +
                "(b.twist = u.twist) + (b.supernatural = u.supernatural) + " +
                "(b.setting = u.setting) + (b.length = u.length) + " +
                "(b.style = u.style) + (b.themes = u.theme)) AS match_score " +
                "FROM books b " +
                "JOIN userpreferences u ON u.user_id = ? AND u.submission_id = ( " +
                " SELECT MAX(submission_id) from userpreferences WHERE user_id = ? " +
                ") " +
                "LEFT JOIN book_covers bc ON bc.book_id = b.book_id " +
                "ORDER BY match_score DESC " +
                "LIMIT 10";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);     //main query
            stmt.setInt(2, userId);     //submission_id selection
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setGenre(rs.getString("genre"));
                    book.setTone(rs.getString("tone"));
                    book.setPace(rs.getString("pace"));
                    book.setProtagonist(rs.getString("protagonist"));
                    book.setEnding(rs.getString("ending"));
                    book.setActionDevelopment(rs.getString("action_or_development")); // typo in DB?
                    book.setRomanceLevel(rs.getString("romance"));
                    book.setTwists(rs.getString("twist"));
                    book.setSupernatural(rs.getString("supernatural"));
                    book.setSetting(rs.getString("setting"));
                    book.setLength(rs.getString("length"));
                    book.setWritingStyle(rs.getString("style"));
                    book.setThemes(rs.getString("themes"));
                    book.setUrl(rs.getString("cover_url"));
                    // Optional: add match_score to Book class if you want to display it
                    recommendedBooks.add(book);
                }
            }
        }

        return recommendedBooks;
    }

}

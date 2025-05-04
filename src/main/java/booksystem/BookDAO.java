package booksystem;

import java.lang.reflect.Type;
import java.sql.*;
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

    public List<Book> getBooksReadByUser(int userId){
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM booksread WHERE user_id = ? ";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            while(resultSet.next()){
                int bookId = resultSet.getInt("book_id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                double rating = resultSet.getDouble("rating");
                double spice = resultSet.getDouble("spice");
                String coverURL = resultSet.getString("cover_url");

                Book book = new Book(bookId, title, author, rating, spice, coverURL);
                books.add(book);
            }
        }catch (SQLException e){
            System.err.println("Error while retrieving books: " +e.getMessage());
        }
        return books;
    }

    public void addBookRead(Book book, int userId){
        String sql = "INSERT INTO booksread (user_id, book_id, title, author, rating, spice, cover_url) VALUES (?, ?, ?, ?, ?, ?, ?) ";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, book.getBookId());
            statement.setString(3, book.getTitle());

            if(book.getAuthor() == null || book.getAuthor().isEmpty()){
                statement.setNull(4, Types.VARCHAR);
            }else{
                statement.setString(4, book.getAuthor());
            }

            if(book.getRating() == 0.0){
                statement.setNull(5, Types.DOUBLE);
            }else{
                statement.setDouble(5, book.getRating());
            }

            if(book.getSpice() == 0.0){
                statement.setNull(6, Types.DOUBLE);
            }else{
                statement.setDouble(6, book.getSpice());
            }

            if (book.getUrl() == null || book.getUrl().isEmpty()) {
                statement.setNull(7, Types.VARCHAR);
            } else {
                statement.setString(7, book.getUrl());
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error while adding a book: " + e.getMessage());
        }
    }

    public void removeBookRead(String title, String author, int userId) {
        String sql;

        // If author is provided, include it in the WHERE clause
        if (author != null && !author.isEmpty()) {
            sql = "DELETE FROM booksread WHERE user_id = ? AND title = ? AND author = ?";
        } else {
            // If author is not provided, only use title for deletion
            sql = "DELETE FROM booksread WHERE user_id = ? AND title = ?";
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, title);

            // If author is provided, bind the author parameter
            if (author != null && !author.isEmpty()) {
                statement.setString(3, author);
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error while removing book: " + e.getMessage());
        }
    }

}

package booksystem;

import com.mysql.cj.exceptions.StreamingNotifiable;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<Book> getTopRecommendedBooks(int userId, Connection conn) throws SQLException {
        List<Book> recommendedBooks = new ArrayList<>();

        String query = "SELECT DISTINCT b.*, bc.cover_url, " +
                "(" +
                "  IF(b.genre = u.genre, 1, 0) + " +
                "  IF(b.tone = u.tone, 1, 0) + " +
                "  IF(b.pace = u.pace, 1, 0) + " +
                "  IF(b.protagonist = u.protagonist, 1, 0) + " +
                "  IF(b.ending = u.ending, 1, 0) + " +
                "  IF(b.action_or_development = u.action_dev, 1, 0) + " +
                "  IF(b.romance = u.romance, 1, 0) + " +
                "  IF(b.twist = u.twist, 1, 0) + " +
                "  IF(b.supernatural = u.supernatural, 1, 0) + " +
                "  IF(b.setting = u.setting, 1, 0) + " +
                "  IF(b.length = u.length, 1, 0) + " +
                "  IF(b.style = u.style, 1, 0) + " +
                "  IF(b.themes = u.theme, 1, 0) " +
                ") AS match_score " +
                "FROM books b " +
                "JOIN userpreferences u ON u.user_id = ? AND u.submission_id = ( " +
                "  SELECT MAX(submission_id) FROM userpreferences WHERE user_id = ? " +
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
        String sql = """
            SELECT br.book_id, b.title, b.author, br.rating, br.spice, bc.cover_url
            FROM books_read br
            JOIN books b ON br.book_id = b.book_id
            LEFT JOIN book_covers bc ON bc.book_id = br.book_id
            WHERE br.user_id = ?
            """;

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
                String coverURl = resultSet.getString("cover_url");

                Book book = new Book(bookId, title, author, rating, spice, coverURl);
                System.out.println("Cover URL " + book.getUrl());
                books.add(book);
            }
            System.out.println("Books on Books Read page");
        }catch (SQLException e){
            System.err.println("Error while retrieving books: " +e.getMessage());
        }
        return books;
    }

    public void addBookRead(Book book, int userId){
        String sql = "INSERT INTO books_read (user_id, book_id, rating, spice) VALUES (?, ?, ?, ?) ";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            if(book.getBookId() == -1){
                System.out.println("Book ID is null");
            }
            statement.setInt(2, book.getBookId());

            if(book.getRating() == 0.0){
                statement.setNull(3, Types.DOUBLE);
            }else{
                statement.setDouble(3, book.getRating());
            }

            if(book.getSpice() == 0.0){
                statement.setNull(4, Types.DOUBLE);
            }else{
                statement.setDouble(4, book.getSpice());
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error while adding a book: " + e.getMessage());
        }
    }

    public void removeBookRead(int bookId, int userId) {
        String sql = "DELETE FROM books_read WHERE user_id = ? AND book_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, bookId);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error while removing book: " + e.getMessage());
        }
    }

    public int getBookIdByTitleAndAuthor(String title, String author) {
        String sql = "SELECT book_id FROM books WHERE title = ? AND author = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, author);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("book_id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting book_id: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // or throw an exception if you prefer
    }

    public String getCoverUrlByBookId(int bookId) {
        String sql = "SELECT cover_url FROM book_covers WHERE book_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getString("cover_url");
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving cover URL: " + e.getMessage());
        }

        return null;  // return null if no cover URL is found
    }

    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, genre, tone, pace, protagonist, ending, action_or_development, romance, twist, supernatural, setting, length, style, themes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getGenre());
            stmt.setString(4, book.getTone());
            stmt.setString(5, book.getPace());
            stmt.setString(6, book.getProtagonist());
            stmt.setString(7, book.getEnding());
            stmt.setString(8, book.getActionDevelopment());
            stmt.setString(9, book.getRomanceLevel());
            stmt.setString(10, book.getTwists());
            stmt.setString(11, book.getSupernatural());
            stmt.setString(12, book.getSetting());
            stmt.setString(13, book.getLength());
            stmt.setString(14, book.getWritingStyle());
            stmt.setString(15, book.getThemes());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

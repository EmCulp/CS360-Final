/*******************************************************************
 * Book Recommendation System *
 * *
 * PROGRAMMER: Emily Culp *
 * COURSE: CS360 - Analysis/Algorithms *
 * DATE: May 6, 2025 *
 * REQUIREMENT: Final *
 * *
 * DESCRIPTION: *
 * This class provides functionality for retrieving book
 * cover images from the Open Library API and storing cover
 * URLs in a local DB. It also includes helper methods
 * for validating authors and checking existing cover entries.
 *
 * COPYRIGHT: This code is copyright (C) 2025 Emily Culp *
 * *
 * CREDITS: *
 * ChatGPT by OpenAI was used for generating documentation. *
 * *
 *******************************************************************/


package booksystem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BookCoverFetcher {
    /**********************************************************
     * METHOD: fetchCoverUrl *
     * DESCRIPTION: Fetches a cover image URL for the given book title *
     *              and author using the OpenLibrary API. *
     * PARAMETERS: bookTitle - title of the book (String) *
     *             author - author of the book (String) *
     * RETURN VALUE: The URL of the book cover image (String), or null if not found. *
     **********************************************************/
    public String fetchCoverUrl(String bookTitle, String author){
        try{
            String apiURL = "https://openlibrary.org/search.json?title=" +
                    bookTitle.replace(" ", "+");
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray docs = json.getJSONArray("docs");

            if(docs.length() > 0 && docs.getJSONObject(0).has("cover_i")){
                // Use cover_i instead of cover_id
                int coverId = docs.getJSONObject(0).getInt("cover_i");
                return "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";  // Correct URL for the cover image
            }else{
                return null;  // If no cover is found
            }
        }catch (Exception e){
            System.err.println("Error fetching cover URL: " +e.getMessage());
            return null;  // Handle errors gracefully
        }
    }

    /**********************************************************
     * METHOD: saveCover *
     * DESCRIPTION: Saves the cover URL to the database for a specific book. *
     * PARAMETERS: bookId - ID of the book (int) *
     *             coverURL - URL of the cover image (String) *
     * RETURN VALUE: None *
     **********************************************************/
    public void saveCover(int bookId, String coverURL){
        try(Connection conn = DatabaseConnection.getConnection()){
            String sql = "INSERT INTO book_covers (book_id, cover_url) VALUES (?, ?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, bookId);
                stmt.setString(2, coverURL);
                stmt.executeUpdate();
            }
        }catch (SQLException e){
            System.out.println("Error saving cover URL: "+e.getMessage());
        }
    }

    /**********************************************************
     * METHOD: getBookInfoById *
     * DESCRIPTION: Retrieves the book title and author from the database *
     *              using the given book ID. *
     * PARAMETERS: bookId - ID of the book (int) *
     * RETURN VALUE: Map containing "title" and "author" keys with corresponding values *
     **********************************************************/
    // Get the book title from the books table
    public Map<String, String> getBookInfoById(int bookId) {
        String sql = "SELECT title, author FROM books WHERE book_id = ?";
        Map<String, String> info = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                info.put("title", rs.getString("title"));
                info.put("author", rs.getString("author"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving info for ID " + bookId + ": " + e.getMessage());
        }
        return info;
    }

    /**********************************************************
     * METHOD: isInvalidAuthor *
     * DESCRIPTION: Checks whether an author field is invalid, such as *
     *              being null, placeholder, or a genre instead of a name. *
     * PARAMETERS: author - author name (String) *
     * RETURN VALUE: true if invalid, false otherwise *
     **********************************************************/
    private boolean isInvalidAuthor(String author){
        if(author == null || author.trim().equalsIgnoreCase("?????")){
            return true;
        }

        String[] genres = {
                "Biography / Autobiography", "Comic", "Drama", "Essay", "Fable", "Fairy Tale",
                "Fantasy", "Fiction", "Narrative Nonfiction", "Poetry", "Science Fiction",
                "Speech", "Fiction in Verse", "Folklore", "Historical Fiction", "Horror",
                "Legend", "Magna", "Mystery", "Mythology", "Nonfiction", "Realistic Fiction",
                "Short Story", "Tall Tale"
        };

        String lowerAuthor = author.trim().toLowerCase();
        for(String genre : genres){
            if(lowerAuthor.equals(genre)){
                return true;
            }
        }
        return false;
    }

    /**********************************************************
     * METHOD: coverExists *
     * DESCRIPTION: Checks whether a cover already exists for a given book ID *
     *              in the book_covers table. *
     * PARAMETERS: bookId - ID of the book (int) *
     * RETURN VALUE: true if a cover exists, false otherwise *
     **********************************************************/
    private boolean coverExists(int bookId) {
        String sql = "SELECT 1 FROM book_covers WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // true if a row exists
        } catch (SQLException e) {
            System.err.println("Error checking if cover exists for book ID " + bookId + ": " + e.getMessage());
            return false;
        }
    }

    /**********************************************************
     * METHOD: main *
     * DESCRIPTION: Main method for fetching and saving book covers for *
     *              all books in a given range of book IDs. *
     * PARAMETERS: args - command line arguments *
     * RETURN VALUE: None *
     **********************************************************/
    public static void main(String[] args) {
        BookCoverFetcher fetcher = new BookCoverFetcher();

        for (int bookId = 125; bookId <= 1124; bookId++) {
            if(fetcher.coverExists(bookId)){
                continue;
            }

            Map<String, String> bookInfo = fetcher.getBookInfoById(bookId);
            if (bookInfo == null) {
                System.out.println("No book info found for book ID: " + bookId);
                continue;
            }

            String title = bookInfo.get("title");
            String author = bookInfo.get("author");

            if (title == null || author == null || title.isEmpty() || author.isEmpty()) {
                System.out.println("Incomplete data for book ID: " + bookId);
                continue;
            }

            if(fetcher.isInvalidAuthor(author)){
                System.out.println("Invalid author \"" + author + "\" for book ID: " + bookId);
                fetcher.saveCover(bookId, null);
                continue;
            }

            String coverUrl = fetcher.fetchCoverUrl(title, author);
            if (coverUrl != null && !coverUrl.isEmpty()) {
                fetcher.saveCover(bookId, coverUrl);
            } else {
                System.out.println("No cover found for \"" + title + "\" by " + author);
            }
        }
    }

}


package booksystem;

import static spark.Spark.*;

import org.json.JSONObject;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private final DatabaseConnection databaseConnection;

    // Inject the DatabaseConnection bean via constructor

    public Main() {
        this.databaseConnection = new DatabaseConnection();
    }

    public static void main(String[] args) throws SQLException {

        port(4567);
        System.out.println("Port opened");

        staticFileLocation("/public");

        Connection conn = DatabaseConnection.getConnection();
        System.out.println("Connection is good");

        get("/login", (req, res) -> {
            System.out.println("Login Page from main");
            return new MustacheTemplateEngine().render(new ModelAndView(null, "login.html"));
        });

        post("/login", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            System.out.println("Received username: " +username);

            User user = UserDAO.authenticate(conn, username, password);

            if(user != null){
                req.session(true).attribute("user_id", user.getUserId());
                res.redirect("/main");
            }else{
                res.redirect("/login?error=invalid");
            }
            return null;
        });

        post("/signup", (req, res) -> {
            String name = req.queryParams("name");
            String email = req.queryParams("email");
            String password = req.queryParams("password");
            String username = req.queryParams("username");

            try {
                UserDAO.register(name, email, password, username);
                res.redirect("/login");
            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/signup?error=registration");
            }
            return null;
        });


        get("/main", (req, res) -> {
            if (req.session().attribute("user_id") == null) {
                res.redirect("/login");
                return null;
            }

            Integer userId = req.session().attribute("user_id");

            return new MustacheTemplateEngine().render(new ModelAndView(null, "Main.html"));
        });

        get("/results", (req, res) -> {
            try {
                Integer userId = req.session().attribute("user_id");
                if (userId == null) {
                    res.redirect("/login");
                    return null;
                }

                Map<String, Object> model = new HashMap<>();

                BookDAO bookDAO = new BookDAO();
                List<Book> recommendations = bookDAO.getTopRecommendedBooks(userId, conn);
                model.put("recommendations", recommendations); // Ensure this matches your template

                return new MustacheTemplateEngine().render(new ModelAndView(model, "results.html"));
            } catch (Exception e) {
                e.printStackTrace(); // Log the real cause of the error
                res.status(500);
                return "Internal Server Error: " + e.getMessage();
            }
        });


        get("/loadBooks", (req, res)->{
           BookLoader.loadBooks("src/main/resources/books.csv", conn);
           return "Books Loaded!";
        });

        post("/submitSurvey", (req, res)->{
            Integer userId = req.session().attribute("user_id");

            if(userId==null){
                res.redirect("/login");
                return null;
            }

            String json = req.body();

            res.status(202);
            res.body("Survey submission is being processed");

            try{
                //Parse JSON and store to DB
                SurveyController controller = new SurveyController();
                controller.handleSurveySubmission(json, userId);
            }catch (Exception e){
                e.printStackTrace();
            }

            return res.body();
        });

        get("/BooksRead", (req, res) -> {
           int userId = req.session().attribute("user_id");

           BookDAO bookDAO = new BookDAO();

           List<Book> booksRead = bookDAO.getBooksReadByUser(userId);

           for(Book book : booksRead){
               String coverURL = bookDAO.getCoverUrlByBookId(book.getBookId());
               book.setUrl(coverURL);
           }

           Map<String, Object> model = new HashMap<>();
           model.put("booksRead", booksRead);

            return new MustacheTemplateEngine().render(new ModelAndView(model, "BooksRead.html"));
        });

        post("/BooksRead", (req, res) -> {
            int userId = req.session().attribute("user_id");

            String action = req.queryParams("action"); // "add" or "remove"
            String title = req.queryParams("title");
            String author = req.queryParams("author");
            String ratingStr = req.queryParams("rating");
            String spiceStr = req.queryParams("spice");

            BookDAO bookDAO = new BookDAO();
            int bookId = bookDAO.getBookIdByTitleAndAuthor(title, author);

            System.out.println("Book ID: " +bookId);

            if (action.equals("addBook")) {
                // If book isn't in database, fetch and insert it
                if (bookId == -1) {
                    JSONObject openLibBook = BookFetcher.searchBookByTitleAndAuthor(title, author);
                    if (openLibBook != null) {
                        try {
                            // Insert the book into your books table
                            BookFetcher.insertBookIntoDatabase(openLibBook);

                            // Create a Book object and insert into DB
                            Book newBook = BookFetcher.parseBookFromJson(openLibBook); // You define this method
                            bookDAO.addBook(newBook);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Try again to get the bookId
                    bookId = bookDAO.getBookIdByTitleAndAuthor(title, author);
                    System.out.println("Book ID Part 2: " +bookId);
                }

                if (bookId != -1) {
                    Book book = new Book();
                    book.setBookId(bookId);
                    book.setRating(ratingStr != null && !ratingStr.isEmpty() ? Double.parseDouble(ratingStr) : 0.0);
                    book.setSpice(spiceStr != null && !spiceStr.isEmpty() ? Double.parseDouble(spiceStr) : 0.0);

                    bookDAO.addBookRead(book, userId);
                }

            } else if (action.equals("removeBook") && bookId != -1) {
                bookDAO.removeBookRead(bookId, userId);
            }

            res.redirect("/BooksRead");
            return null;
        });


    }

}

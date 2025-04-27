package booksystem;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.Connection;
import java.sql.SQLException;

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

        LoginController.setupRoutes(conn);
        System.out.println("Routes set up!");

        get("/login", (req, res) -> {
            System.out.println("Login Page from main");
            return new MustacheTemplateEngine().render(new ModelAndView(null, "login.html"));
        });

        post("/login", (req, res) -> {
            String email = req.queryParams("email");
            String password = req.queryParams("password");

            User user = UserDAO.authenticate(conn, email, password);

            if(user != null){
                req.session(true).attribute("user_id", user.getUserId());
                res.redirect("/main");
            }else{
                res.redirect("/login?error=invalid");
            }
            return null;
        });

        get("/main", (req, res) -> {
            if (req.session().attribute("user_id") == null) {
                res.redirect("/login");
                return null;
            }

            return new MustacheTemplateEngine().render(new ModelAndView(null, "Main.html"));
        });

        get("/results", (req, res) -> {
            if (req.session().attribute("user_id") == null) {
                res.redirect("/login");
                return null;
            }

            return new MustacheTemplateEngine().render(new ModelAndView(null, "results.html"));
        });

        get("/loadBooks", (req, res)->{
           BookLoader.loadBooks("src/main/resources/books.csv", conn);
           return "Books Loaded!";
        });

        post("/submitSurvey", (req, res)->{
           if(req.session().attribute("user_id")==null){
               res.redirect("/login");
               return null;
           }

           String json = req.body();

           //Parse JSON and store to DB
            SurveyController controller = new SurveyController();
            controller.handleSurveySubmission(json);

            res.status(200);
            return "Survey submitted successfully!";
        });

    }

}

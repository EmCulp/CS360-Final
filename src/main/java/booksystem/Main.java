package booksystem;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.Connection;

public class Main {

    private final DatabaseConnection databaseConnection;

    // Inject the DatabaseConnection bean via constructor

    public Main() {
        this.databaseConnection = new DatabaseConnection();
    }

    public static void main(String[] args){
        port(4567);

        staticFileLocation("/public");

        get("/", (req, res) -> {
            return new MustacheTemplateEngine().render(new ModelAndView(null, "Main.html"));
        });

        get("/results", (req, res) -> {
            return new MustacheTemplateEngine().render(new ModelAndView(null, "results.html"));
        });

        get("/loadBooks", (req, res)->{
           Connection conn = new DatabaseConnection().getConnection();
           BookLoader.loadBooks("src/main/resources/books.csv", conn);
           return "Books Loaded!";
        });

        post("/submitSurvey", (req, res)->{
           String json = req.body();

           //Parse JSON and store to DB
            SurveyController controller = new SurveyController();
            controller.handleSurveySubmission(json);

            res.status(200);
            return "Survey submitted successfully!";
        });

    }

}

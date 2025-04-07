package booksystem;

import static spark.Spark.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final DatabaseConnection databaseConnection;

    // Inject the DatabaseConnection bean via constructor

    public Main() {
        this.databaseConnection = new DatabaseConnection();
    }

    public static void main(String[] args){
        port(4567);

        get("/", (req, res)->{
           return "<h1>Welcome to BookSurvey!</h1><form method='post' action='/submit'>" +
                   "<input type='text' name='name' placeholder='Your name'>" +
                   "<input type='submit' value='Submit'>" +
                   "</form>";
        });

        post("/submit", (req, res)->{
            String name = req.queryParams("name");
            return "<h2>Thanks, " + name + "! We'll suggest some books soon.</h2>";
        });
    }

    public void runApplication() {
        Scanner scanner = new Scanner(System.in);

        try {
            Connection connection = databaseConnection.getConnection();

            User user = new User(1, "john_doe");

            System.out.println("Welcome to the Book Recommendation System!");
            List<SurveyResponses> responses = collectSurveyResponses(scanner);

            storeSurveyResponses(connection, user, responses);

            RecommendationService recommendationService = new RecommendationService();
            List<Book> recommendedBooks = recommendationService.recommendBooks(user);

            System.out.println("Recommended Books for you: ");
            for (Book book : recommendedBooks) {
                System.out.println(book.getTitle() + " by " + book.getAuthor());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private List<SurveyResponses> collectSurveyResponses(Scanner scanner) {
        List<SurveyResponses> responses = new ArrayList<>();

        System.out.println("What genre do you prefer?");
        String genreAnswer = scanner.nextLine();
        responses.add(new SurveyResponses(1, 1, genreAnswer));

        return responses;
    }

    public void storeSurveyResponses(Connection connection, User user, List<SurveyResponses> responses) {
        String insertSQL = "INSERT INTO SurveyResponses (user_id, question_id, response) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            // Loop through all responses and insert them into the database
            for (SurveyResponses response : responses) {
                preparedStatement.setInt(1, response.getUserID());
                preparedStatement.setInt(2, response.getQuestionID());
                preparedStatement.setString(3, response.getResponse());

                // Execute the insert query
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

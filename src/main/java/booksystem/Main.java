package booksystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        try{
            Connection connection = DatabaseConnection.getConnection();

            User user = new User(1, "john_doe");

            System.out.println("Welcome to the Book Recommendation System!");
            List<SurveyResponses> responses = collectSurveyResponses(scanner);

            storeSurveyResponses(connection, user, responses);

            RecommendationService recommendationService = new RecommendationService();
            List<Book> recommendedBooks = recommendationService.recommendBooks(user);

            System.out.println("Recommended Books for you: ");
            for(Book book : recommendedBooks){
                System.out.println(book.getTitle() + " by " +book.getAuthor());
            }


        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            scanner.close();
        }
    }

    private static List<SurveyResponses> collectSurveyResponses(Scanner scanner){
        List<SurveyResponses> responses = new ArrayList<>();

        System.out.println("What genre do you prefer?");
        String genreAnswer = scanner.nextLine();
        responses.add(new SurveyResponses(1, 1, genreAnswer));

        return responses;
    }

    public static void storeSurveyResponses(Connection connection, User user, List<SurveyResponses> responses) {
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

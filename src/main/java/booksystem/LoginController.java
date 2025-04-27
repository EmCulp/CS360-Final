package booksystem;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.mustache.MustacheTemplateEngine;


import java.sql.Connection;

import static spark.Spark.*;

public class LoginController {
    public static void setupRoutes(Connection connection){
        System.out.println("Routes was called");
        staticFileLocation("/public");

        get("/login", (req, res) -> {
            System.out.println("Get login page from setupRoutes");
           return renderLoginPage();
        });

        post("/login", (req, res) -> {
            String email = req.queryParams("email");
            String password = req.queryParams("password");
            User user = UserDAO.authenticate(connection, email, password);

            if(user != null){
                req.session(true).attribute("user_id", user.getUserId());
                res.redirect("/Main");
            }else{
                return "Invalid credentials";
            }
            return null;
        });

        get("/signup", (req, res) -> {
            return renderSignupPage();
        });

        post("/signup", (req, res) -> {
            String name = req.queryParams("name");
            String email = req.queryParams("email");
            String password = req.queryParams("password");

            UserDAO.register(name, email, password);
            res.redirect("/login");
            return null;
        });

        get("/logout", (req, res) -> {
            req.session().invalidate();
            res.redirect("/login");
            return null;
        });
    }
    private static String renderLoginPage() {
        return new MustacheTemplateEngine().render(new ModelAndView(null, "login.html"));

    }

    private static String renderSignupPage() {
        return new MustacheTemplateEngine().render(new ModelAndView(null, "signup.html"));
    }
}

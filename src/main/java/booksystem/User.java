/*******************************************************************
 * Book Recommendation System - User Class *
 * *
 * PROGRAMMER: Emily Culp *
 * COURSE: CS360 - Analysis / Algorithms *
 * DATE: May 6, 2025 *
 * REQUIREMENT: Final *
 * *
 * DESCRIPTION: *
 * This file defines the User class, which models user data including *
 * ID, name, email, password, and username. It includes getters and *
 * setters for accessing and modifying user information. *
 * *
 * COPYRIGHT: This code is copyright (C) 2025 Emily Culp *
 * *
 * CREDITS: *
 * ChatGPT by OpenAI was used for generating documentation. *
 * *
 *******************************************************************/


package booksystem;
import java.util.*;


public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String username;

    /**********************************************************
     * METHOD: User (Constructor) *
     * DESCRIPTION: Initializes a User object with attributes. *
     * PARAMETERS: int id, String name, String email, String password, String username *
     * RETURN VALUE: None *
     **********************************************************/
    public User(int id, String name, String email, String password, String username) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    /**********************************************************
     * METHOD: getUserId *
     * DESCRIPTION: Returns the user's ID. *
     * PARAMETERS: None *
     * RETURN VALUE: int - user's ID *
     **********************************************************/
    public int getUserId() {
        return id;
    }

    /**********************************************************
     * METHOD: setId *
     * DESCRIPTION: Sets the user's ID. *
     * PARAMETERS: int id - user's new ID *
     * RETURN VALUE: void *
     **********************************************************/
    public void setId(int id) {
        this.id = id;
    }

    /**********************************************************
     * METHOD: getName *
     * DESCRIPTION: Returns the user's name. *
     * PARAMETERS: None *
     * RETURN VALUE: String - user's name *
     **********************************************************/
    public String getName() {
        return name;
    }

    /**********************************************************
     * METHOD: setName *
     * DESCRIPTION: Sets the user's name. *
     * PARAMETERS: String name - user's new name *
     * RETURN VALUE: void *
     **********************************************************/
    public void setName(String name) {
        this.name = name;
    }

    /**********************************************************
     * METHOD: getEmail *
     * DESCRIPTION: Returns the user's email address. *
     * PARAMETERS: None *
     * RETURN VALUE: String - user's email *
     **********************************************************/
    public String getEmail() {
        return email;
    }

    /**********************************************************
     * METHOD: setEmail *
     * DESCRIPTION: Sets the user's email address. *
     * PARAMETERS: String email - user's new email *
     * RETURN VALUE: void *
     **********************************************************/
    public void setEmail(String email) {
        this.email = email;
    }

    /**********************************************************
     * METHOD: getPassword *
     * DESCRIPTION: Returns the user's password. *
     * PARAMETERS: None *
     * RETURN VALUE: String - user's password *
     **********************************************************/
    public String getPassword() {
        return password;
    }

    /**********************************************************
     * METHOD: setPassword *
     * DESCRIPTION: Sets the user's password. *
     * PARAMETERS: String password - new password *
     * RETURN VALUE: void *
     **********************************************************/
    public void setPassword(String password) {
        this.password = password;
    }

    /**********************************************************
     * METHOD: getUsername *
     * DESCRIPTION: Returns the user's username. *
     * PARAMETERS: None *
     * RETURN VALUE: String - user's username *
     **********************************************************/
    public String getUsername() {
        return username;
    }

    /**********************************************************
     * METHOD: setUsername *
     * DESCRIPTION: Sets the user's username. *
     * PARAMETERS: String username - new username *
     * RETURN VALUE: void *
     **********************************************************/
    public void setUsername(String username) {
        this.username = username;
    }
}
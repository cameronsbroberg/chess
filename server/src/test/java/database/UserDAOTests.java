package database;

import dataaccess.DataAccessException;
import dataaccess.MySqlUserDAO;
import dataaccess.ResponseException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Server;

public class UserDAOTests {
    private static final UserData TEST_USER = new UserData("Suitcase Murphy", "smPassword", "sm@mail.com");
    private static final UserData TEST_USER_2 = new UserData("The Big Alabama", "alPassword", "tba@mail.com");
    private static final UserData TEST_USER_3 = new UserData("Blue Note", "bluenote5", "bn@mail.com");

    private static Server server;

    @Test
    @DisplayName("Start a database")
    public void initializationTest() {
        MySqlUserDAO userDAO = Assertions.assertDoesNotThrow(() -> new MySqlUserDAO());
    }
    @Test
    @DisplayName("Clear the database")
    public void clearTest() {
        MySqlUserDAO userDAO = Assertions.assertDoesNotThrow(() -> new MySqlUserDAO());
        Assertions.assertDoesNotThrow(() -> userDAO.clear());
    }
    @Test
    @DisplayName("Clear a full database")
    public void clearFullTest() {
        MySqlUserDAO userDAO = Assertions.assertDoesNotThrow(() -> new MySqlUserDAO());
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER_2));
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER_3));
        Assertions.assertDoesNotThrow(() -> userDAO.clear());
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(TEST_USER.username()));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(TEST_USER_2.username()));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(TEST_USER_3.username()));
    }
    @Test
    @DisplayName("Create a user successfully")
    public void insertUserTest(){
        MySqlUserDAO userDAO = new MySqlUserDAO();
        userDAO.clear();
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
    }
    @Test
    @DisplayName("Create the same user twice")
    public void insertUserTwice(){
        MySqlUserDAO userDAO = new MySqlUserDAO();
        userDAO.clear();
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
        Assertions.assertThrows(ResponseException.class, () -> userDAO.createUser(TEST_USER));
        //NOTE: In actual running, this should not occur. The UserService class calls getUser to make sure the username is available.
    }

    @Test
    @DisplayName("Get a user after inserting")
    public void getUser() {
        MySqlUserDAO userDAO = new MySqlUserDAO();
        userDAO.clear();
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
        UserData resultUser = Assertions.assertDoesNotThrow(() -> userDAO.getUser(TEST_USER.username()));
        Assertions.assertEquals(resultUser, TEST_USER);
    }
    @Test
    @DisplayName("Get a user that's not there")
    public void getBadUser() {
        MySqlUserDAO userDAO = new MySqlUserDAO();
        userDAO.clear();
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(TEST_USER_2.username()));
    }
}

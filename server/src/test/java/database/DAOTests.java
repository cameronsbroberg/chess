package database;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MySqlUserDAO;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Server;

public class DAOTests {
    private static final UserData TEST_USER = new UserData("SuitcaseMurphy", "smPassword", "sm@mail.com");

    private static Server server;

    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @Test
    @DisplayName("Start a database")
    public void initializationTest() {
        MySqlUserDAO userDAO = Assertions.assertDoesNotThrow(() -> new MySqlUserDAO());
    }
    @Test
    @DisplayName("Delete a database")
    public void destructionTest() {
        MySqlUserDAO userDAO = new MySqlUserDAO();
        userDAO.clear();
    }
    @Test
    @DisplayName("Insert a user successfully")
    public void insertUserTest(){
        MySqlUserDAO userDAO = new MySqlUserDAO();
        userDAO.clear();
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));

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
    public void getBadUser(){
        MySqlUserDAO userDAO = new MySqlUserDAO();
        userDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("fakeUser"));
    }
}

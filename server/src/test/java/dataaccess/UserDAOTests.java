package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import service.BadRequestException;

public class UserDAOTests {
    private static final UserData TEST_USER = new UserData("Suitcase Murphy", "smPassword", "sm@mail.com");
    private static final UserData TEST_USER_2 = new UserData("The Big Alabama", "alPassword", "tba@mail.com");
    private static final UserData TEST_USER_3 = new UserData("Blue Note", "bluenote5", "bn@mail.com");

    private static MySqlUserDAO userDAO;

    @BeforeEach
    public void initialize(){
        userDAO = Assertions.assertDoesNotThrow(() -> new MySqlUserDAO());
        Assertions.assertDoesNotThrow(() -> userDAO.clear());
    }
    @Test
    @DisplayName("Start and clear a database")
    public void clearTest() {
        Assertions.assertNotNull(userDAO);
        Assertions.assertDoesNotThrow(()->userDAO.clear());
    }

    @Test
    @DisplayName("Clear a full database")
    public void clearFullTest() {
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER_2));
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER_3));
        Assertions.assertDoesNotThrow(() -> userDAO.clear());
        Assertions.assertThrows(BadRequestException.class, () -> userDAO.getUser(TEST_USER.username()));
        Assertions.assertThrows(BadRequestException.class, () -> userDAO.getUser(TEST_USER_2.username()));
        Assertions.assertThrows(BadRequestException.class, () -> userDAO.getUser(TEST_USER_3.username()));
    }
    @Test
    @DisplayName("Create a user successfully")
    public void insertUserTest(){
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
    }
    @Test
    @DisplayName("Create the same user twice")
    public void insertUserTwice(){
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
        Assertions.assertThrows(DataAccessException.class,() -> userDAO.createUser(TEST_USER));
        //NOTE: In actual running, this should not occur. The UserService class calls getUser to make sure the username is available.
    }

    @Test
    @DisplayName("Get a user after inserting")
    public void getUser() {
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
        UserData resultUser = Assertions.assertDoesNotThrow(() -> userDAO.getUser(TEST_USER.username()));
        Assertions.assertEquals(TEST_USER,resultUser);
    }
    @Test
    @DisplayName("Get a user that's not there")
    public void getBadUser() {
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
        Assertions.assertThrows(BadRequestException.class, () -> userDAO.getUser(TEST_USER_2.username()));
    }
}

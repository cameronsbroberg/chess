package database;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MySqlAuthDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.BadRequestException;
import service.InvalidTokenException;

public class AuthDAOTests {
    private static final UserData TEST_USER = new UserData("Suitcase Murphy", "smPassword", "sm@mail.com");
    private static final UserData TEST_USER_2 = new UserData("The Big Alabama", "alPassword", "tba@mail.com");
    private static final UserData TEST_USER_3 = new UserData("Blue Note", "bluenote5", "bn@mail.com");

    private static AuthDAO authDAO;

    @BeforeEach
    public void initialize(){
        authDAO = Assertions.assertDoesNotThrow(() -> new MySqlAuthDAO());
        Assertions.assertDoesNotThrow(() -> authDAO.clear());
    }
    @Test
    @DisplayName("Clear successfully")
    public void clearTable(){
        Assertions.assertNotNull(authDAO);
        Assertions.assertDoesNotThrow(()->authDAO.clear());
    }
    @Test
    @DisplayName("Add an authData")
    public void createAuthGood(){
        AuthData authData = Assertions.assertDoesNotThrow(() -> authDAO.createAuth(TEST_USER.username()));
        Assertions.assertEquals(TEST_USER.username(),authData.username());
    }
    @Test
    @DisplayName("Add an authData without valid input")
    public void createAuthBad(){
        Assertions.assertThrows(BadRequestException.class, () -> authDAO.createAuth(null));
    }
    @Test
    @DisplayName("Get authData from authToken")
    public void getAuthGood(){
        AuthData authData = Assertions.assertDoesNotThrow(() -> authDAO.createAuth(TEST_USER.username()));
        AuthData resultAuth = Assertions.assertDoesNotThrow(() -> authDAO.getAuth(authData.authToken()));
        Assertions.assertEquals(authData, resultAuth);
    }
    @Test
    @DisplayName("Get authData from bad authToken")
    public void getAuthBad(){
        Assertions.assertThrows(InvalidTokenException.class, () -> authDAO.getAuth("FakeAuthToken"));
    }
    @Test
    @DisplayName("Delete auth that's actually there")
    public void deleteAuthGood(){
        AuthData authData = Assertions.assertDoesNotThrow(() -> authDAO.createAuth(TEST_USER.username()));
        AuthData resultAuth = Assertions.assertDoesNotThrow(() -> authDAO.getAuth(authData.authToken()));
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth(resultAuth.authToken()));
        Assertions.assertThrows(InvalidTokenException.class, () -> authDAO.getAuth(authData.authToken()));
    }
    @Test
    @DisplayName("Delete auth that's not there")
    public void deleteAuthBad(){
        Assertions.assertThrows(InvalidTokenException.class, () -> authDAO.deleteAuth("FakeAuthToken"));
    }
}

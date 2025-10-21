package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;

public class UserServiceTests {
    private MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private UserService userService;
    private UserData user;

    private void initialize(){
        this.memoryUserDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.userService = new UserService(this.memoryUserDAO, this.authDAO);
        this.user = new UserData("Alice","pass123","hello@byu.edu");
    }
    @Test
    @DisplayName("Register a new user successfully")
    public void registerNewUser(){
        initialize();
        UserData firstUser = new UserData("Alice","pass123","hello@byu.edu");
        AuthData firstUserAuth = Assertions.assertDoesNotThrow(() -> userService.register(firstUser));
    }

    @Test
    @DisplayName("Fail to register a second user with the same username")
    public void registerTwoUsers(){
        initialize();
        AuthData firstUserAuth = Assertions.assertDoesNotThrow(() -> userService.register(user));
        UserData secondUser = new UserData("Alice","weakpass4","bonjour@byu.edu");
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(secondUser));
    }

    @Test
    @DisplayName("Successfully login an existing user")
    public void loginGoodUser(){
        initialize();
        Assertions.assertDoesNotThrow(() -> userService.register(user));
        AuthData loginResult = Assertions.assertDoesNotThrow(() ->
                userService.login(new LoginRequest(user.username(), user.password())));
    }

    @Test
    @DisplayName("Unsuccessfully login a bad username")
    public void loginBadUser(){
        initialize();
        Assertions.assertDoesNotThrow(() -> userService.register(user));
        InvalidTokenException e = Assertions.assertThrows(InvalidTokenException.class,
                () -> userService.login(new LoginRequest("Bob","pass123")));
        Assertions.assertEquals("Error: unauthorized",e.getMessage());
    }

    @Test
    @DisplayName("Unsuccessfully login with right username and wrong password")
    public void loginBadPw(){
        initialize();
        Assertions.assertDoesNotThrow(() -> userService.register(user));
        InvalidTokenException e = Assertions.assertThrows(InvalidTokenException.class,
                () -> userService.login(new LoginRequest("Alice","wrongpass123")));
        Assertions.assertEquals("Error: unauthorized",e.getMessage());
    }

    @Test
    @DisplayName("Successfully logout a logged-in user")
    public void logout(){
        initialize();
        Assertions.assertDoesNotThrow(() -> userService.register(user));
        AuthData loginResult = Assertions.assertDoesNotThrow(() ->
                userService.login(new LoginRequest(user.username(), user.password())));
        Assertions.assertDoesNotThrow(() -> userService.logout(loginResult.authToken()));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(loginResult.authToken()));
    }

    @Test
    @DisplayName("Unsuccessfully logout a fake authToken")
    public void logoutBadAuth(){
        initialize();
        Assertions.assertThrows(InvalidTokenException.class, () -> userService.logout("fakeAuthTokenString"));
    }

    @Test
    @DisplayName("Unsuccessfully logout a user who is not logged in")
    public void logoutOfflineUser(){
        initialize();
        Assertions.assertDoesNotThrow(() -> userService.register(user));
        Assertions.assertThrows(InvalidTokenException.class, () -> userService.logout("fakeAuthTokenString"));
    }

}

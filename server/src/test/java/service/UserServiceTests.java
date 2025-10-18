package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;

public class UserServiceTests {

    @Test
    @DisplayName("Register a new user successfully")
    public void registerNewUser(){
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        UserData firstUser = new UserData("Alice","pass123","hello@byu.edu");
        AuthData firstUserAuth = Assertions.assertDoesNotThrow(() -> userService.register(firstUser));
    }

    @Test
    @DisplayName("Fail to register a second user with the same username")
    public void registerTwoUsers(){
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        UserData firstUser = new UserData("Alice","pass123","hello@byu.edu");
        AuthData firstUserAuth = Assertions.assertDoesNotThrow(() -> userService.register(firstUser));
        UserData secondUser = new UserData("Alice","weakpass4","bonjour@byu.edu");
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(secondUser));
    }

    @Test
    @DisplayName("Successfully login an existing user")
    public void login(){
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        UserData firstUser = new UserData("Alice","pass123","hello@byu.edu");
        Assertions.assertDoesNotThrow(() -> userService.register(firstUser));
        AuthData loginResult = Assertions.assertDoesNotThrow(() ->
                userService.login(new LoginRequest(firstUser.username(), firstUser.password())));
    }

    @Test
    @DisplayName("Unsuccessfully login a bad username")
    public void login_bad_user(){
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        UserData firstUser = new UserData("Alice","pass123","hello@byu.edu");
        Assertions.assertDoesNotThrow(() -> userService.register(firstUser));
        InvalidLoginException e = Assertions.assertThrows(InvalidLoginException.class,
                () -> userService.login(new LoginRequest("Bob","pass123")));
        Assertions.assertEquals("Invalid username",e.getMessage());
    }

    @Test
    @DisplayName("Unsuccessfully login with right username and wrong password")
    public void login_bad_pw(){
        UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        UserData firstUser = new UserData("Alice","pass123","hello@byu.edu");
        Assertions.assertDoesNotThrow(() -> userService.register(firstUser));
        InvalidLoginException e = Assertions.assertThrows(InvalidLoginException.class,
                () -> userService.login(new LoginRequest("Alice","wrongpass123")));
        Assertions.assertEquals("Invalid password",e.getMessage());
    }
}

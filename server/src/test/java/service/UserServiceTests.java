package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}

package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import dataaccess.UserDAO;
import requests.LoginRequest;

public class UserService {
    private UserDAO userDAO = null;
    private AuthDAO authDAO = null;
    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData registerRequest) {//I don't think I need a specific RegisterRequest class
        String username = registerRequest.username();
        try {
            userDAO.getUser(username);
            throw new AlreadyTakenException("Error: already taken");
        } catch (DataAccessException e) {
            userDAO.createUser(registerRequest);
            return authDAO.createAuth(registerRequest.username());
        }
    }

    public AuthData login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        try {
            UserData userData = userDAO.getUser(username);
            if (!(loginRequest.password().equals(userData.password()))) {
                throw new InvalidLoginException("Invalid password");
            }
            return authDAO.createAuth(username);
        }
        catch (DataAccessException e) {
            throw new InvalidLoginException("Invalid username");
        }
    }

    public void logout(String authToken) {
        try {
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new InvalidTokenException("Error: unauthorized");
        }
    }
}

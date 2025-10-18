package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import dataaccess.UserDAO;

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
            UserData userData = userDAO.getUser(username);
            throw new AlreadyTakenException("username already taken");
        } catch (DataAccessException e) {
            //This is good, it means that the username isn't taken.
            userDAO.createUser(registerRequest);
            return authDAO.createAuth(registerRequest.username());
        }
    }
}

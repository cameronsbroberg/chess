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

    public AuthData register(UserData registerRequest)
            throws BadRequestException,AlreadyTakenException{
        String username = registerRequest.username();
        if(username == null | registerRequest.password() == null | registerRequest == null){
            throw new BadRequestException("Error: bad request");
        }
        try {
            userDAO.getUser(username);
            throw new AlreadyTakenException("Error: already taken");
        } catch (DataAccessException e) {
            userDAO.createUser(registerRequest);
            return authDAO.createAuth(registerRequest.username());
        }
    }

    public AuthData login(LoginRequest loginRequest) throws InvalidTokenException,BadRequestException {
        String username = loginRequest.username();
        if(username == null | loginRequest.password() == null){
            throw new BadRequestException("Error: bad request");
        }
        try {
            UserData userData = userDAO.getUser(username);
            if (!(loginRequest.password().equals(userData.password()))) {
                throw new InvalidTokenException("Error: unauthorized");
            }
            return authDAO.createAuth(username);
        }
        catch (DataAccessException e) {
            throw new InvalidTokenException("Error: unauthorized");
        }
    }

    public void logout(String authToken) {
        if(authToken == null){
            throw new BadRequestException("Error: bad request");
        }
        try {
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new InvalidTokenException("Error: unauthorized");
        }
    }
}

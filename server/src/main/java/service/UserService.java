package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;

public class UserService {
    private UserDAO userDAO = null;
    private AuthDAO authDAO = null;
    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    private String encryptPassword(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword,BCrypt.gensalt());
    }

    public AuthData register(UserData rawRegisterRequest)
            throws BadRequestException,AlreadyTakenException,DataAccessException{
        String username = rawRegisterRequest.username();
        String email = rawRegisterRequest.email();
        if(username == null | rawRegisterRequest.password() == null | email == null){
            throw new BadRequestException("Error: bad request");
        }
        String encryptedPw = encryptPassword(rawRegisterRequest.password());
        try {
            userDAO.getUser(username);
            throw new AlreadyTakenException("Error: already taken");
        } catch (BadRequestException e) {
            UserData registerRequest = new UserData(username, encryptedPw, email);
            userDAO.createUser(registerRequest);
            return authDAO.createAuth(registerRequest.username());
        }
    }

    public AuthData login(LoginRequest loginRequest)
            throws InvalidTokenException,BadRequestException,DataAccessException {
        String username = loginRequest.username();
        if(username == null | loginRequest.password() == null){
            throw new BadRequestException("Error: bad request");
        }
        UserData userData = null;
        try {
            userData = userDAO.getUser(username);
        } catch (BadRequestException e) {
            throw new InvalidTokenException("Error: unauthorized");
        }
        if (!(BCrypt.checkpw(loginRequest.password(),userData.password()))){
            throw new InvalidTokenException("Error: unauthorized");
        }
        return authDAO.createAuth(username);
    }

    public void logout(String authToken)
            throws DataAccessException,InvalidTokenException{
        if(authToken == null){
            throw new InvalidTokenException("Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}

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
            throws BadRequestException,AlreadyTakenException{
        String username = rawRegisterRequest.username();
        String email = rawRegisterRequest.email();
        if(username == null | rawRegisterRequest.password() == null | email == null){
            throw new BadRequestException("Error: bad request");
        }
        String encryptedPw = encryptPassword(rawRegisterRequest.password());
        try {
            userDAO.getUser(username);
            throw new AlreadyTakenException("Error: already taken");
        } catch (DataAccessException e) {
            UserData registerRequest = new UserData(username, encryptedPw, email);
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
            if (!(BCrypt.checkpw(loginRequest.password(),userData.password()))){
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

package dataaccess;

import model.AuthData;
import service.InvalidTokenException;

import java.util.UUID;

public interface AuthDAO {
    default String generateToken(){
        return UUID.randomUUID().toString();
    };
    AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException, InvalidTokenException;
    void deleteAuth(String authToken) throws DataAccessException, InvalidTokenException;
    void clear() throws DataAccessException;
}

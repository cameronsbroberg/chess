package dataaccess;

import model.AuthData;

import java.util.UUID;

public interface AuthDAO {
    default String generateToken(){
        return UUID.randomUUID().toString();
    };
    AuthData createAuth(String username);
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear();
}

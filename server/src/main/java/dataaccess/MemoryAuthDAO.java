package dataaccess;
import model.AuthData;
import service.InvalidTokenException;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> authorizations = new HashMap<>();

    public AuthData createAuth(String username) {
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        authorizations.put(authToken,authData);
        return authData;
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        if(authorizations.containsKey(authToken)){
            return authorizations.get(authToken);
        }
        else {
            throw new DataAccessException("Authtoken not found");
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException, InvalidTokenException {
        if(authorizations.containsKey(authToken)){
            authorizations.remove(authToken);
        }
        else {
            throw new DataAccessException("Authtoken not found");
        }
    }

    public void clear() {
        authorizations.clear();
    }
}

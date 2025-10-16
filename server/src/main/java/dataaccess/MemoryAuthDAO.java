package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> authorizations = new HashMap<>();
    public void createAuth(AuthData authData) {
        authorizations.put(authData.authToken(),authData);
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        if(authorizations.containsKey(authToken)){
            return authorizations.get(authToken);
        }
        else throw new DataAccessException("Authtoken not found");
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        if(authorizations.containsKey(authToken)){
            authorizations.remove(authToken);
        }
        else throw new DataAccessException("Authtoken not found");
    }

    public void clear() {
        authorizations.clear();
    }
}

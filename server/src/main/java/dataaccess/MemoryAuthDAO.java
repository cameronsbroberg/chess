package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> authorizations = new HashMap<>();
    public void createAuth(AuthData authData) {
        authorizations.put(authData.authToken(),authData);
    }

    public AuthData getAuth(String authToken) {
        return authorizations.get(authToken);
    }

    public void deleteAuth(String authToken) {
        authorizations.remove(authToken);
    }

    public void clear() {
        authorizations.clear();
    }
}

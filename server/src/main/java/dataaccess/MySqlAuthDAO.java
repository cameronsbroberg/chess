package dataaccess;

import model.AuthData;

public class MySqlAuthDAO implements AuthDAO{
    @Override
    public String generateToken() {
        return "";
    }

    @Override
    public AuthData createAuth(String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}

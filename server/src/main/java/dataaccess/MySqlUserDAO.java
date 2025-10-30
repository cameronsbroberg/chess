package dataaccess;

import model.UserData;

public class MySqlUserDAO implements UserDAO{
    public MySqlUserDAO() throws DataAccessException{
        DatabaseManager.createDatabase();
    }

    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }
}

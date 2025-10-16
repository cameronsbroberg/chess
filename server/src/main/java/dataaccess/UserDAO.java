package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData userData); //FIXME: what arguments does this take?
    UserData getUser(String username) throws DataAccessException;
    //void updateUser(UserData userData); //Is there ever a need for this?
    void clear();
}

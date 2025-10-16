package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData userData); //FIXME: what arguments does this take?
    UserData getUser(String username);
    void clear();
}

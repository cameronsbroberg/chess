package dataaccess;

import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private HashMap<String, UserData> users = new HashMap<>();
    public void createUser(UserData userData){
        users.put(userData.username(),userData);
    }

    public UserData getUser(String username) throws DataAccessException{
        if(users.containsKey(username)){
            return users.get(username);
        }
        else throw new DataAccessException("Username not found");
    }

    public void clear() {
        users.clear();
    }
}

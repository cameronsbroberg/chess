package dataaccess;

import chess.ChessGame;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO{
    public MySqlUserDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new ResponseException(e.getMessage()); //TODO: What.p
        }
    }
    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            String statement =             """
            CREATE TABLE IF NOT EXISTS user(
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
            );
            """;
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new ResponseException(String.format("Unable to configure database: %s",e.getMessage()));
        }
    }

    private String encryptPassword(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword,BCrypt.gensalt());
    }
    @Override
    public void createUser(UserData userData) throws ResponseException{
        String encryptedPw = encryptPassword(userData.password());
        try (var conn = DatabaseManager.getConnection()){
            String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?);";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, encryptedPw);
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(e.getMessage());
        } catch (DataAccessException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            String statement = "SELECT password, email FROM user WHERE username = ?";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1,username);
                try(var response = preparedStatement.executeQuery()){
                    UserData userData = null;
                    while(response.next()){
                        String password = response.getString("password");
                        String email = response.getString("email");
                        userData = new UserData(username,password,email);
                    }
                    if(userData == null){
                        throw new DataAccessException("Username not found");
                    }
                    return userData;
                }
            }
        }
        catch(SQLException e){
            throw new ResponseException(e.getMessage());
        }
    }

    @Override
    public void clear() throws ResponseException{
        try(var conn = DatabaseManager.getConnection()){
            String statement = "TRUNCATE TABLE user"; //TODO: Should this be DROP instead?
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ResponseException(String.format("Unable to configure database: %s",e.getMessage()));
        }
    }
}

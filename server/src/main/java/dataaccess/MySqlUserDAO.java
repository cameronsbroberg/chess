package dataaccess;

import chess.ChessGame;
import model.UserData;

import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO{
    public MySqlUserDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new ResponseException(e.getMessage()); //TODO: What.p
        }
    }
//    private final String[] createStatements = {
//            """
//            CREATE TABLE IF NOT EXISTS user(
//            id INT NOT NULL AUTO_INCREMENT,
//            username VARCHAR(255) NOT NULL,
//            password VARCHAR(255) NOT NULL,
//            email VARCHAR(255) NOT NULL,
//            PRIMARY KEY (id)
//            );
//            """,
//            """
//            CREATE TABLE IF NOT EXISTS authData (
//            authToken VARCHAR(255) NOT NULL,
//            username VARCHAR(255) NOT NULL,
//            PRIMARY KEY (authToken)
//            );
//            """,
//
//            """
//            CREATE TABLE IF NOT EXISTS gameData (
//            gameID INT NOT NULL AUTO_INCREMENT,
//            whiteUsername VARCHAR(255),
//            blackUsername VARCHAR(255),
//            gameName VARCHAR(255) NOT NULL,
//            game VARCHAR(255) NOT NULL,
//            PRIMARY KEY (gameID)
//            );
//            """
//    };
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

    @Override
    public void createUser(UserData userData) throws ResponseException{
        try (var conn = DatabaseManager.getConnection()){
            String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?);";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
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

package dataaccess;

import chess.ChessGame;
import model.UserData;

import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO{
    public MySqlUserDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user(
            id INT NOT NULL AUTO_INCREMENT,
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            PRIMARY KEY (id)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS authData (
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken)
            );
            """,

            """
            CREATE TABLE IF NOT EXISTS gameData (
            gameID INT NOT NULL AUTO_INCREMENT,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255) NOT NULL,
            game VARCHAR(255) NOT NULL,
            PRIMARY KEY (gameID)
            );
            """
    };
    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            for(var statement : createStatements){
                try(var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException e) {
            throw new ResponseException(String.format("Unable to configure database: %s",e.getMessage()));
        }
    }
    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            String statement = "SELECT id, username, password, email FROM user WHERE username ='" + username + "'";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch(Exception e){
            throw new DataAccessException("Username not found");
        }
        return null;
    }

    @Override
    public void clear() throws ResponseException{ //FIXME this should probably throw a ResponseException
        String[] deleteStatements = {//TODO: Should this be delete? Or Truncate?
                //TODO: Separate tables by DAO. This should be a single statement instead of a list.
                "TRUNCATE TABLE user;",
                "TRUNCATE TABLE authData;",
                "TRUNCATE TABLE game;"
        };
        try(var conn = DatabaseManager.getConnection()){
            for(var statement : deleteStatements){
                try(var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException | DataAccessException e) {//FIXME you should probably deal with this somewhere else.
            throw new ResponseException(String.format("Unable to configure database: %s",e.getMessage()));
        }
    }
}

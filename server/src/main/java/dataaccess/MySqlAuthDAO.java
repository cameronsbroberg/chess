package dataaccess;

import model.AuthData;
import service.InvalidTokenException;

import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO{
    public MySqlAuthDAO() throws DataAccessException{
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            String statement = """
            CREATE TABLE IF NOT EXISTS authData(
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken)
            );
            """;
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to configure database: %s",e.getMessage()));
        }
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException{
        String authToken = generateToken();
        try (var conn = DatabaseManager.getConnection()){
            String statement = "INSERT INTO authData (authToken, username) VALUES (?, ?);";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));        }
        return new AuthData(authToken,username);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, InvalidTokenException {
        try (var conn = DatabaseManager.getConnection()){
            String statement = "SELECT username FROM authData WHERE authToken = ?";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1,authToken);
                try(var response = preparedStatement.executeQuery()){
                    AuthData authData = null;
                    while(response.next()){
                        String username = response.getString("username");
                        authData = new AuthData(authToken,username);
                    }
                    if(authData == null){
                        throw new InvalidTokenException("Authtoken not found");
                    }
                    return authData;
                }
            }
        }
        catch(SQLException e){
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, InvalidTokenException {
        try (var conn = DatabaseManager.getConnection()){
            String statement = "DELETE FROM authData WHERE authToken = ?";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1,authToken);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected == 0){
                    throw new InvalidTokenException("Error: unauthorized");
                }
            }
        }
        catch(SQLException e){
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));        }
    }

    @Override
    public void clear() throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            String statement = "TRUNCATE TABLE authData";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));        }
    }
}

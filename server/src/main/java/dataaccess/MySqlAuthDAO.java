package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

public class MySqlAuthDAO implements AuthDAO{
    public MySqlAuthDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new ResponseException(e.getMessage());
        }
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
            throw new ResponseException(String.format("Unable to configure database: %s",e.getMessage()));
        }
    }

    @Override
    public AuthData createAuth(String username) {
        String authToken = generateToken();
        try (var conn = DatabaseManager.getConnection()){
            String statement = "INSERT INTO authData (authToken, username) VALUES (?, ?);";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(e.getMessage());
        } catch (DataAccessException e) {
            throw new ResponseException(e.getMessage());
        }
        return new AuthData(authToken,username);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
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
                        throw new DataAccessException("Authtoken not found");
                    }
                    return authData;
                }
            }
        }
        catch(SQLException e){
            throw new ResponseException(e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            String statement = "DELETE FROM authData WHERE authToken = ?";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1,authToken);
                preparedStatement.executeUpdate();
            }
        }
        catch(SQLException e){//FIXME: Distinguish between when authToken is not there and when the database is not there.
            throw new DataAccessException("Authtoken not found");
        }
    }

    @Override
    public void clear() {
        try(var conn = DatabaseManager.getConnection()){
            String statement = "TRUNCATE TABLE authData"; //TODO: Should this be DROP instead?
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new ResponseException(String.format("Unable to configure database: %s",e.getMessage()));
        }
    }
}

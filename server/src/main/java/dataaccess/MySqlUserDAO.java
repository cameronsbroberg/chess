package dataaccess;

import model.UserData;
import service.BadRequestException;

import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO{
    public MySqlUserDAO() throws DataAccessException{
        configureDatabase();
    }
    private void configureDatabase() throws DataAccessException{
        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                String statement = """
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
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Error: unable to configure database: %s",e.getMessage()));
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?);";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, userData.password());
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));
        }
    }

    @Override
    public UserData getUser(String username)
            throws DataAccessException, BadRequestException {
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
                        throw new BadRequestException("Username not found");
                    }
                    return userData;
                }
            }
        }
        catch(SQLException e){
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            String statement = "TRUNCATE TABLE user";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));
        }
    }
}

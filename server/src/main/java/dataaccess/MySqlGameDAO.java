package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import service.BadRequestException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO{
    private Gson serializer;
    public MySqlGameDAO() throws DataAccessException{
        configureDatabase();
        this.serializer = new Gson();

    }
    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            String statement =             """
                CREATE TABLE IF NOT EXISTS gameData (
                gameId INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(30),
                blackUsername VARCHAR(30),
                gameName VARCHAR(30) NOT NULL,
                game VARCHAR(2000) NOT NULL,
                PRIMARY KEY (gameID)
                );
            """;
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));        }
    }
    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        String gameString = serializer.toJson(gameData.game());
        try (var conn = DatabaseManager.getConnection()){
            String statement = "INSERT INTO gameData (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?);";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, gameData.whiteUsername());
                preparedStatement.setString(2, gameData.blackUsername());
                preparedStatement.setString(3, gameData.gameName());
                preparedStatement.setString(4, gameString);
                preparedStatement.executeUpdate();
                String iDStatement = "SELECT LAST_INSERT_ID();";
                try(var preparedIdStatement = conn.prepareStatement(iDStatement)){
                    var response = preparedIdStatement.executeQuery();
                    while(response.next()){
                        int id = response.getInt("LAST_INSERT_ID()");
                        return id;
                    }
                }

            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));        }
        return -404; //Theoretically unreachable
    }

    @Override
    public GameData getGame(int gameID)
            throws DataAccessException,BadRequestException {
        try (var conn = DatabaseManager.getConnection()){
            String statement = "SELECT whiteUsername, blackUsername, gameName, game FROM gameData WHERE gameId = ?;";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setInt(1,gameID);
                try(var response = preparedStatement.executeQuery()){
                    GameData gameData = null;
                    while(response.next()){
                        String whiteUsername = response.getString("whiteUsername");
                        String blackUsername = response.getString("blackUsername");
                        String gameName = response.getString("gameName");
                        String gameString = response.getString("game");
                        chess.ChessGame chessGame = serializer.fromJson(gameString, chess.ChessGame.class);
                        gameData = new GameData(gameID,whiteUsername,blackUsername,gameName,chessGame);
                    }
                    if(gameData == null){
                        throw new BadRequestException("Error: bad request");
                    }
                    return gameData;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            String statement = "SELECT * FROM gameData;";
            try(var preparedStatement = conn.prepareStatement(statement)){
                try(var response = preparedStatement.executeQuery()){
                    Collection<GameData> games = new ArrayList<>();
                    while(response.next()){
                        int gameID = response.getInt("gameId");
                        String whiteUsername = response.getString("whiteUsername");
                        String blackUsername = response.getString("blackUsername");
                        String gameName = response.getString("gameName");
                        String gameString = response.getString("game");
                        chess.ChessGame chessGame = serializer.fromJson(gameString, chess.ChessGame.class);
                        GameData gameData = new GameData(gameID,whiteUsername,blackUsername,gameName,chessGame);
                        games.add(gameData);
                    }
                    return games;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));        }
    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {
        getGame(gameID); //getGame should throw a BadRequestException(?) if the game isn't there.
        try (var conn = DatabaseManager.getConnection()){
            String statement = "UPDATE gameData SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameId = ?;";
            String gameString = serializer.toJson(gameData.game());
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, gameData.whiteUsername());
                preparedStatement.setString(2, gameData.blackUsername());
                preparedStatement.setString(3, gameData.gameName());
                preparedStatement.setString(4,gameString);
                preparedStatement.setInt(5,gameID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));        }
    }

    @Override
    public void clear() throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()){
            String statement = "TRUNCATE TABLE gameData";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Error: Internal Server error: %s",e.getMessage()));
        }
    }

    @Override
    public int getNextID() {
        return 0; //This should be fine since it's only used in GameService::createGame, while the DAO::createGame ignores it
    }
}

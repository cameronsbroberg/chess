package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySqlGameDAO implements GameDAO{
    private Gson serializer;
    public MySqlGameDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new ResponseException(e.getMessage());
        }
        Gson serializer = new Gson();

    }
    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()){
            String statement =             """
                CREATE TABLE IF NOT EXISTS gameData (
                gameId INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                game VARCHAR(255) NOT NULL,
                PRIMARY KEY (gameID)
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
    public int createGame(GameData gameData) {
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
                    var response = preparedStatement.executeQuery();
                    while(response.next()){
                        int id = response.getInt("gameID");
                        return id;
                    }
                }

            }
        } catch (SQLException e) {
            throw new ResponseException(e.getMessage());
        } catch (DataAccessException e) {
            throw new ResponseException(e.getMessage());
        }
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
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
                        throw new DataAccessException("Error: bad request");
                    }
                    return gameData;
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {

    }

    @Override
    public void clear() {

    }

    @Override
    public int getNextID() {
        return 0;
    }
}

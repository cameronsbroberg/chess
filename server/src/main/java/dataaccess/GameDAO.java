package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(GameData gameData);
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames();
    void updateGame(int gameID, GameData gameData) throws DataAccessException;
    void clear();
    int getNextID();
}

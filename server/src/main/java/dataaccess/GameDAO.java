package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(GameData gameData); //FIXME What parameters? Return GameID.
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames();
    void updateGame(int gameID, GameData gameData) throws DataAccessException; //FIXME: what parameters
    void clear();
    int getNextID();
}

package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(GameData gameData); //FIXME What parameters? Return GameID.
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    void updateGame(int gameID, GameData gameData); //FIXME: what parameters
    void clear();
}

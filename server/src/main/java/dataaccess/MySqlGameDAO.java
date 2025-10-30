package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySqlGameDAO implements GameDAO{
    public MySqlGameDAO() {
        return;
    }

    @Override
    public int createGame(GameData gameData) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
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

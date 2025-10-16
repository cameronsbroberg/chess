package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    private int nextID = 1;
    private HashMap<Integer, GameData> games = new HashMap<>();
    public int createGame(GameData gameData) {
        games.put(nextID, gameData);
        return nextID++; //FIXME: make sure that this actually updates afterwards.
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void updateGame(int gameID, GameData gameData) {
        games.replace(gameID, gameData); //FIXME: This could be simplified to not be copying a bunch of existing data.
    }

    public void clear() {
        games.clear();
    }
}

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

    public int getNextID() {
        return nextID;
    }

    public GameData getGame(int gameID) throws DataAccessException{
        if(games.containsKey(gameID)){
            return games.get(gameID);
        }
        else{
            throw new DataAccessException("Game not found");
        }
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void updateGame(int gameID, GameData gameData) throws DataAccessException{
        if(games.containsKey(gameID)){
            games.replace(gameID, gameData);
        }
        else{
            throw new DataAccessException("Game not found");
        }
    }

    public void clear() {
        games.clear();
    }
}

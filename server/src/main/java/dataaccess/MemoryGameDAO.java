package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private int nextID = 1;
    private HashMap<Integer, GameData> games = new HashMap<>();

    public int createGame(GameData gameData) {
        games.put(nextID, gameData);
        return nextID++;
    }

    public int getNextID() {
        return nextID;
    }

    public GameData getGame(int gameID) throws DataAccessException{
        if(games.containsKey(gameID)){
            return games.get(gameID);
        }
        else{
            throw new DataAccessException("Error: bad request");
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
            throw new DataAccessException("Error: bad request");
        }
    }

    public void clear() {
        games.clear();
        nextID = 1;
    }
}

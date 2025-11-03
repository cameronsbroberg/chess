package database;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MySqlGameDAO;
import dataaccess.ResponseException;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class GameDAOtests {
    private static final GameData TEST_GAME = new GameData(49,null,null,"FIRST GAME",new ChessGame());
    private static final GameData BAD_TEST_GAME = new GameData(78,null,null,null,null);
    private static final Collection<GameData> gamesWithGoodIds = new ArrayList<>();
    private static final GameData TEST_GAME_1 = new GameData(1,null,null,"First Game",new ChessGame());
    private static final GameData TEST_GAME_2 = new GameData(2,null,null,"Second Game",new ChessGame());
    private static final GameData TEST_GAME_3 = new GameData(3,null,null,"Third Game",new ChessGame());

    private MySqlGameDAO gameDao = null;

    @Test
    @DisplayName("Initialize database")
    public void startTable(){
        this.gameDao = Assertions.assertDoesNotThrow(MySqlGameDAO::new);
    }
    @Test
    @DisplayName("Clear successfully")
    public void clearTable(){
        startTable();
        Assertions.assertDoesNotThrow(gameDao::clear);
    }
    @Test
    @DisplayName("Create game")
    public void createGame(){
        clearTable();
        int gameId = Assertions.assertDoesNotThrow(()->gameDao.createGame(TEST_GAME));
        Assertions.assertEquals(gameId, 1);
    }
    @Test
    @DisplayName("Create bad game")
    public void createBadGame(){
        clearTable();
        Assertions.assertThrows(ResponseException.class,()->gameDao.createGame(BAD_TEST_GAME));
    }
    @Test
    @DisplayName("Get game that's there")
    public void getGame(){
        clearTable();
        int gameId = Assertions.assertDoesNotThrow(()->gameDao.createGame(TEST_GAME));
        GameData gameData = Assertions.assertDoesNotThrow(()->gameDao.getGame(gameId));
        Assertions.assertEquals(TEST_GAME.whiteUsername(),gameData.whiteUsername());
        Assertions.assertEquals(TEST_GAME.blackUsername(),gameData.blackUsername());
        Assertions.assertEquals(TEST_GAME.gameName(),gameData.gameName());
        Assertions.assertEquals(TEST_GAME.game(),gameData.game());
    }
    @Test
    @DisplayName("Get nonexistent game")
    public void getBadGame(){
        clearTable();
        Assertions.assertThrows(DataAccessException.class,()->gameDao.getGame(23));
    }
    @Test
    @DisplayName("Get list of games")
    public void getlist(){
        clearTable();
        gamesWithGoodIds.add(TEST_GAME_1);
        gamesWithGoodIds.add(TEST_GAME_2);
        gamesWithGoodIds.add(TEST_GAME_3);
        for (GameData game : gamesWithGoodIds){
            Assertions.assertDoesNotThrow(()->gameDao.createGame(game));
        }
        Collection<GameData> gameList = Assertions.assertDoesNotThrow(gameDao::listGames);
        Assertions.assertEquals(gamesWithGoodIds,gameList);
    }
    @Test
    @DisplayName("Get empty list of games") //Hopefully this works as a negative test because I don't see another way to test
    public void getEmptylist(){
        clearTable();
        Collection<GameData> gameList = Assertions.assertDoesNotThrow(gameDao::listGames);
        Assertions.assertEquals(new ArrayList<>(),gameList);
    }
    @Test
    @DisplayName("Update game successfully")
    public void updateGame(){
        clearTable();
        int gameId = Assertions.assertDoesNotThrow(()->gameDao.createGame(TEST_GAME));
        Assertions.assertDoesNotThrow(()->gameDao.updateGame(gameId,TEST_GAME_1));
        GameData updatedGame = Assertions.assertDoesNotThrow(()->gameDao.getGame(gameId));
        Assertions.assertEquals(TEST_GAME_1,updatedGame);
    }
    @Test
    @DisplayName("Update game that's not there ")
    public void updateFakeGame(){
        clearTable();
        Assertions.assertThrows(DataAccessException.class,()-> gameDao.updateGame(4,TEST_GAME_1));
    }
}

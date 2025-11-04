package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.BadRequestException;

import java.util.ArrayList;
import java.util.Collection;

public class GameDAOtests {
    private static final GameData TEST_GAME = new GameData(49,null,null,"FIRST GAME",new ChessGame());
    private static final GameData BAD_TEST_GAME = new GameData(78,null,null,null,null);
    private static final Collection<GameData> gamesInOrder = new ArrayList<>();
    private static final GameData TEST_GAME_1 = new GameData(1,null,null,"First Game",new ChessGame());
    private static final GameData TEST_GAME_2 = new GameData(2,null,null,"Second Game",new ChessGame());
    private static final GameData TEST_GAME_3 = new GameData(3,null,null,"Third Game",new ChessGame());

    private static MySqlGameDAO gameDao;

    @BeforeEach
    public void initialize(){
        gameDao = Assertions.assertDoesNotThrow(() -> new MySqlGameDAO());
        Assertions.assertDoesNotThrow(gameDao::clear);
    }
    @Test
    @DisplayName("Clear successfully")
    public void clearTable(){
        Assertions.assertNotNull(gameDao);
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
        Assertions.assertThrows(DataAccessException.class,()->gameDao.createGame(BAD_TEST_GAME));
    }
    @Test
    @DisplayName("Get game that's there")
    public void getGame(){
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
        Assertions.assertThrows(BadRequestException.class,()->gameDao.getGame(23));
    }
    @Test
    @DisplayName("Get list of games")
    public void getlist(){
        gamesInOrder.add(TEST_GAME_1);
        gamesInOrder.add(TEST_GAME_2);
        gamesInOrder.add(TEST_GAME_3);
        for (GameData game : gamesInOrder){
            Assertions.assertDoesNotThrow(()->gameDao.createGame(game));
        }
        Collection<GameData> gameList = Assertions.assertDoesNotThrow(gameDao::listGames);
        Assertions.assertEquals(gamesInOrder,gameList);
    }
    @Test
    @DisplayName("Get empty list of games") //Hopefully this works as a negative test because I don't see another way to test
    public void getEmptylist(){
        Collection<GameData> gameList = Assertions.assertDoesNotThrow(gameDao::listGames);
        Assertions.assertEquals(new ArrayList<>(),gameList);
    }
    @Test
    @DisplayName("Update game successfully")
    public void updateGame(){
        int gameId = Assertions.assertDoesNotThrow(()->gameDao.createGame(TEST_GAME));
        Assertions.assertDoesNotThrow(()->gameDao.updateGame(gameId,TEST_GAME_1));
        GameData updatedGame = Assertions.assertDoesNotThrow(()->gameDao.getGame(gameId));
        Assertions.assertEquals(TEST_GAME_1,updatedGame);
    }
    @Test
    @DisplayName("Update game that's not there ")
    public void updateFakeGame(){
        Assertions.assertThrows(BadRequestException.class,()-> gameDao.updateGame(4,TEST_GAME_1));
    }
}

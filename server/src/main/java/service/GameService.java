package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import requests.CreateRequest;

public class GameService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public int createGame(CreateRequest createRequest){//The int is the gameID
        try {
            authDAO.getAuth(createRequest.authToken());
            int gameID = gameDAO.getNextID();

            GameData gameData = new GameData(gameID,
                    createRequest.whiteUsername(),
                    createRequest.blackUsername(),
                    createRequest.gameName(),
                    createRequest.game());
            return gameDAO.createGame(gameData); //FIXME is this bad practice? This is kind of weird code.
        } catch (DataAccessException e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }
}

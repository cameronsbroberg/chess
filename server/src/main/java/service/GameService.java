package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;
import results.CreateResult;
import results.GameListResult;
import results.GameSummary;

import java.util.ArrayDeque;
import java.util.Collection;

public class GameService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private boolean isAuthorized(String authToken) throws InvalidTokenException{
        try {
            authDAO.getAuth(authToken);
            return true;
        } catch (DataAccessException e) {
            throw new InvalidTokenException("Error: unauthorized");
        }
    }

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public GameListResult listGames(String authToken) {
        if(isAuthorized(authToken)){
            Collection<GameSummary> gameList = new ArrayDeque<>();
            for (GameData game : gameDAO.listGames()){
                GameSummary gameSummary = new GameSummary(game.gameId(),game.whiteUsername(),game.blackUsername(),game.gameName());
                gameList.add(gameSummary);
            }
            return new GameListResult(gameList);
        }
        return null; //Theoretically unreachable.
    }

    public CreateResult createGame(CreateRequest createRequest) {
        if(isAuthorized(createRequest.authToken())){
            int gameID = gameDAO.getNextID();
            GameData gameData = new GameData(gameID,
                    createRequest.whiteUsername(),
                    createRequest.blackUsername(),
                    createRequest.gameName(),
                    createRequest.game());
            return new CreateResult(gameDAO.createGame(gameData));
        }
        return null; //Theoretically unreachable
    }

    public void joinGame(String authToken, JoinRequest joinRequest) throws BadRequestException{
        if(!isAuthorized(authToken)) {
            return;
        }
        try{
            GameData gameData = gameDAO.getGame(joinRequest.gameID());
            String username = authDAO.getAuth(authToken).username();
                switch (joinRequest.teamColor()){
                    case WHITE -> {
                        if(gameData.whiteUsername() == null){
                            GameData updatedGameData = new GameData(gameData.gameId(),
                                    username,
                                    gameData.blackUsername(),
                                    gameData.gameName(),
                                    gameData.game());
                            gameDAO.updateGame(updatedGameData.gameId(), updatedGameData);
                        }
                        else {
                            throw new AlreadyTakenException("Error: already taken");
                        }
                    }
                    case BLACK -> {
                        if(gameData.blackUsername() == null){
                            GameData updatedGameData = new GameData(gameData.gameId(),
                                    gameData.whiteUsername(),
                                    username,
                                    gameData.gameName(),
                                    gameData.game());
                            gameDAO.updateGame(updatedGameData.gameId(), updatedGameData);
                        }
                        else {
                            throw new AlreadyTakenException("Error: already taken");
                        }
                    }
                }
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void clear() {
        this.gameDAO.clear();
        this.authDAO.clear();
        this.userDAO.clear();
    }
}

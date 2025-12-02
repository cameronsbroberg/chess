package handler;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameId, Session session){
        Set<Session> existingSet = connections.get(gameId);
        if(existingSet == null){
            Set<Session> newSet = Set.of(session);
            connections.put(gameId, newSet);
        }
        else{
            existingSet.add(session);
            connections.replace(gameId,existingSet);
        }
    }

    public void remove(int gameId, Session session){
        Set<Session> existingSet = connections.get(gameId);
        if(existingSet == null){
            return; //TODO: Throw an error?
        }
        else{
            existingSet.remove(session);
            connections.replace(gameId,existingSet);
        }
    }

    public void broadcast(int gameId, String message){
        //TODO: broadcast a message to all clients involved in the game
    }
}

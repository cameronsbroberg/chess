package handler;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameId, Session session){
        connections.putIfAbsent(gameId,ConcurrentHashMap.newKeySet());
        connections.get(gameId).add(session);
//        Set<Session> existingSet = connections.get(gameId);
//        if(existingSet == null){
//            Set<Session> newSet = Set.of(session);
//            connections.put(gameId, newSet);
//        }
//        else{
//            existingSet.add(session);
//            connections.replace(gameId,existingSet);
//        }
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

    public void broadcast(Session excludeSession, int gameId, String message) throws IOException {
        for(Session connection : connections.get(gameId)){
            if(connection.isOpen() && !connection.equals(excludeSession)){
                connection.getRemote().sendString(message);
            }
        }
        //broadcast a message to all other clients involved in the game
    }
}

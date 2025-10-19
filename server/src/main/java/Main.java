import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8008);

        System.out.println("♕ 240 Chess Server");
    }
}
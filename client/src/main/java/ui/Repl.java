package ui;

import chess.ChessGame;
import chess.ChessPiece;
import serverFacade.ServerFacade;
import server.Server;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private PreLoginClient client;
    private final Server server;
    private final ServerFacade serverFacade;
    public Repl(){
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
        this.client = new PreLoginClient(serverFacade);
    }
    public void run(){
        System.out.println("Welcome to Chess!");
        while(true){
            System.out.println("Type a command");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            System.out.println(client.eval(line));
        }
    }
}

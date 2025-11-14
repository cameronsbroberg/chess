package ui;

import chess.ChessGame;
import chess.ChessPiece;
import serverFacade.ServerFacade;
import server.Server;

import java.util.Objects;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private Client client;
//    private final Server server;
    private final ServerFacade serverFacade;

    public Repl(String port){
//        server = new Server();
//        var port = server.run(0);
//        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
        this.client = new PreLoginClient(serverFacade,this);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void run(){
        System.out.println("Welcome to Chess!");
        var result = "";
        while(!result.equals("quit")){
            System.out.println("Type a command");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            result = client.eval(line);
            System.out.println(result);
        }
        System.out.println("Thank you for playing.");
//        server.stop();
    }
}

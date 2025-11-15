package ui;
import serverfacade.ServerFacade;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private Client client;
    private final ServerFacade serverFacade;

    public Repl(String port){
        serverFacade = new ServerFacade("http://localhost:" + port);
        this.client = new PreLoginClient(serverFacade,this);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void run(){
        System.out.println(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "Welcome to Chess!");
        System.out.println(client.helpString());
        var result = "";
        while(!result.equals("quit")){
            System.out.println("Type a command");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            result = client.eval(line);
            System.out.println(result);
        }
        System.out.println("Thank you for playing.");
    }
}

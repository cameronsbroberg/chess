package ui;

import server.Server;
import serverFacade.ServerFacade;

public abstract class Client {
    protected ServerFacade serverFacade;
    protected Repl repl;
    abstract String eval(String input);
}

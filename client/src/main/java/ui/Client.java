package ui;

import serverFacade.ServerFacade;

public abstract class Client {
    protected ServerFacade serverFacade;
    protected Repl repl;
    abstract String eval(String input);
    abstract String helpString();
}

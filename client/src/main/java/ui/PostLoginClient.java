package ui;

public class PostLoginClient implements Client{
    private String authToken;
    public PostLoginClient(String authToken){
        this.authToken = authToken;
    }
    @Override
    public String eval(String input) {
        return "ACK I've logged in but I don't know what I'm doing!";
    }
}

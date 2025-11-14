package serverFacade;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import requests.CreateRequest;
import requests.LoginRequest;
import results.CreateResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void clear(){
        var request = baseRequest("DELETE","/db",null).build();
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public AuthData register(UserData userData) throws ResponseException{
        var request = baseRequest("POST","/user",userData)
                .build();
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    };

    public AuthData login(LoginRequest loginRequest) throws ResponseException{
        var request = baseRequest("POST","/session",loginRequest)
                .build();
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException{
        var request = baseRequest("DELETE","/session",null)
                .header("authorization",authToken)
                .build();
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public CreateResult createGame(CreateRequest createRequest) throws ResponseException{
        var request = baseRequest("POST", "/game", createRequest)
                .header("authorization",createRequest.authToken())
                .build();
        var response = sendRequest(request);
        return handleResponse(response, CreateResult.class);
    }

    private HttpRequest.Builder baseRequest(String method, String path, Object body){
        return HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
    }
//    private HttpRequest buildRequest(String method, String path, Object body){
//        var request = HttpRequest.newBuilder()
//                .uri(URI.create(serverUrl + path))
//                .method(method, makeRequestBody(body));
//        if(body != null) {
//            request.setHeader("Content-Type","application/json"); //What does this do?
//        }
//        return request.build();
//    };

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        }
        else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new ResponseException(body);
            }
            throw new ResponseException("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

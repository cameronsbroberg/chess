package database;

import dataaccess.DatabaseManager;
import dataaccess.MySqlUserDAO;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import server.Server;

public class DAOTests {
    private static final UserData TEST_USER = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");

    private static Server server;

    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    public void initializationTest() {
        MySqlUserDAO userDAO = new MySqlUserDAO();

        //TODO I guess this stuff shouldn't go here. My code should still be functional and I should have the SQL-accessing portions in my DAOs.
//        try(var conn = DatabaseManager.getConnection()){
//            for(var statement : queries){
//                try(var preparedStatement = conn.prepareStatement(statement)){
//                    preparedStatement.executeUpdate();
//                }
//            }
//        }
    }
}

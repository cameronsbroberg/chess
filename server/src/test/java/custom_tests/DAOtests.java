package custom_tests;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import passoff.chess.EqualsTestingUtility;

public class DAOtests {

    @Test
    @DisplayName("Create and get Auth")
    public void createAndAddAuth(){
        AuthData authData = new AuthData("genericToken","cambam");
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        authDAO.createAuth("cambam");

        AuthData foundAuth = Assertions.assertDoesNotThrow(() -> authDAO.getAuth("genericToken"));
        Assertions.assertEquals(authData, foundAuth);

        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("fakeToken"),"Authtoken not found");
    }

    @Test
    @DisplayName("Create and get Auth")
    public void addAndDeleteAuth(){
        AuthData authData = new AuthData("genericToken","cambam");
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        authDAO.createAuth("cambam");

        AuthData foundAuth = Assertions.assertDoesNotThrow(() -> authDAO.getAuth("genericToken"));
        Assertions.assertEquals(authData, foundAuth);

        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth("genericToken"));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("genericToken"),"Authtoken not found");
    }
}

package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private DataAccess makeDao() {
        return new dataaccess.MemoryDataAccess();
    }

    @Test
    void register_positive_returnsAuthTokenAndUsername() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);

        var result = userService.register(new UserData("alice", "pw", "a@a.com"));

        assertNotNull(result);
        assertEquals("alice", result.username);
        assertNotNull(result.authToken);
        assertFalse(result.authToken.isEmpty());
    }

    @Test
    void register_negative_duplicateUsername_throwsForbidden() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);

        userService.register(new UserData("alice", "pw", "a@a.com"));

        var ex = assertThrows(DataAccessException.class,
                () -> userService.register(new UserData("alice", "pw2", "a2@a.com")));

        assertEquals("forbidden", ex.getMessage());
    }

    @Test
    void login_positive_returnsAuthTokenAndUsername() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);

        userService.register(new UserData("bob", "pw", "b@b.com"));
        var result = userService.login("bob", "pw");

        assertNotNull(result);
        assertEquals("bob", result.username);
        assertNotNull(result.authToken);
        assertFalse(result.authToken.isEmpty());
    }
}

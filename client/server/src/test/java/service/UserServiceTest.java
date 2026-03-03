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

    @Test
    void login_negative_wrongPassword_throwsUnauthorized() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);

        userService.register(new UserData("bob", "pw", "b@b.com"));

        var ex = assertThrows(DataAccessException.class,
                () -> userService.login("bob", "WRONG"));

        assertEquals("unauthorized", ex.getMessage());
    }

    @Test
    void logout_positive_invalidatesToken() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);

        var reg = userService.register(new UserData("carl", "pw", "c@c.com"));
        String token = reg.authToken;

        userService.logout(token);

        // After logout, token should be invalid -> unauthorized on logout again
        var ex = assertThrows(DataAccessException.class, () -> userService.logout(token));
        assertEquals("unauthorized", ex.getMessage());
    }

    @Test
    void logout_negative_nullToken_throwsUnauthorized() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);

        var ex = assertThrows(DataAccessException.class, () -> userService.logout(null));
        assertEquals("unauthorized", ex.getMessage());
    }
}

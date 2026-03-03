package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import handler.AuthResult;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess dao;

    public UserService(DataAccess dao) {
        this.dao = dao;
    }

    public static class RegisterResult {
        public String username;
        public String authToken;

        public RegisterResult(String username, String authToken) {
            this.username = username;
            this.authToken = authToken;
        }
    }

    public RegisterResult register(UserData user) throws DataAccessException {
        if (user == null || user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("bad request");
        }
        dao.createUser(user);
        String token = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(token, user.username()));
        return new RegisterResult(user.username(), token);
    }

    public AuthResult login(String username, String password) throws DataAccessException{
        if (username == null || password == null) {
            throw new DataAccessException("bad request");
        }
        UserData user = dao.getUser(username);
        if (user == null) {
            throw new DataAccessException("unauthorized");
        }
        if (!user.password().equals(password)) {
            throw new DataAccessException("unauthorized");
        }
        String token = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(token, username));
        return new AuthResult(username, token);
    }

    public void logout(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("unauthorized");
        }

        if (dao.getAuth(authToken) == null) {
            throw new DataAccessException("unauthorized");
        }

        dao.deleteAuth(authToken);
    }
}

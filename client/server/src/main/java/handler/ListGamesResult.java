package handler;

import java.util.Collection;

public class ListGamesResult {
    public Collection<?> games;

    public ListGamesResult(Collection<?> games) {
        this.games = games;
    }
}

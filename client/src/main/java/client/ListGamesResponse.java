package client;

import java.util.List;

public record ListGamesResponse(List<GameData> games) {
}

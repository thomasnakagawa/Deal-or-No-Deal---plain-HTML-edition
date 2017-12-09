package GameState;

import GameState.GameState;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameStateCache {
    private Map<String, GameStateCacheRecord> gameStateMap;
    private int maxNumberOfGamesStored;

    public GameStateCache() {
        gameStateMap = new HashMap<>();
    }

    public String createNewGame() {
        String ID = generateGameStateID();
        gameStateMap.put(ID, new GameStateCacheRecord());
        return ID;
    }

    public GameState getGameState(String id) {
        return gameStateMap.get(id).getGameState();
    }

    public void removeGameState(String id) {
        gameStateMap.remove(id);
    }

    private synchronized String generateGameStateID() {
        return UUID.randomUUID().toString();
    }

    private boolean hasRecordExpired(GameStateCacheRecord record) {
        return record.getCreatedAt().plus > LocalDateTime.now()
    }
}

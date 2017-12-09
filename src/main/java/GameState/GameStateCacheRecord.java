package GameState;

import java.time.LocalDateTime;

class GameStateCacheRecord {
    private GameState gameState;
    private LocalDateTime createdAt;

    public GameStateCacheRecord() {
        gameState = new GameState();
        createdAt = LocalDateTime.now();
    }

    public GameState getGameState() {
        return gameState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

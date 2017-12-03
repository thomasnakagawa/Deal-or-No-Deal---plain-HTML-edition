import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameStateCache {
    private Map<String, GameState> gameStateMap;

    public GameStateCache() {
        gameStateMap = new HashMap<>();
    }

    public String createNewGame() {
        String ID = generateGameStateID();
        gameStateMap.put(ID, new GameState());
        return ID;
    }

    public GameState getGameState(String id) {
        return gameStateMap.get(id);
    }

    public void removeGameState(String id) {
        gameStateMap.remove(id);
    }

    private synchronized String generateGameStateID() {
        return UUID.randomUUID().toString();
    }
}

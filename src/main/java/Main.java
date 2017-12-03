import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        GameStateCache gameStateCache = new GameStateCache();

        get("/start", (req, res) -> {
           String id = gameStateCache.createNewGame();
           GameState gameState = gameStateCache.getGameState(id);
           return ViewRenderer.renderCaseView(id, gameState);
        });

        get("/game/*/case/*", (req, res) -> {
            // get values from request
            String gameID = req.splat()[0];
            int caseNumber = Integer.parseInt(req.splat()[1]);

            // get associated GameState
            GameState gameState = gameStateCache.getGameState(gameID);

            // open the case
            boolean isTimeForBankerOffer = gameState.casesToOpenUntilBankerOffer() == 1;
            gameState.chooseCase(caseNumber);

            // render the next view
            if (isTimeForBankerOffer) {
                String offer = Formatter.formatMoney(gameState.generateBankerOffer());
                return ViewRenderer.renderBankerView(gameID, gameState, offer);
            }else {
                return ViewRenderer.renderCaseView(gameID, gameState);
            }
        });

        get("/game/*/deal/*", (req, res) -> {
            // get values from request
            String gameID = req.splat()[0];
            boolean deal = req.splat()[1].equals("yes");

            // get associate GameState
            GameState gameState = gameStateCache.getGameState(gameID);

            // validate that this is a correct time for a deal/no deal
            // TODO

            // process the deal and render the next view
            if (deal) {
                // delete the game state, ensure no backsies
                gameStateCache.removeGameState(gameID);

                return "Congrats bruh";
            }else {
                // serve the next case view
                return ViewRenderer.renderCaseView(gameID, gameState);
            }
        });
    }
}
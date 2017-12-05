import GameState.GameState;
import GameState.Case;
import GameState.GameStateCache;
import Util.Formatter;
import Util.ViewRenderer;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        GameStateCache gameStateCache = new GameStateCache();

        get("/", (req, res) -> {
            return ViewRenderer.renderHomeScreen();
        });

        get("/start", (req, res) -> {
            String id = gameStateCache.createNewGame();
            GameState gameState = gameStateCache.getGameState(id);
            return ViewRenderer.renderCaseView(id, gameState);
        });

        get("/game/*/case/*", (req, res) -> {
            // get values from request
            String gameID = req.splat()[0];
            int caseNumber = Integer.parseInt(req.splat()[1]);

            // get associated GameState.GameState
            GameState gameState = gameStateCache.getGameState(gameID);

            // open the case
            boolean isTimeForBankerOffer = gameState.casesToOpenUntilBankerOffer() == 1;
            gameState.chooseCase(caseNumber);

            // render the next view
            if (isTimeForBankerOffer) {
                String offer = Formatter.formatMoney(gameState.generateBankerOffer());
                return ViewRenderer.renderBankerView(gameID, gameState, offer);
            } else {
                return ViewRenderer.renderCaseView(gameID, gameState);
            }
        });

        get("/game/*/deal/*", (req, res) -> {
            // get values from request
            String gameID = req.splat()[0];
            boolean deal = req.splat()[1].equals("yes");

            // get associate GameState.GameState
            GameState gameState = gameStateCache.getGameState(gameID);

            // validate that this is a correct time for a deal/no deal
            // TODO

            // process the deal and render the next view
            if (deal) {
                // delete the game state, ensure no backsies
                gameStateCache.removeGameState(gameID);

                return ViewRenderer.renderEnding(gameID, gameState, false, true);
            } else {
                // serve the next case view
                // if time for swap view, show that
                if (gameState.isTimeForSwap()) {
                    return ViewRenderer.renderSwapView(gameID, gameState);
                } else {
                    return ViewRenderer.renderCaseView(gameID, gameState);
                }
            }
        });

        get("/game/*/swap/*", (req, res) -> {
            // get values from request
            String gameID = req.splat()[0];
            boolean swap = req.splat()[1].equals("yes");

            // get associate GameState.GameState
            GameState gameState = gameStateCache.getGameState(gameID);

            // apply swap if chose to
            if (swap) {
                gameState.swapTheLastTwoCases();
            }

            // render next view
            return ViewRenderer.renderEnding(gameID, gameState, swap, false);
        });

        post("/game/*/leaderboard", (req, res) -> {
            // post to leaderboard
            
            return ViewRenderer.renderLeaderboard();
        });

        get("/game/leaderboard", (req, res) -> {
            return ViewRenderer.renderLeaderboard();
        });
    }
}
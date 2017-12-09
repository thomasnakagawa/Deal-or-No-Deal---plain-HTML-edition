import GameState.GameState;
import GameState.GameStateCache;
import GameState.EndingType;
import Util.Formatter;
import Util.LeaderboardAccessor;
import Util.ViewRenderer;

import java.io.IOException;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) throws IOException {
        port(80);
        System.out.println("Running");

        GameStateCache gameStateCache = new GameStateCache();

        get("/", (req, res) -> ViewRenderer.renderHomeScreen());
        get("/about", (req, res) -> ViewRenderer.renderAboutPage());
        get("/leaderboard", (req, res) -> ViewRenderer.renderLeaderboard(LeaderboardAccessor.getLeaderboardScores()));

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
            gameState.chooseCase(caseNumber);

            // render the next view
            if (gameState.needDecisionOnOffer()) {
                return ViewRenderer.renderBankerView(gameID, gameState);
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

            // process the deal and render the next view
            if (deal) {
                gameState.finalizeGame(EndingType.Deal);
                return ViewRenderer.renderEnding(gameID, gameState);
            } else {
                // serve the next case view
                // if time for swap view, show that
                gameState.declineOffer();
                if (gameState.isTimeForSwap()) {
                    // TODO: this only works because the swap option always happens after a deal decision. If the rules change and that isnt the case, this check will have to happen somewhere else
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
                gameState.finalizeGame(EndingType.Swap);
            }else {
                gameState.finalizeGame(EndingType.NoSwap);
            }

            // render next view
            return ViewRenderer.renderEnding(gameID, gameState);
        });

        post("/game/*/leaderboard", (req, res) -> {
            // get values from request
            String gameID = req.splat()[0];
            String name = req.queryParams("name");

            // get associate GameState
            GameState gameState = gameStateCache.getGameState(gameID);
            int score = gameState.getFinalWinnings();
            gameStateCache.removeGameState(gameID);

            // post to leaderboard
            return ViewRenderer.renderLeaderboard(LeaderboardAccessor.postScoreAndGetLeaderboardScores(name, score));
        });
    }
}
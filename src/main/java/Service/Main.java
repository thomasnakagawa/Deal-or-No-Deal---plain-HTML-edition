package Service;

import GameState.GameState;
import GameState.Ending.EndingType;
import Rendering.ViewRenderer;

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
            return ViewRenderer.renderNextGameView(id, gameState);
        });

        get("/game/*/case/*", (req, res) -> {
            // get values from request
            String gameID = req.splat()[0];
            int caseNumber = Integer.parseInt(req.splat()[1]);

            // get associated GameState
            GameState gameState = gameStateCache.getGameState(gameID);

            // open the case
            gameState.chooseCase(caseNumber);

            // render the next view
            return ViewRenderer.renderNextGameView(gameID, gameState);
        });

        get("/game/*/deal/*", (req, res) -> {
            // get values from request
            String gameID = req.splat()[0];
            boolean deal = req.splat()[1].equals("yes");

            // get associated GameState
            GameState gameState = gameStateCache.getGameState(gameID);

            // process the deal
            if (deal) {
                gameState.finalizeGame(EndingType.Deal);
            } else {
                gameState.declineOffer();
            }

            // render the next view
            return ViewRenderer.renderNextGameView(gameID, gameState);
        });

        get("/game/*/swap/*", (req, res) -> {
            // get values from request
            String gameID = req.splat()[0];
            boolean swap = req.splat()[1].equals("yes");

            // get associated GameState
            GameState gameState = gameStateCache.getGameState(gameID);

            // apply swap if chose to
            if (swap) {
                gameState.finalizeGame(EndingType.Swap);
            }else {
                gameState.finalizeGame(EndingType.NoSwap);
            }

            // render next view
            return ViewRenderer.renderNextGameView(gameID, gameState);
        });

        post("/game/*/leaderboard", (req, res) -> {
            // get values from request
            String gameID = req.splat()[0];
            String name = req.queryParams("name");

            // get associated GameState
            GameState gameState = gameStateCache.getGameState(gameID);
            int score = gameState.getFinalWinnings();
            gameStateCache.removeGameState(gameID);

            // post to leaderboard
            return ViewRenderer.renderLeaderboard(LeaderboardAccessor.postScoreAndGetLeaderboardScores(name, score));
        });
    }
}
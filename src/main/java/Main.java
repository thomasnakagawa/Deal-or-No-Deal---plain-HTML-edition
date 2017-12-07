import GameState.GameState;
import GameState.GameStateCache;
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
        get("/leaderboard", (req, res) -> ViewRenderer.renderLeaderboard(LeaderboardAccessor.getLeaderboardScores(), null));

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
                int finalScore = (int) Math.floor(gameState.getLatestOfferValue());
                gameState.setFinalWinnings(finalScore);

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

            // set the final score
            int finalScore = (int) Math.floor(gameState.getChosenCase().getValue());
            gameState.setFinalWinnings(finalScore);

            // render next view
            return ViewRenderer.renderEnding(gameID, gameState, swap, false);
        });

        post("/game/*/leaderboard", (req, res) -> {
            System.out.println("here");
            // get values from request
            String gameID = req.splat()[0];
            String name = req.queryParams("name");

            // get associate GameState.GameState
            GameState gameState = gameStateCache.getGameState(gameID);
            int score = gameState.getFinalWinnings();
            gameStateCache.removeGameState(gameID);

            // post to leaderboard
            return ViewRenderer.renderLeaderboard(LeaderboardAccessor.postScoreAndGetLeaderboardScores(name, score), name);
        });
    }
}
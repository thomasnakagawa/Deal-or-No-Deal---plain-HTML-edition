package GameState;

import GameState.Case.Case;
import GameState.Ending.EndingData;
import GameState.Ending.EndingType;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {
    private CaseCollection caseCollection;
    private Banker banker;
    private EndingData endingData;
    private GamePhase gamePhase;

    public GameState() {
        caseCollection = new CaseCollection(GameRules.MONEY_AMOUNTS);
        banker = new Banker();
        gamePhase = GamePhase.choosingInitialCase;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public boolean isTimeForSwap() {
        // it's time to give the option to swap when there is only one closed case left
        int closedCases = caseCollection.getClosedCases().size();
        return closedCases == 1;
    }

    public void chooseCase(int caseNumber) {
        if (gamePhase == GamePhase.choosingInitialCase) {
            caseCollection.ownCase(caseNumber);
            gamePhase = GamePhase.openingCases;
        }else if (gamePhase == GamePhase.openingCases) {
            boolean timeForOffer = casesToOpenUntilBankerOffer() == 1;
            caseCollection.openCase(caseNumber);
            // if its time, have banker make an offer
            if (timeForOffer) {
                makeBankerOffer();
                gamePhase = GamePhase.pendingDeal;
            }
        }else {
            throw new IllegalStateException("Cannot choose case at this time");
        }
    }

    public int casesToOpenUntilBankerOffer() {
        int casesOpened = caseCollection.getOpenedCases().size();
        int nextOfferAt = Arrays.asList(GameRules.BANKER_OFFERS_AT).stream()
                .filter(offer -> offer > casesOpened)
                .min(Integer::compare).get();
        return nextOfferAt - casesOpened;
    }

    private void makeBankerOffer() {
        List<Float> moneyValues = caseCollection
                .getClosedCases()
                .stream()
                .map(c -> c.getValue())
                .collect(Collectors.toList());

        banker.generateOffer(moneyValues);
    }

    public double getLatestOffer() {
        return banker.getOfferHistory().get(0);
    }
    public void declineOffer() {
        if (gamePhase != GamePhase.pendingDeal) {
            throw new IllegalStateException("Cannot decline offer because there isn't one");
        }
        if (isTimeForSwap()) {
            gamePhase = GamePhase.pendingSwap;
        }else {
            gamePhase = GamePhase.openingCases;
        }
    }

    public List<Double> getFullOfferHistory() {
        return banker.getOfferHistory();
    }

    public List<Double> getOfferHistoryMinusMostRecent() {
        List<Double> history = getFullOfferHistory();
        return history.subList(1, history.size());
    }

    public Case getTheFinalCase() {
        if (gamePhase != GamePhase.finished ) {
            throw new IllegalStateException("Cannot get final case");
        }

        List<Case> closedCases = caseCollection.getClosedCases();
        if (closedCases.size() != 1) {
            throw new IllegalStateException("Cannot get final case, there is more than one");
        }
        return closedCases.get(0);
    }

    public List<Case> getCaseList() {
        return caseCollection.getAllCases();
    }

    public List<Case> getMoneyTableState() {
        return getCaseList().stream()
                .sorted((case1, case2) -> Float.compare(case1.getValue(), case2.getValue()))
                .collect(Collectors.toList());
    }

    public Case getOwnCase() {
        return caseCollection.getOwnedCase();
    }

    public void finalizeGame(EndingType endingType) {
        if (endingType == null) {
            throw new IllegalArgumentException("EndingData type cannot be null");
        }
        if (endingData != null) {
            throw new IllegalStateException("Cannot finalize the game after it has already been finalized");
        }
        int finalWinnings = -1;
        switch(endingType) {
            case Deal:
                if (gamePhase != GamePhase.pendingDeal) {
                    throw new IllegalStateException("Cannot accept deal at this time");
                }
                finalWinnings = (int) Math.floor(banker.getOfferHistory().get(0));
                break;
            case Swap:
                if (gamePhase != GamePhase.pendingSwap) {
                    throw new IllegalStateException("Cannot swap at this time");
                }
                caseCollection.swapChosenCaseWithLastClosedCase();
                finalWinnings = (int) Math.floor(caseCollection.getOwnedCase().getValue());
                break;
            case NoSwap:
                if (gamePhase != GamePhase.pendingSwap) {
                    throw new IllegalStateException("Cannot decline swap at this time");
                }
                finalWinnings = (int) Math.floor(caseCollection.getOwnedCase().getValue());
                break;
            default:
                throw new IllegalArgumentException("Cannot finalize the game with this ending type");
        }
        gamePhase = GamePhase.finished;
        endingData = new EndingData(endingType, finalWinnings);
    }

    public int getFinalWinnings() {
        if (endingData == null || gamePhase != GamePhase.finished) {
            throw new IllegalStateException("Cannot get winnings before finalizing the game");
        }
        return endingData.getPrizeMoney();
    }

    public EndingType getEndingType() {
        if (endingData == null || gamePhase != GamePhase.finished) {
            throw new IllegalStateException("Cannot get ending type before finalizing the game");
        }
        return endingData.getEndingType();
    }
}

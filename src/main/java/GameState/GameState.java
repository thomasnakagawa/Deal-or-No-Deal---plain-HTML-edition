package GameState;

import java.util.*;
import java.util.stream.Collectors;

public class GameState {
    private CaseCollection caseCollection;
    private Banker banker;
    private EndingState endingState;
    private boolean latestOfferNeedsDecision;

    public GameState() {
        caseCollection = new CaseCollection(GameRules.MONEY_AMOUNTS);
        banker = new Banker();
        latestOfferNeedsDecision = false;
    }

    public boolean isTimeForSwap() {
        // it's time to give the option to swap when there is only one closed case left
        int closedCases = caseCollection.getClosedCases().size();
        return closedCases == 1;
    }

    public void chooseCase(int caseNumber) {
        if (caseCollection.hasChosenOwnCase()) {
            caseCollection.openCase(caseNumber);
            // if its time, have banker make an offer
            if (casesToOpenUntilBankerOffer() == 0) {
                 makeBankerOffer();
            }
        }else {
            caseCollection.ownCase(caseNumber);
        }
    }

    private void makeBankerOffer() {
        List<Float> moneyValues = caseCollection
                .getClosedCases().stream()
                .map(c -> c.getValue())
                .collect(Collectors.toList());
        banker.generateOffer(moneyValues);
        latestOfferNeedsDecision = true;
    }

    public boolean needDecisionOnOffer() {
        return latestOfferNeedsDecision;
    }

    public double getLatestOffer() {
        return banker.getOfferHistory().get(0);
    }
    public void declineOffer() {
        if (needDecisionOnOffer() == false) {
            throw new IllegalStateException("Cannot decline offer because there isn't one");
        }
        latestOfferNeedsDecision = false;
    }

    public List<Double> getFullOfferHistory() {
        return banker.getOfferHistory();
    }

    public List<Double> getOfferHistoryMinusMostRecent() {
        List<Double> history = getFullOfferHistory();
        return history.subList(1, history.size());
    }

    public int casesToOpenUntilBankerOffer() {
        int casesOpened = caseCollection.getOpenedCases().size();
        int nextOfferAt = Arrays.asList(GameRules.BANKER_OFFERS_AT).stream()
                .filter(offer -> offer > casesOpened)
                .min(Integer::compare).get();
        return nextOfferAt - casesOpened;
    }

    public Case getTheFinalCase() {
        if (!isTimeForSwap()) {
            throw new IllegalStateException("Cannot get final case because its not time for the swap");
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
        return getCaseList().stream().sorted((case1, case2) -> Float.compare(case1.getValue(), case2.getValue())).collect(Collectors.toList());
    }

    private boolean hasChosenOwnCase() {
        return caseCollection.hasChosenOwnCase();
    }

    public void finalizeGame(EndingType endingType) {
        if (endingState != null) {
            throw new IllegalStateException("Cannot finalize the game after it has already been finalized");
        }
        if (endingType == null) {
            throw new IllegalArgumentException("Ending type cannot be null");
        }
        int finalWinnings = -1;
        switch(endingType) {
            case Deal:
                // TODO: ensure this is ok time to take deal.
                finalWinnings = (int) Math.floor(banker.getOfferHistory().get(0));
                break;
            case Swap:
                // TODO: ensure this is ok time to swap. Closed cases = 1 or something
                caseCollection.swapChosenCaseWithLastClosedCase();
                finalWinnings = (int) Math.floor(caseCollection.getOwnedCase().getValue());
                break;
            case NoSwap:
                // TODO: ensure this is ok time to not swap. Closed cases = 1 or something
                finalWinnings = (int) Math.floor(caseCollection.getOwnedCase().getValue());
                break;
            default:
                throw new IllegalArgumentException("Cannot finalize the game with this ending type");
        }

        endingState = new EndingState(endingType, finalWinnings);
    }

    public int getFinalWinnings() {
        if (endingState == null) {
            throw new IllegalStateException("Cannot get winnings before finalizing the game");
        }
        return endingState.getPrizeMoney();
    }

}

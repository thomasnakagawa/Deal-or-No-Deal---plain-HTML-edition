package GameState;

import java.util.*;
import java.util.stream.Collectors;

import Util.Formatter;

public class GameState {
    private List<Case> caseList;
    private List<Double> pastBankerOffers;
    private boolean hasSwapped;
    private int finalWinnings = -1;

    public GameState() {
        caseList = randomizeCases();
        pastBankerOffers = new ArrayList<>();
        hasSwapped = false;
    }

    public void chooseCase(int caseNumber) {
        if (caseNumber < 1 || caseNumber > GameRules.MONEY_AMOUNTS.length) {
            throw new IllegalArgumentException("There is no case with that number");
        }
        Case chosenCase = getCaseByNumber(caseNumber);
        if (hasChosenOwnCase()) {
            // the initial case has been chosen, open the given case
            chosenCase.setCaseState(CaseState.Opened);
        }else {
            // no initial case had been chosen, so choose this one
            chosenCase.setCaseState(CaseState.Chosen);
        }
    }

    public Case getChosenCase() {
        return caseList.stream()
                .filter(c -> c.getCaseState() == CaseState.Chosen) // not inefficient because lazy evaluation
                .findFirst().orElse(null);
    }

    public int casesToOpenUntilBankerOffer() {
        int casesOpened = (int) caseList.stream().filter(c -> c.getCaseState() == CaseState.Opened).count();
        int nextOfferAt = Arrays.asList(GameRules.BANKER_OFFERS_AT).stream().filter(offer -> offer > casesOpened).min(Integer::compare).get();
        return nextOfferAt - casesOpened;
    }

    public boolean isTimeForSwap() {
        int casesOpened = (int) caseList.stream().filter(c -> c.getCaseState() == CaseState.Opened).count();
        return casesOpened == GameRules.MONEY_AMOUNTS.length - 2;
    }

    public void swapTheLastTwoCases() {
        if (!isTimeForSwap()) {
            throw new IllegalStateException("Cannot swap at this time");
        }

        Case finalCase = getTheFinalCase();
        finalCase.setCaseState(CaseState.Chosen);

        Case chosenCase = getChosenCase();
        chosenCase.setCaseState(CaseState.Closed);

        hasSwapped = true;
    }

    public Case getTheFinalCase() {
        if (!isTimeForSwap()) {
            throw new IllegalStateException("Cannot get final case at this time");
        }
        return caseList.stream()
                .filter(c -> c.getCaseState() == CaseState.Closed)
                .findFirst().orElseThrow(IllegalStateException::new);
    }

    public boolean hasSwappedCases() {
        return hasSwapped;
    }

    public List<Case> getCaseList() {
        return caseList;
    }

    public double generateBankerOffer() {
        // http://commcognition.blogspot.ca/2007/06/deal-or-no-deal-bankers-formula.html
        double expectedValue = 0f;
        int numberUnopened = 0;
        float maxValueRemaining = -1f;
        float minValueRemaining = 9999999999f;
        for (Case c : caseList) {
            if (c.getCaseState() != CaseState.Opened) {
                numberUnopened += 1;
                expectedValue += c.getValue();
                maxValueRemaining = Math.max(c.getValue(), maxValueRemaining);
                minValueRemaining = Math.min(c.getValue(), minValueRemaining);
            }
        }
        expectedValue /= numberUnopened;

        double offer =  12275.3 +
                (0.748 * expectedValue) +
                (-2714.74 * numberUnopened) +
                (-0.04 * maxValueRemaining) +
                (0.0000006986 * expectedValue * expectedValue) +
                (32.623 * numberUnopened * numberUnopened);

        pastBankerOffers.add(offer);
        return offer;
    }

    public String getLatestOffer() {
        return Formatter.formatMoney(pastBankerOffers.get(pastBankerOffers.size() - 1));
    }

    public List<String> getOfferHistory() {
        List<String> returnList = new ArrayList<>();
        for (int i = pastBankerOffers.size() - 2; i >= 0; i--) { // - 2 because skipping the most recent one
            returnList.add(Formatter.formatMoney(pastBankerOffers.get(i)));
        }
        return returnList;
    }

    public List<String> getCompleteOfferHistory() {
        List<String> returnList = new ArrayList<>();
        for (int i = pastBankerOffers.size() - 1; i >= 0; i--) { // - 1 because including the most recent one
            returnList.add(Formatter.formatMoney(pastBankerOffers.get(i)));
        }
        return returnList;
    }

    public List<Case> getMoneyTableState() {
        return caseList.stream().sorted((case1, case2) -> Float.compare(case1.getValue(), case2.getValue())).collect(Collectors.toList());
    }

    private List<Case> randomizeCases() {
        List<Float> randomMoneyAmounts = Arrays.asList(GameRules.MONEY_AMOUNTS);
        Collections.shuffle(randomMoneyAmounts);

        List<Case> cases = new ArrayList<>();
        for (int i = 0; i < randomMoneyAmounts.size(); i++) {
            cases.add(new Case(i + 1, randomMoneyAmounts.get(i)));
        }
        return cases;
    }

    private Case getCaseByNumber(int caseNumber) {
        return caseList.stream()
                .filter(c -> c.getNumber() == caseNumber) // not inefficient because lazy evaluation
                .findFirst().orElse(null);
    }

    private boolean hasChosenOwnCase() {
        return caseList.stream().anyMatch(c -> c.getCaseState() == CaseState.Chosen);
    }

    private void setFinalWinnings(int moneyAmount) {
        if (finalWinnings != -1) {
            throw new IllegalStateException("Cannot set winnings after it's been set");
        }
        finalWinnings = moneyAmount;
    }

    public int getFinalWinnings() {
        return finalWinnings;
    }

}

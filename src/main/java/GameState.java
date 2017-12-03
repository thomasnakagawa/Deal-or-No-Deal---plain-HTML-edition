import java.util.*;

public class GameState {
    private List<Case> caseList;

    public GameState() {
        caseList = randomizeCases();
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


        return 12275.3 +
                (0.748 * expectedValue) +
                (-2714.74 * numberUnopened) +
                (-0.04 * maxValueRemaining) +
                (0.0000006986 * expectedValue * expectedValue) +
                (32.623 * numberUnopened * numberUnopened);

    }

    public Map<String, CaseState> getMoneyTableState() {
        Map<String, CaseState> moneyTableState = new HashMap<>();
        for (float moneyAmount : GameRules.MONEY_AMOUNTS) {
            Case associatedCase = caseList.stream().filter(c -> c.getValue() == moneyAmount).findFirst().orElseThrow(IllegalStateException::new);
            moneyTableState.put(associatedCase.getValueFormatted(), associatedCase.getCaseState());
        }
        return moneyTableState;
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

}

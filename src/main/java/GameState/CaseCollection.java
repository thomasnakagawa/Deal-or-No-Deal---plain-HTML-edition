package GameState;

import GameState.Case.Case;
import GameState.Case.CaseState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class CaseCollection {
    private List<Case> caseList;
    private boolean swappedCases = false;

    public CaseCollection(Float[] moneyAmounts) {
        caseList = randomizeCases(moneyAmounts);
    }

    private List<Case> randomizeCases(Float[] moneyAmounts) {
        List<Float> randomMoneyAmounts = Arrays.asList(moneyAmounts);
        Collections.shuffle(randomMoneyAmounts);

        List<Case> cases = new ArrayList<>();
        for (int i = 0; i < randomMoneyAmounts.size(); i++) {
            cases.add(new Case(i + 1, randomMoneyAmounts.get(i)));
        }
        return cases;
    }

    public void ownCase(int caseNumber) {
        Case chosenCase = getCaseByNumber(caseNumber);
        chosenCase.setCaseState(CaseState.Owned);
    }

    public void openCase(int caseNumber) {
        Case chosenCase = getCaseByNumber(caseNumber);
        chosenCase.setCaseState(CaseState.Opened);
    }

    private Case getCaseByNumber(int caseNumber) {
        return caseList.stream()
                .filter(c -> c.getNumber() == caseNumber)
                .findFirst().orElseThrow(() -> new IllegalStateException("Invalid case number"));
    }

    public List<Case> getAllCases() {
        return caseList;
    }

    public Case getOwnedCase() {
        List<Case> ownedCases = getCasesOfState(CaseState.Owned);
        if (ownedCases.size() != 1) {
            throw new IllegalStateException("There is not exactly one owned case");
        }
        return ownedCases.get(0);
    }

    public List<Case> getOpenedCases() {
        return getCasesOfState(CaseState.Opened);
    }

    public List<Case> getClosedCases() {
        return getCasesOfState(CaseState.Closed);
    }

    private List<Case> getCasesOfState(CaseState caseState) {
        return caseList.stream()
                .filter(c -> c.getCaseState() == caseState)
                .collect(Collectors.toList());
    }

    public void swapChosenCaseWithLastClosedCase() {
        if (hasSwappedCases()) {
            throw new IllegalStateException("Cannot swap cases, a swap has already occured");
        }
        List<Case> closedCases = getClosedCases();
        if (closedCases.size() != 1) {
            throw new IllegalStateException("Cannot swap cases, there is not exactly one closed case");
        }
        Case newChosenCase = closedCases.get(0);
        Case oldChosenCase = getOwnedCase();

        oldChosenCase.setCaseState(CaseState.Closed);
        newChosenCase.setCaseState(CaseState.Owned);

        swappedCases = true;
    }

    public boolean hasSwappedCases() {
        return swappedCases;
    }
}

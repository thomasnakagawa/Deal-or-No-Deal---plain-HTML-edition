package GameState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class CaseCollection {
    private List<Case> caseList;
    private boolean chosenOwnCase = false;
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
        if (hasChosenOwnCase()) {
            throw new IllegalStateException("Cannot choose own case, it has already been chosen");
        }
        Case chosenCase = getCaseByNumber(caseNumber);
        chosenCase.setCaseState(CaseState.Owned);
        chosenOwnCase = true;
    }

    public void openCase(int caseNumber) {
        if (hasChosenOwnCase() == false) {
            throw new IllegalStateException("Cannot open case, haven't chosen own case yet");
        }
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
        if (hasChosenOwnCase() == false) {
            throw new IllegalStateException("Cannot get owned case before choosing the case");
        }
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

    public boolean hasChosenOwnCase() {
        return chosenOwnCase;
    }

    public void swapChosenCaseWithLastClosedCase() {
        if (hasSwappedCases()) {
            throw new IllegalStateException("Cannot swap cases, a swap has already occured");
        }
        List<Case> closedCases = getClosedCases();
        if (closedCases.size() != 1) {
            throw new IllegalStateException("Cannot sawp cases, there is not exactly one closed case");
        }
        Case newChosenCase = closedCases.get(0);

        getOwnedCase().setCaseState(CaseState.Opened);
        newChosenCase.setCaseState(CaseState.Owned);

        swappedCases = true;
    }

    public boolean hasSwappedCases() {
        return swappedCases;
    }
}

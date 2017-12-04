package GameState;

import Util.Formatter;

public class Case {
    private int number;
    private float value;
    private CaseState caseState;

    public Case(int number, float value) {
        this.number = number;
        this.value = value;
        this.caseState = CaseState.Closed;
    }

    public int getNumber() {
        return number;
    }

    public float getValue() {
        return value;
    }

    public String getValueFormatted() {
        return Formatter.formatMoney(value);
    }

    public CaseState getCaseState() {
        return this.caseState;
    }

    public void setCaseState(CaseState caseState) {
        this.caseState = caseState;
    }
}


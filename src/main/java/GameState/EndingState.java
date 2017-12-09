package GameState;

public class EndingState {
    private EndingType endingType;
    private int prizeMoney;

    public EndingState(EndingType endingType, int prizeMoney) {
        this.endingType = endingType;
        this.prizeMoney = prizeMoney;
    }

    public EndingType getEndingType() {
        return endingType;
    }

    public int getPrizeMoney() {
        return prizeMoney;
    }
}

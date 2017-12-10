package GameState.Ending;

public class EndingData {
    private EndingType endingType;
    private int prizeMoney;

    public EndingData(EndingType endingType, int prizeMoney) {
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

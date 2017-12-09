package GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is for the GameState to call into to get banker offers, and get the history of offers
 */
class Banker {
    private List<Double> pastOffers;

    public Banker() {
        pastOffers = new ArrayList<>();
    }

    // given the money values of the cases that haven't been opened yet, make an offer to buy the player's owned case
    public double generateOffer(List<Float> concealedMoneyAmounts) {
        // http://commcognition.blogspot.ca/2007/06/deal-or-no-deal-bankers-formula.html
        int numberUnopened = concealedMoneyAmounts.size();

        double expectedValue = 0f;
        float maxValueRemaining = -1f;
        float minValueRemaining = 9999999999f;
        for (float moneyAmount : concealedMoneyAmounts) {
            numberUnopened += 1;
            expectedValue += moneyAmount;
            maxValueRemaining = Math.max(moneyAmount, maxValueRemaining);
            minValueRemaining = Math.min(moneyAmount, minValueRemaining);
        }
        expectedValue /= numberUnopened;

        double offer =  12275.3 +
                (0.748 * expectedValue) +
                (-2714.74 * numberUnopened) +
                (-0.04 * maxValueRemaining) +
                (0.0000006986 * expectedValue * expectedValue) +
                (32.623 * numberUnopened * numberUnopened);

        pastOffers.add(0, offer); // adds to the front
        return offer;
    }

    // get the list of past offers, ordered from most to least recent
    public List<Double> getOfferHistory() {
       return pastOffers;
    }
}

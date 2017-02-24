package Server.GameLogic;

/**
 * Created by peter on 21-02-2017.
 */


import java.util.ArrayList;

/**
 * Class Hand represents each player's four playable cards
 * Game is going to have a Hand, representing the active cards of each turn
 */
public class Hand {

    private ArrayList<Card> activeCards;

    public ArrayList<Card> getActiveCards() {
        return activeCards;
    }


    public void clear() {

        for (Card iCard : activeCards) {
            activeCards.remove(iCard);
        }
    }


    public void receiveCard(Card card) {
        activeCards.add(card);
    }
}

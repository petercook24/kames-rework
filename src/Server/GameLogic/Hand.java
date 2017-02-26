package Server.GameLogic;

/**
 * Created by peter on 21-02-2017.
 */


import java.util.Vector;

/**
 * Class Hand represents each player's four playable cards
 * Game is going to have a Hand, representing the active cards of each turn
 */
public class Hand {

    private Vector<Card> activeCards;

    public Hand(){
        activeCards = new Vector<>();
    }

    public Vector<Card> getActiveCards() {
        return activeCards;
    }


    public void clear() {

        if(activeCards.size() == 0){
            return;
        }
        activeCards = new Vector<>();

    }


    public void receiveCard(Card card) {
        activeCards.add(card);
    }

    public void removeCard(Card card){
        activeCards.remove(card);
    }

    @Override
    public String toString(){

        String cardsValues = "";

        for (Card iCard : activeCards) {
            cardsValues += " " + iCard.getValue();
        }
        return cardsValues;
    }
}

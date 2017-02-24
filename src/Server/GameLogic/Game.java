package Server.GameLogic;

import Server.Chat1.ClientDispatcher;
import Server.Chat1.Chat1;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by tiagoRodrigues on 18/02/2017.
 */

/**
 * Server Responsabilities:
 * <p>
 * Server.Chat1.Chat1:
 * Start the chat
 * <p>
 * <p>
 * CARDS:
 * Have a cards and pick cards randomly from it whenever needed
 * Know the cards on the table
 * Know the cards of each player
 * Interpret a player request to change cards
 * Check whether or not a player request to change cards is valid
 * Inform a player if the card switch was successful
 * Know the number of rounds played
 * Evaluate if a player won when "Kames / Corta Kames" is called
 * Manage how long each round lasts
 * When round ends, put new cards on the table
 */

public class Game {


    private Chat1 chat;
    private Deck deck;
    private HashMap<ClientDispatcher, Hand> players;
    private Hand tableHand; //SHARED MUTABLE STATE


    public void init() {
        chat = new Chat1(this);
        deck = new Deck();
        players = new HashMap<>(4);// Inits a map for 4 players
        tableHand = new Hand();
        chat.startChat(); //STARTS THE CHAT
        setSignal();
    }

    private void setSignal() {

    }

   public void start() {
       showForbiddenCard();
       giveInitialCardsToPlayers();
       startNewTurn();
    }

    private void startNewTurn() {

        burnTableHand();
        drawTableCards();
        while (!isTurnOver()) {
            keepProcessingTrades();//este metodo pode ter um nome melhor! SERÁ AQUI UM DO PRROBS DO MULTITHREADING?
        }
        startNewTurn();
    }

    public void endGame(ClientDispatcher player, String endCommand) {

        ClientDispatcher enemy1 = null;
        ClientDispatcher enemy2 = null;

        for (ClientDispatcher iPlayer : getPlayersSet()) {
            if (iPlayer.getTeam() != player.getTeam()) {

                if (enemy1 == null) {
                    enemy1 = iPlayer;
                    continue;
                }
                enemy2 = iPlayer;
            }
        }

        if (endCommand.equals("KAMES")) {
            if (hasKames(getPartner(player))) {
                winRound(player, getPartner(player));
                return;
            }
            winRound(enemy1, enemy2);
            return;
        }
        if (endCommand.equals("CORTA")) {
            if (hasKames(enemy1) || hasKames(enemy2)) {
                winRound(player, getPartner(player));
                return;
            }
            winRound(enemy1, enemy2);
        }
    }

    private String showForbiddenCard() {
        return deck.showLastCard().getValue();
    }

    private void giveInitialCardsToPlayers() {
        for (ClientDispatcher iPlayer : getPlayersSet()) {
            deck.give4CardsTo(getPlayersMap().get(iPlayer));
        }
    }

    private void drawTableCards() {
        deck.give4CardsTo(tableHand);
    }

    private void burnTableHand() {
        tableHand.clear();
    }

    private boolean isTurnOver() {
        throw new UnsupportedOperationException();
    }

    private void keepProcessingTrades() {
        throw new UnsupportedOperationException();
    }

    /**
     * Switches a player's card with one card from the table
     *
     * @param tableCard  table's card that the player wants to grab
     * @param playerCard player's card that he wants to put on the table
     * @returns true if card switch is made, false if not
     */
    public boolean switchTableCardWith(Card tableCard, Card playerCard) {
        if (!tableHand.getActiveCards().remove(tableCard)) {
            return false;
        }
        tableHand.getActiveCards().add(playerCard);
        return true;
    }

    public HashMap<ClientDispatcher, Hand> getPlayersMap() {
        return players;
    }

    public Set<ClientDispatcher> getPlayersSet() {
        return players.keySet();
    }


}

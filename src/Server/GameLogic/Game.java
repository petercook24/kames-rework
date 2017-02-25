package Server.GameLogic;

import Server.Chat1.ClientDispatcher;
import Server.Chat1.Chat1;
import Server.Chat1.Messager;

import java.util.Hashtable;
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
    private Hashtable<ClientDispatcher, Hand> players;
    private Hand tableHand; //SHARED MUTABLE STATE
    private int roundsPlayed;
    private long lastCommandTime;
    private long waitTime = 10000;
    private long endtime = lastCommandTime + waitTime;
    //private long timeSinceLastCommand = System.currentTimeMillis() - lastCommandTime;

    public void init() {
        chat = new Chat1(this);
        deck = new Deck();
        players = new Hashtable<>(4);// Inits a map for 4 players
        tableHand = new Hand();
        chat.startChat(); //STARTS THE CHAT
    }


    public void startNewGame() {

        System.out.println("GAME IS STRATING-----------------");

        roundsPlayed = 0;
        showForbiddenCard();
        giveInitialCardsToPlayers();
        startNewTurn();
    }


    private void startNewTurn() {

        int turnCycles = 0;

        burnTableHand();
        drawTableCards();

        while (!isTurnOver()) {

            keepProcessingTrades();
        }
    }
    

    public void endGame(ClientDispatcher player, String endGameCommand) {

        ClientDispatcher enemy1 = null;
        ClientDispatcher enemy2 = null;
        ClientDispatcher partner = getPartner(player);

        for (ClientDispatcher iPlayer : getPlayersSet()) {
            if (iPlayer != player && iPlayer != partner) {
                if (enemy1 == null) {
                    enemy1 = iPlayer;
                    continue;
                }
                enemy2 = iPlayer;
            }
        }

        if (endGameCommand.equals("/K")) {
            if (hasKames(partner)) {
                winRound(player, partner);
                return;
            }
            winRound(enemy1, enemy2);
            return;
        }
        if (endGameCommand.equals("/C")) {
            if (hasKames(enemy1) || hasKames(enemy2)) {
                winRound(player, partner);
                return;
            }
            winRound(enemy1, enemy2);
        }
    }

    private void showForbiddenCard() {
        String cardValue = deck.showLastCard().getValue();
        chat.broadcast(Messager.getChatLastCardIsMessage(cardValue));
    }

    private void giveInitialCardsToPlayers() {
        for (ClientDispatcher iPlayer : getPlayersSet()) {

            Hand playerHand = getPlayersMap().get(iPlayer);
            deck.give4CardsTo(playerHand);

            iPlayer.sendMessage(Messager.getClientCardsReceivedMessage(playerHand.toString()));
        }
    }

    private void drawTableCards() {
        deck.give4CardsTo(tableHand);
        chat.broadcast(Messager.getChatCardsOnTableMessage(tableHand.toString()));
    }

    private void burnTableHand() {
        tableHand.clear();
        chat.broadcast(Messager.getChatTableCardsClearedMessage());
    }

    private boolean isTurnOver() {
        throw new UnsupportedOperationException();
    }

    private void keepProcessingTrades() {
    
        try {
            wait(10000);
            if (endtime() >= 10000) {
                System.out.println("STARTTING A NEW TURN!");
                startNewTurn();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        
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
        chat.broadcast(Messager.getChatCardsOnTableMessage(tableHand.toString()));
        
        
        long curTime = System.currentTimeMillis();
        System.out.println( "CURTIME IS: " + curTime);
       // System.out.println("time since last command is: " + timeSinceLastCommand);
        
        resetLastCommandTime(curTime);
        //System.out.println("AND NOW time since last command is**: " + timeSinceLastCommand);
        System.out.println("A card was changed");
        return true;
        
    }


    public Hashtable<ClientDispatcher, Hand> getPlayersMap() {
        return players;
    }

    public Set<ClientDispatcher> getPlayersSet() {
        return players.keySet();
    }

    private boolean hasKames(ClientDispatcher player) {

        String referenceValue = "";

        for (Card iCard : players.get(player).getActiveCards()) {
            if (referenceValue.isEmpty()) {
                referenceValue = iCard.getValue();
                continue;
            }
            if (!iCard.getValue().equals(referenceValue)) {
                return false;
            }
        }
        return true;
    }

    private ClientDispatcher getPartner(ClientDispatcher player) {

        String playerTeam = player.getTeam();

        for (ClientDispatcher iPlayer : getPlayersSet()) {
            if (iPlayer != player && iPlayer.getTeam().equals(playerTeam)) {
                return iPlayer;
            }
        }
        System.err.println("SOMETHING WENT TERRIBLY WRONG GETTING PARTNER");
        return null;
    }


    private void winRound(ClientDispatcher player, ClientDispatcher partner) {
        player.win();
        partner.win();
        roundsPlayed++;
    }

    public Hand getTableHand() {
        return tableHand;
    }
    
    public long getLastCommandTime () {
        return lastCommandTime;
    }
    
    public void resetLastCommandTime (long timeLastCommand) {
        this.lastCommandTime = timeLastCommand;
    }
    
}

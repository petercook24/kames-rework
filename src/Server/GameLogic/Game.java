package Server.GameLogic;

import Server.Chat1.ClientDispatcher;
import Server.Chat1.Chat1;
import Server.Chat1.Messager;

import java.io.IOException;
import java.security.MessageDigest;
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


    public static final int ROUNDS_TO_WIN = 4;
    public static final long WAIT_TIME_BETWEEN_TURNS = 20000;

    private Chat1 chat;
    private Deck deck;
    private Hashtable<ClientDispatcher, Hand> players;
    private Hand tableHand; //SHARED MUTABLE STATE
    private long lastCommandTime;

    public void init() {
        chat = new Chat1(this);
        players = new Hashtable<>(4);// Inits a map for 4 players
        chat.startChat(); //STARTS THE CHAT
    }


    public void startNewGame() {

        chat.broadcast(Messager.getChatGameStartMessage());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        deck = new Deck();
        tableHand = new Hand();
        showForbiddenCard();
        giveInitialCardsToPlayers();
        startNewTurn();
    }


    private void startNewTurn() {
        if (isWinnerFound()) {
            endGame();
        }

        burnTableHand();
        drawTableCards();
        resetLastCommandTime();
        keepProcessingTrades();
        startNewTurn();
    }


    public void endRound(ClientDispatcher player, String endGameCommand) {

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

        if (deck.getDeckSize() <= 3) {
            chat.broadcast(Messager.getChatNoMoreCardsOnDeckMEssage(deck.getDeckSize()));
            return;
        }

        synchronized (tableHand) {
            deck.give4CardsTo(tableHand);
        }
        chat.broadcast(Messager.getChatCardsOnTableMessage(tableHand.toString()));


        //inform players of what are their cards. Just to be easier to play/test for now
        for (ClientDispatcher iPlayer : getPlayersSet()) {

            Hand playerHand = getPlayersMap().get(iPlayer);

            iPlayer.sendMessage(Messager.getClientYourHandIsMessage(playerHand.toString()));


        }
    }


    private void burnTableHand() {
        synchronized (tableHand) {
            tableHand.clear();
        }
        chat.broadcast(Messager.getChatTableCardsClearedMessage());
    }

    private void keepProcessingTrades() {
        System.out.println(getPlayersSet());
    
        for (ClientDispatcher iPlayer : getPlayersSet()) {
    
            System.out.println(iPlayer.getOut().checkError());

            if (iPlayer.getOut().checkError()){
                System.out.println("A player disconnected");
                chat.broadcast("One player disconnected. Server shut down. Game closed");
                System.exit(1);
                
            }
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (System.currentTimeMillis() - lastCommandTime < WAIT_TIME_BETWEEN_TURNS) {
            keepProcessingTrades();
            return;
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

        resetLastCommandTime();
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

        chat.broadcast(Messager.getChatWinnerMessage(player.getTeam(), player.getRoundsWon()));
        player.win();
        partner.win();
        startNewGame();
    }

    public Hand getTableHand() {
        return tableHand;
    }


    public void resetLastCommandTime() {
        lastCommandTime = System.currentTimeMillis();
    }

    private boolean isWinnerFound() {

        for (ClientDispatcher iPlayer : getPlayersSet()) {

            if (iPlayer.getRoundsWon() == ROUNDS_TO_WIN) {
                return true;
            }
        }
        return false;
    }

    private void endGame() {

        for (ClientDispatcher iPlayer : getPlayersSet()) {

            if (iPlayer.getRoundsWon() == ROUNDS_TO_WIN) {
                chat.broadcast(Messager.getChatWinningTeamMessage(iPlayer.getTeam()));
                startNewGame();
            }
        }
        System.err.println("SOMETHING REALLY WRONG ENDING GAME WHEN WINNER WAS FOUND");
    }

    public void updatePlayerHand(String nickname, Card cardToAdd, Card cardToRemove){

        for (ClientDispatcher iPlayer : getPlayersSet()){

            if (iPlayer.getNickName().equals(nickname)){

                Hand playerHand = getPlayersMap().get(iPlayer);

                playerHand.receiveCard(cardToAdd);
                playerHand.removeCard(cardToRemove);


            }

        }

    }

}

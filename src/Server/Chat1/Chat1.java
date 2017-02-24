package Server.Chat1;

import Server.GameLogic.Card;
import Server.GameLogic.Game;
import Server.GameLogic.Hand;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by codecadet on 15/02/17.
 */
public class Chat1 {

    public static final int PORT = 8080;
    public static final int MAX_USERS = 4;

    private Game game;
    private int connectedUsers = 0;
    private ServerSocket serverSocket;


    public Chat1(Game game) {

        this.game = game;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server socket created");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startChat() {

        try {
            while (true) {

                Socket clientSocket = serverSocket.accept();//Blocks while waiting for a client connection
                System.out.println("connection established to IP: " + clientSocket.getInetAddress());

                connectedUsers++;
                initClientDispatcher(clientSocket);
                if (connectedUsers == 4) {
                    setSignal(); //TODO: setSignal()
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String msg) {

        synchronized (game.getPlayersMap()) {

            for (ClientDispatcher iClientDispatcher : game.getPlayersSet()) {

                iClientDispatcher.sendMessage(msg);
                System.out.println(Messager.getServerSentMessage(iClientDispatcher.getNickName()));
            }
        }
    }

    private void initClientDispatcher(Socket clientSocket) {

        ClientDispatcher clientDispatcher = new ClientDispatcher(clientSocket, this);
        game.getPlayersMap().put(clientDispatcher, new Hand());
        clientDispatcher.setTeam(connectedUsers);

        ExecutorService pool = Executors.newFixedThreadPool(MAX_USERS);
        pool.submit(clientDispatcher);
    }

    public boolean clientExists(String userName) {

        synchronized (game.getPlayersMap()) {

            for (ClientDispatcher iClientDispatcher : game.getPlayersSet()) {
                if (iClientDispatcher.getNickName().equals(userName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void sendPrivateMessageTo(String nickName, String treatedPrivateMessage) {

        ClientDispatcher userCD = getUserClientDispatcher(nickName);
        userCD.sendMessage(Messager.getClientPrivateMessage(nickName, treatedPrivateMessage));
    }


    public void sendOnlineList(ClientDispatcher clientDispatcher) {

        synchronized (game.getPlayersMap()) {

            for (ClientDispatcher iClientDispatcher : game.getPlayersSet()) {
                clientDispatcher.sendMessage(iClientDispatcher.getNickName());
            }
        }
    }


    private ClientDispatcher getUserClientDispatcher(String nickName) {

        synchronized (game.getPlayersMap()) {

            for (ClientDispatcher iClientDispatcher : game.getPlayersSet()) {
                if (iClientDispatcher.getNickName().equals(nickName)) {
                    return iClientDispatcher;
                }
            }
        }
        return null;
    }


    public void disconnect(ClientDispatcher clientDispatcher) {

        game.getPlayersMap().remove(clientDispatcher);
        connectedUsers--;

        clientDispatcher.sendMessage(Messager.getClientDisconnectedMessage());
        broadcast(Messager.getChatDisconnectedMessage(clientDispatcher.getNickName()));
        System.out.println(Messager.getServerDisconnectedMessage(clientDispatcher.getNickName()));
    }

    public void endGame(ClientDispatcher player, String endGameCommand) {
        game.endGame(player, endGameCommand);
    }

    public void switchTableCardWith(String tableCardValue, String playerCardValue, ClientDispatcher player) {


        Hand playerHand = game.getPlayersMap().get(player);
        Card playerCard;
        Card tableCard;

        if (playerHasCard(playerCardValue, playerHand) && tableHasCard(tableCardValue)) {
            playerCard = getPlayerCard(playerCardValue, playerHand);
            tableCard = getTableCard(tableCardValue);

            synchronized (game.getTableHand()) {
                game.switchTableCardWith(tableCard, playerCard);
            }
        }
    }

    private boolean playerHasCard(String playerCardValue, Hand playerHand) {
        for (Card iCard : playerHand.getActiveCards()) {
            if (iCard.getValue().equals(playerCardValue)) {
                return true;
            }
        }
        return false;
    }

    private Card getPlayerCard(String playerCardValue, Hand playerHand){
        for (Card iCard : playerHand.getActiveCards()) {
            if (iCard.getValue().equals(playerCardValue)){
                return iCard;
            }
        }
        System.err.println("SOMETHING REALLY WRONG GETTING EXISTING PLAYER'S CARD");
        return null;
    }

    private boolean tableHasCard(String tableCardValue) {
        for (Card iCard : game.getTableHand().getActiveCards()) {
            if (iCard.getValue().equals(tableCardValue)) {
                return true;
            }
        }
        return false;
    }

    private Card getTableCard(String tableCardValue){
        for (Card iCard : game.getTableHand().getActiveCards()) {
            if (iCard.getValue().equals(tableCardValue)) {
                return iCard;
            }
        }
        System.err.println("SOMETHING REALLY WRONG GETTING EXISTING TABLE CARD");
        return null;
    }
}

package Server.Chat;

import Client.Client;
import Server.GameLogic.Game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tiagoRodrigues on 18/02/2017.
 */
public class Chat {

    /**
     * Server.Chat1.Chat1 Responsabilities
     *
     * Handle multiple clients (Client.Client Handlers)
     * Distinguish between a game command and a chat message
     * Inform gameServer if a player issued a game command
     * Broadcast messages to players
     *
     */

    private static final int PORT_NUMBER = 8080;                   // Port to listen for connections

    private Game game;                                             // Game object that will have all logic
    private ArrayList<ClientHandler> clients;                           // List of Client threads
    private boolean gameStarted;                                   // If game has started = true
    public static final int PLAYER_LIMIT = 4;                     // Limit number of players
    private int numOfConnections;                                  // Number of players



    /**
     * Starts the game:
     * 1 - Waits for client connections up to the @PLAYER_LIMIT
     * 2 - Handles the new client to a socket giving it a name.
     */
    public void start() {

        clients = new ArrayList<>();

        try {

            System.out.println("Starting server at port: " + PORT_NUMBER + ", please wait...");
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Server started: " + serverSocket);

            while (numOfConnections < PLAYER_LIMIT) {

                try {

                    // Waits for client connections
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostName());

                    // Creates a new ClientHandler
                    numOfConnections++;
                    String name = "Player-" + numOfConnections;
                    ClientHandler client = new ClientHandler(clientSocket, this);
                    clients.add(client);

                    // Serves the client connection with a new thread
                    Thread thread = new Thread(client);
                    thread.setName(name);
                    thread.start();

                } catch (IOException ex) {
                    System.out.println("Error receiving the client connection: " + ex.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Unable to start port at: " + PORT_NUMBER);
        }
    }

    /**
     * Runs a game command.
     * @param nickname to know who made it.
     * @param message the command.
     */
    public void runGameCommand(String nickname, String message){
        throw new UnsupportedOperationException();
    }

    /**
     * Send a message only to the player's partner.
     * @param partnerNickname partner nickname
     * @param nickname sender nickname
     * @param message message to send to partner
     */
    public void sendMessageToPartner(String partnerNickname, String nickname, String message){

        for(ClientHandler client: clients) {

            if(client.getNickname().equals(partnerNickname)) {

                client.sendMsgToSelf(nickname, message);
            }
        }
    }

    /**
     * Sends a message to all players
     * @param nickname nickname from who sent the message
     * @param message message to send to all
     */
    public void broadcast(String nickname, String message){

        synchronized (clients) {

            System.out.println(nickname + ":" + message);

            for(ClientHandler iClient: clients) {

                if(!iClient.getNickname().equals(nickname)) {
                    iClient.sendMsgToSelf(nickname, message);
                }
            }
        }
    }

    /**
     * Sets the team before the game starts.
     */
    public synchronized void setPartners() {

        boolean waitingToSet = true;

        while(waitingToSet) {

            int nameCount = 0;

            for(ClientHandler client: clients) {

                if(!client.getNickname().isEmpty()) {
                    nameCount++;
                }

                if(nameCount == 4) {
                    waitingToSet = false;
                }
            }
        }

        // FIRST TEAM
        clients.get(0).setPartnerNickname(clients.get(1).getNickname());
        clients.get(1).setPartnerNickname(clients.get(0).getNickname());

        // SECOND TEAM
        clients.get(2).setPartnerNickname(clients.get(3).getNickname());
        clients.get(3).setPartnerNickname(clients.get(2).getNickname());

    }

    public int getNumOfConnections() {
        return numOfConnections;
    }

    /**
     * Sets the game that will handle all the logic of the game for the chat.
     * @param game where all our game logic resides.
     */
    public void setGame(Game game) {
        this.game = game;
    }
}

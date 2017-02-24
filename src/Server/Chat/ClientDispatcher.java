package Server.Chat;

import Server.Chat.Chat;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.Socket;

/**
 * Created by tiagoRodrigues on 18/02/2017.
 */
public class ClientDispatcher implements Runnable {

    /**
     * Client.Client Handler Responsabilities
     *
     * Same shit we did before
     */

    private Chat chatServer;                        // Chat1 server
    private Socket clientSocket;
    private BufferedWriter out = null;
    private BufferedReader in = null;
    private String nickname;
    private String partnerNickname;

    public ClientDispatcher(Socket socket , Chat chatServer) throws IOException {
        this.clientSocket = socket;
        this.chatServer = chatServer;

        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {

        System.out.println("Thread " + Thread.currentThread().getName() + " has started.");

        try {

            setupNickname();

            setupPartner();

            while(!clientSocket.isClosed()) {
                ;
                // Waits for client messages
                String message = in.readLine();

                if(message == null) {
                    // There is message.
                    continue;

                } else if (!message.isEmpty()) {

                    if(message.startsWith("/")) {
                        chatServer.runGameCommand(nickname, message);
                        return;
                    }

                    chatServer.broadcast(nickname, message);
//                    // TODO: Isto será durante o início do jogo.
//                    chatServer.sendMessageToPartner(nickname, message);
//                    // TODO: Isto será durante o jogo.
//                    chatServer.broadcast(nickname, message);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the OutputStream from client handler.
     * @return
     */
    public BufferedWriter getOutputStream () {
        return out;
    }

    /**
     * Returns the player nickname.
     * @return
     */
    public String getNickname() {
        return nickname;
    }

    // TODO: Envia a mensagem para o cliente.
    public void sendMsgToSelf(String nickname, String message) {

        try {

            out.write(nickname + ": " + message);
            out.newLine();
            out.flush();

        } catch(IOException ex) {
            System.out.println("Error sending message to Client: " + nickname);
        }
    }

    public void sendMsgToPartner(String message) {

        chatServer.sendMessageToPartner(partnerNickname, nickname, message);
    }

    private void setupNickname() {

        boolean nickname = false;

        while(!nickname) {

            try {

                out.write("Please insert your nickname: ");
                out.flush();

                String message = in.readLine();

                if(message.length() > 12) {
                    out.write("Your username can't be longer than 12 characters.");
                    out.newLine();
                    out.flush();

                } else {
                    this.nickname = message;
                    nickname = true;
                    out.write("Your nickname has been set. " + message);
                    out.newLine();
                    out.flush();
                }

            } catch(IOException ex) {
                System.out.println("Error setting up player " + Thread.currentThread().getName() + ": " + ex.getMessage());
            }
        }
    }

    /**
     * Setups the teams so the game can startNewGame.
     * 1 - Waits for the 4 players
     * 2 - Calls the chat server method to setup the partners for each player
     * 3 - Writes out to the players who are their partners.
     */
    private void setupPartner() {

/* 1: */ while(chatServer.getNumOfConnections() < Chat.PLAYER_LIMIT) {

            try {

                out.write("Waiting for the other players.... Currently " + chatServer.getNumOfConnections() + " online.");
                out.newLine();
                out.flush();
                Thread.sleep(3000);

            } catch (IOException ex) {
                System.out.println("Error setting up the teams => CLIENT HANDLER : " + ex.getMessage());

            } catch (InterruptedException ex) {
                System.out.println("Error waiting for players => CLIENT HANDLER : " + ex.getMessage());
            }
        }

/* 2:*/ chatServer.setPartners();

/* 3:*/ try {
            out.write("Game - Your partner is " + partnerNickname + ".\n");
            out.flush();

        } catch(IOException ex) {
            System.out.println("Error writing the partner. CLIENT HANDLER : " + ex.getMessage());
        }
    }

    public void setPartnerNickname(String partnerNickname) {
        this.partnerNickname = partnerNickname;
    }
}

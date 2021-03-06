package Server.Chat1;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by peter on 17-02-2017.
 */
public class ClientDispatcher implements Runnable {

    private Chat1 chat1;
    private BufferedReader in;
    private PrintWriter out;
    private InetAddress ip; //TODO: DO I REALLY NEED IT?
    private String nickName;
    private String team;
    private Socket clientSocket;
    private int roundsWon;


    public ClientDispatcher(Socket clientSocket, Chat1 chat1) {

        try {
            
            this.chat1 = chat1;
            this.clientSocket = clientSocket;
            roundsWon = 0;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            ip = clientSocket.getInetAddress();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        try {
            while (!isNickNameValid()) {
                sendMessage(Messager.getClientInsertNickNameMessage());
                setNickName(in.readLine());
            }
            System.out.println("My nickname is " + nickName);
            System.out.println("My team is " + team);

            chat1.broadcast(Messager.getChatWelcomeMessage(nickName));

            //clientDispatcher need to hold here to be sure 4 players are connected

            while (chat1.getConnectedUsers() != chat1.MAX_USERS) {
                continue;
            }

            //when they are all connected, then set signal

            setSignal();


            String msg = in.readLine();

            while (msg != null) {

                System.out.println(Messager.getServerReceivedFromMessage(nickName, msg));

                if (isMessageIrregular(msg)) {
                    treatMessage(msg);
                    msg = in.readLine(); //Blocks while waiting for client's message
                    continue;
                }

                //IF MESSAGE IS REGULAR
                chat1.broadcastExcept(this.getNickName(),Messager.getChatUserSaidMessage(nickName, msg));
                msg = in.readLine(); //Blocks while waiting for client's message
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void treatMessage(String msg) {

        if (msg == null) {
            System.out.println("DISCONECTING");
            chat1.disconnect(this);
            return;
        }

        String command = msg.split(" ")[0];

        switch (command) {

            case "/QUIT":
                chat1.disconnect(this);
                break;

            case "/LIST":
                chat1.sendOnlineList(this);
                break;

            case "/K":
            case "/C":
                System.out.println("KAMES OR CORTA RECEIVED");
                chat1.broadcast(Messager.getChatKamesMessage(command, nickName, team));
                chat1.endGame(this, command);
                break;

            case "/S":
                if(msg.trim().length() > 5){
                    chat1.broadcast(Messager.getChatInvalidSwitchCommmandMessage(nickName));
                    return;
                }
                String tableCardValue = msg.split(" ")[1];
                String playerCardValue = msg.split(" ")[2];
                chat1.switchTableCardWith(tableCardValue, playerCardValue, this);
                chat1.broadcast(Messager.getChatPlayerTryingToSwitchMessage(nickName, tableCardValue, playerCardValue));


            case "/HELP":
                //sendCommandList();
                break;
            default:

        }
    }


    private boolean isMessageIrregular(String msg) {
        return msg == null || msg.length() == 0 || msg.startsWith("/");
    }


    public void sendMessage(String msg) { // sendMessageToclient.

        //try {
            out.write(msg + "\n");
            out.flush();

//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private boolean isNickNameValid() {
        return nickName != null && !nickName.equals("");
    }

    private void setNickName(String newNickName) {
        nickName = newNickName; //TODO: ADD NUMBER IF NICK ALREADY EXISTS
    }

    public String getNickName() {
        return nickName;
    }

    public void setTeam(int connectionNb) {
        if (connectionNb % 2 != 0) {
            team = "TEAM 1";
            return;
        }
        team = "TEAM 2";
    }

    public String getTeam() {
        return team;
    }

    public int getRoundsWon() {
        return roundsWon;
    }

    public void win() {
        roundsWon++;
    }

    public void setSignal() {

        long curTime = System.currentTimeMillis();
        long duration = 20000;
        long endTime = curTime + duration;

        sendMessage("You are now talking to your partner to arrange a signal. You have" + (duration / 1000) + " seconds");

        while (System.currentTimeMillis() <= endTime) {

            try {

                if (in.ready()) {
                    System.out.println("here");
                    String msg = in.readLine(); //I need this method to be non blocking
                    chat1.sendTeamMessage(team, msg);
                    System.out.println("team message sent!");

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("done waiting");
    }
    
    public Socket getClientSocket () {
        return clientSocket;
    }
    
    public PrintWriter getOut () {
        return out;
    }
}


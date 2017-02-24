package Server.Chat1;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by peter on 17-02-2017.
 */
public class ClientDispatcher implements Runnable {

    private Chat1 chat1;
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private InetAddress ip; //TODO: DO I REALLY NEED IT?
    private String nickName;
    private String team;
    private boolean connected;


    public ClientDispatcher(Socket clientSocket, Chat1 chat1) {

        try {
            this.chat1 = chat1;
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            ip = clientSocket.getInetAddress();
            connected = true;

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
            chat1.broadcast(Messager.getChatWelcomeMessage(nickName));

            setTeam();
            String msg = in.readLine();

            while (msg != null || !connected) {

                System.out.println(Messager.getServerReceivedFromMessage(nickName, msg));

                if (isMessageIrregular(msg)) {
                    treatMessage(msg);
                    msg = in.readLine(); //Blocks while waiting for client's message
                    continue;
                }

                //IF MESSAGE IS REGULAR
                chat1.broadcast(Messager.getChatUserSaidMessage(nickName, msg));
                msg = in.readLine(); //Blocks while waiting for client's message
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void treatMessage(String msg) {

        System.out.println("treating message");
        System.out.println("msg is = " + msg);

        if (msg == null) {
            System.out.println("DISCONECTING");
            chat1.disconnect(this);
            return;
        }

        String command = msg.split(" ")[0];

        switch (command) {

            case "QUIT":
                chat1.disconnect(this);
                break;

            case "POKE":
                //TODO case msg starts with POKE
                break;

            case "PM":
                String user = msg.split(" ")[1];
                String finalMessage = msg.substring(command.length() + user.length() + 2);//SPACES THAT ARE NOT COUNTED

                if (user == null) {
                    sendMessage(Messager.getClientNoUserErrorMessage());
                    break;
                }
                if (!chat1.clientExists(user)) {
                    sendMessage(Messager.getClientInvalidUserErrorMessage(user));
                    break;
                }
                chat1.sendPrivateMessageTo(user, finalMessage);
                break;

            case "LIST":
                chat1.sendOnlineList(this);
                break;

            case "HELP":
                //sendCommandList();
                break;


        }
    }


    private boolean isMessageIrregular(String msg) {
        return (msg == null ||
                msg.length() == 0) ||
                msg.startsWith("QUIT") ||
                msg.startsWith("POKE") ||
                msg.startsWith("PM") ||
                msg.startsWith("LIST");
    }


    public void sendMessage(String msg) {

        try {
            out.write(msg + "\n");
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
}


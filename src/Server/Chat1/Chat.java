package Server.Chat1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by codecadet on 15/02/17.
 */
public class Chat {

    public static final int PORT = 8080;
    public static final int MAX_USERS = 4;
    private final List<ClientDispatcher> clientDispatcherList;//SHARED MUTABLE STATE

    private int connectedUsers = 0;
    private ServerSocket serverSocket;


    public Chat() {

        clientDispatcherList = new Vector<>();

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server socket created");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startChat() {

        try {
            while (connectedUsers < MAX_USERS) {

                Socket clientSocket = serverSocket.accept();//Blocks while waiting for a client connection
                System.out.println("connection established to IP: " + clientSocket.getInetAddress());

                initClientDispatcher(clientSocket);
                connectedUsers++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String msg) {

        synchronized (clientDispatcherList) {

            for (ClientDispatcher iClientDispatcher : clientDispatcherList) {

                iClientDispatcher.sendMessage(msg);
                System.out.println(Messager.getServerSentMessage(iClientDispatcher.getNickName()));
            }
        }
    }

    private void initClientDispatcher(Socket clientSocket) {

        ClientDispatcher clientDispatcher = new ClientDispatcher(clientSocket, this);
        clientDispatcherList.add(clientDispatcher);

        ExecutorService pool = Executors.newFixedThreadPool(20);
        pool.submit(clientDispatcher);
    }

    public boolean clientExists(String userName) {

        synchronized (clientDispatcherList) {

            for (ClientDispatcher iClientDispatcher : clientDispatcherList) {
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

        synchronized (clientDispatcherList) {

            for (ClientDispatcher iClientDispatcher : clientDispatcherList) {
                clientDispatcher.sendMessage(iClientDispatcher.getNickName());
            }
        }
    }


    private ClientDispatcher getUserClientDispatcher(String nickName) {

        synchronized (clientDispatcherList) {

            for (ClientDispatcher iClientDispatcher : clientDispatcherList) {
                if (iClientDispatcher.getNickName().equals(nickName)) {
                    return iClientDispatcher;
                }
            }
        }
        return null;
    }


    public void disconnect(ClientDispatcher clientDispatcher) {

        clientDispatcherList.remove(clientDispatcher);
        connectedUsers--;

        clientDispatcher.sendMessage(Messager.getClientDisconnectedMessage());
        broadcast(Messager.getChatDisconnectedMessage(clientDispatcher.getNickName()));
        System.out.println(Messager.getServerDisconnectedMessage(clientDispatcher.getNickName()));
    }
}

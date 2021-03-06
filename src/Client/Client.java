package Client;

import Server.GameLogic.Hand;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by tiagoRodrigues on 18/02/2017.
 */
public class Client {

    /**
     * write messages to output stream
     * read messages from input stream and print them to screen
     * be able to distinguish between messages from another players and messages from the Server.GameLogic.Game
     * Know when to print a message to Server.Chat1.Chat1 sys out or to Server.GameLogic.Game sys out
     */


    private Socket socket;
    private String SERVER_IP = "localhost";
    private final int SERVER_PORT = 8080;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader userInput;
    private boolean connected;
    public LanternaTerminal lanternaTerminal;


    public void connect() {

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            lanternaTerminal = new LanternaTerminal();
            lanternaTerminal.createLanternaWindow();

            // input and output streams initializing and System.in console input
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));
            connected = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startClient() {

        //Reads from System.in @userInput  and writes to output stream @out
        //Creates an anonymous runnable class

        Thread clientWriterThread = new Thread(new Runnable() {

            @Override
            public void run() {

                String in = "";

                while (in != null) {

                    //Blocks while waiting input from client
                    try {
                        in = userInput.readLine();
                        out.println(in);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (in.equals("/QUIT"))
                        disconnect();
                }
            }
        });
        clientWriterThread.start();

        // Reader from the input stream @in and writes to client console using System.out

        while (connected) {

            String inputMsg = null;
            try {
                inputMsg = in.readLine();
            } catch (IOException e) {
                e.getMessage();
            }

            System.out.println(inputMsg);

            if(inputMsg.contains("CARDS RECEIVED")){

                //Put each of the four cards values in an array and send it to print on Lanterna GUI
                String[] playerCards = new String[]{inputMsg.split(" ")[3], inputMsg.split(" ")[4], inputMsg.split(" ")[5], inputMsg.split(" ")[6]};
                lanternaTerminal.drawPlayerHand(playerCards);
            }

            if(inputMsg.contains("TABLE CARDS:")){

                //Put each of the four cards values in an array and send it to print on Lanterna GUI
                String[] tableCards = new String[]{inputMsg.split(" ")[2], inputMsg.split(" ")[3], inputMsg.split(" ")[4], inputMsg.split(" ")[5]};
                lanternaTerminal.drawTable(tableCards);
            }

            if (inputMsg == null) {
                System.out.println("Connection lost");
                disconnect();
            }
        }
    }

    private void disconnect() {

        connected = false;
        System.out.println("DISCONNECTING");

        try {
            socket.close();
            System.exit(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




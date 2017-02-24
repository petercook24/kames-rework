package Server.Chat1;

/**
 * Created by peter on 17-02-2017.
 */
public class Messager {

    public static String getClientNoUserErrorMessage() {
        return "no PM <nickname> inserted. To send a private message: PM <nickname> <msg>";
    }

    public static String getClientInvalidUserErrorMessage(String user) {
        return "The user " + user + " does not exist.";
    }

    public static String getClientDisconnectedMessage() {
        return "YOU DISCONNECTED FROM THE SERVER";
    }

    public static String getClientPrivateMessage(String nickName, String msg) {
        return "<PM " + nickName + ">" + msg;
    }

    public static String getClientInsertNickNameMessage() {
        return "Please insert your nickname: ";
    }

    public static String getChatWelcomeMessage(String nickName) {
        return "----NEW USER JOINED SERVER --> " + nickName;
    }

    public static String getChatUserSaidMessage(String nickname, String msg) {
        return nickname + " said: " + msg;
    }

    public static String getChatDisconnectedMessage(String nickName) {
        return "----" + nickName + " DISCONNECTED ----";
    }

    public static String getServerDisconnectedMessage(String nickName) {
        return nickName + " disconnected.";
    }

    public static String getServerReceivedFromMessage(String nickName, String msg) {
        return "received message from " + nickName + ": " + msg;
    }

    public static String getServerSentMessage(String nickname) {
        return "message sent to: " + nickname;
    }

    public static String getChatInvalidSwitchCommmandMessage(String nickName){
        return  nickName + ", you inserted an invalid switch command! ";
    }

    public static String getChatPlayerTryingToSwitchMessage(String nickName, String tableCardValue, String playerCardValue) {

        return nickName + " is trying to switch table card --> " + tableCardValue + " with player card --> "
                + playerCardValue;
    }
}

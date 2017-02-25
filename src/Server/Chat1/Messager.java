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
        return "< " + nickname + " > " + msg;
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

    public static String getChatKamesMessage(String command, String nickName, String team) {
        if (command.equals("/C")) {
            return "----- " + team + "- " + nickName + " IS CORTATING";
        }
        return "----- " + team + "- " + nickName + " SHOUTED KAMES!";
    }

    public static String getChatLastCardIsMessage(String cardValue) {
        return "FORBIDDEN CARD IS --> " + cardValue + " ** BEWARE!";
    }

    public static String getClientCardsReceivedMessage(String cardsValues) {
        return "CARDS RECEIVED -->" + cardsValues;
    }

    public static String getChatTableCardsClearedMessage() {
        return "---- TABLE CARDS CLEARED -----";
    }

    public static String getChatCardsOnTableMessage(String cardsValues) {
        return "TABLE CARDS:" + cardsValues;
    }

    public static String getChatWinningTeamMessage(String team) {
        return "WINNING TEAM IS *** " + team + " *** \n" +
                "KNEEL BEFORE THE NEW KAMES KINGS!";
    }

    public static String getChatGameStartMessage() {
        return "----- PREPARE YOURSELVES, GAME IS STARTING -----";
    }

    public static String getChatNoMoreCardsOnDeckMEssage(int deckSize) {
        return "THERE ARE " + deckSize + " CARDS LEFT ON THE DECK!";
    }
}

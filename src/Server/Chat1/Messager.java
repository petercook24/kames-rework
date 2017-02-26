package Server.Chat1;

/**
 * Created by peter on 17-02-2017.
 */

public class Messager {
    public static final String ANSI_RESET = " \u001B[0m";
    public static final String ANSI_BLUE = "\u001B[44m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static String getClientNoUserErrorMessage() {
        return "no PM <nickname> inserted. To send a private message: PM <nickname> <msg>";
    }

    public static String getClientInvalidUserErrorMessage(String user) {
        return "The user " + user + " does not exist."+"\n";
    }

    public static String getClientDisconnectedMessage() {
        return ANSI_PURPLE_BACKGROUND + ANSI_GREEN + "YOU DISCONNECTED FROM THE SERVER" + ANSI_RESET+"\n";
    }

    public static String getClientPrivateMessage(String nickName, String msg) {
        return "<PM " + nickName + ">" + msg+"\n";
    }

    public static String getClientInsertNickNameMessage() {
        return ANSI_PURPLE_BACKGROUND + ANSI_GREEN + "Please insert your nickname: " + ANSI_RESET+"\n";
    }

    public static String getChatWelcomeMessage(String nickName) {
        return ANSI_PURPLE_BACKGROUND + ANSI_GREEN + "----NEW USER JOINED SERVER --> " + nickName + ANSI_RESET+"\n";
    }

    public static String getChatUserSaidMessage(String nickname, String msg) {
        return "< " + nickname + " > " + msg+"\n";
    }

    public static String getChatDisconnectedMessage(String nickName) {
        return ANSI_PURPLE_BACKGROUND + ANSI_GREEN + "----" + nickName + " DISCONNECTED ----" + ANSI_RESET+"\n";
    }

    public static String getServerDisconnectedMessage(String nickName) {
        return nickName + " disconnected."+"\n";
    }

    public static String getServerReceivedFromMessage(String nickName, String msg) {
        return "received message from " + nickName + ": " + msg+"\n";
    }

    public static String getServerSentMessage(String nickname) {
        return "message sent to: " + nickname+"\n";
    }

    public static String getChatKamesMessage(String command, String nickName, String team) {
        if (command.equals("/C")) {
            return ANSI_YELLOW_BACKGROUND + ANSI_BLUE + "----- " + team + "- " + nickName + " IS CORTATING"+ ANSI_RESET+"\n" ;
        }
        return ANSI_YELLOW_BACKGROUND + ANSI_BLUE + "----- " + team + "- " + nickName + " SHOUTED KAMES!" + ANSI_RESET+"\n";
    }

    public static String getChatLastCardIsMessage(String cardValue) {
        return ANSI_PURPLE_BACKGROUND + ANSI_GREEN + "FORBIDDEN CARD IS --> " + cardValue + " ** BEWARE!" +ANSI_RESET+"\n";
    }

    public static String getClientCardsReceivedMessage(String cardsValues) {
        return ANSI_PURPLE_BACKGROUND + ANSI_GREEN + "CARDS RECEIVED -->" + cardsValues + ANSI_RESET;
    }

    public static String getChatTableCardsClearedMessage() {
        return ANSI_PURPLE_BACKGROUND + ANSI_GREEN + "---- TABLE CARDS CLEARED -----" +ANSI_RESET;
    }

    public static String getChatCardsOnTableMessage(String cardsValues) {
        return ANSI_PURPLE_BACKGROUND + ANSI_GREEN + "TABLE CARDS:" + cardsValues + ANSI_RESET+"\n";
    }

    public static String getChatWinningTeamMessage(String team) {
        return "WINNING TEAM IS *** " + team + " *** \n" +
                "KNEEL BEFORE THE NEW KAMES KINGS!";
    }

    public static String getChatGameStartMessage() {
        return ANSI_PURPLE_BACKGROUND + ANSI_GREEN + "----- PREPARE YOURSELVES, GAME IS STARTING -----" + ANSI_RESET+"\n";
    }

    public static String getChatNoMoreCardsOnDeckMEssage(int deckSize) {
        return ANSI_PURPLE_BACKGROUND + ANSI_GREEN + "THERE ARE " + deckSize + " CARDS LEFT ON THE DECK!" + ANSI_RESET+"\n";
    }

    public static String getChatWinnerMessage(String team, int roundsWon) {
        return team + " WINS!!! " + team + "victories: " + roundsWon;
    }

    public static String getClientYourHandIsMessage(String cardsValues) {
        return "YOUR HAND IS: " + cardsValues;
    }
}

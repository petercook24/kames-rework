package Server;

import Server.GameLogic.Game;

/**
 * Created by peter on 21-02-2017.
 */
public class ServerMain {

    public static void main(String[] args) {

        Game game = new Game();
        game.init();
//        game.startNewGame();
    }
}

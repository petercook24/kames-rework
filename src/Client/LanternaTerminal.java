package Client;

/**
 * Created by codecadet on 24/02/17.
 */

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by codecadet on 24/02/17.
 */
public class LanternaTerminal {

    private static Screen screen;

    public void createLanternaWindow() {


        /**
         * Start the screen an set its initial properties
         */
        screen = TerminalFacade.createScreen();
        screen.getTerminal().getTerminalSize().setColumns(40);
        screen.getTerminal().getTerminalSize().setRows(20);
        screen.startScreen();


        /**
         * Write first header
         */
        screen.putString(0, 0, "Cards on the table", Terminal.Color.BLACK, Terminal.Color.WHITE, ScreenCharacterStyle.Underline);


        /**
         * Draw a line to slip the screen
         */
        for (int i = 0; i < screen.getTerminal().getTerminalSize().getColumns(); i++) {
            screen.putString(i, 9, "*", Terminal.Color.GREEN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
        }

        /**
         * Draw second header
         */
        screen.putString(0, 11, "Cards in your hand", Terminal.Color.BLACK, Terminal.Color.WHITE, ScreenCharacterStyle.Underline);

        /**
         * Refresh screen. After any change we need to refresh the screen to render new changes
         */
        screen.refresh();
    }

    public void clearLineTable() {

        for (int i = 15; i < 27; i++) {
            screen.putString(i, 4, " ", Terminal.Color.WHITE, Terminal.Color.BLACK);
        }
    }
    private void clearLinePlayer() {
        for (int i = 15; i < 27; i++) {
            screen.putString(i, 15, " ", Terminal.Color.WHITE, Terminal.Color.BLACK);
        }
    }

    public void drawTable(String[] cards) {

        clearLineTable();
        screen.putString(15, 4, cards[0], Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.putString(18, 4, cards[1], Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.putString(21, 4, cards[2], Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.putString(24, 4, cards[3], Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.refresh();

    }

    public void drawPlayerHand(String[] cards) {
        clearLinePlayer();
        screen.putString(15, 15, cards[0], Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.putString(18, 15, cards[1], Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.putString(21, 15, cards[2], Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.putString(24, 15, cards[3], Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.refresh();
    }




    public void clearScreen() {
        screen.clear();
    }

}

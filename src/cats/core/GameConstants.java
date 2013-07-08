package cats.core;

/**
 * Multiplayer TicTacToe
 * Josh
 * 06/07/13
 * 9:04 PM
 */
public interface GameConstants {

    public static final String HOST = "localhost";
    public static final int PORT = 8974;

    public static final byte X = 'X';
    public static final byte O = 'O';

    public static final byte MESSAGE = -1;

    public static final byte WIN = 0;
    public static final byte LOSE = 1;
    public static final byte TIE = 2;

    public static final byte MATCH = 3;
    public static final byte DISCONNECT = 4;

    public static final byte ASSIGN = 5;
    public static final byte MOVE = 6;

    public static final byte DO_TURN = 7;
}

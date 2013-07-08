package cats.core.connection.event;

/**
 * Multiplayer TicTacToe
 * Josh
 * 06/07/13
 * 8:34 PM
 */
public interface ConnectionListener {

    public void onConnectionOpened(final ConnectionEvent e);

    public void onConnectionClosed(final ConnectionEvent e);
}

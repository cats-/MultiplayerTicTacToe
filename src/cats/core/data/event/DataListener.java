package cats.core.data.event;

/**
 * Multiplayer TicTacToe
 * Josh
 * 06/07/13
 * 8:35 PM
 */
public interface DataListener {

    public void onDataReceived(final DataEvent e);

    public void onDataSent(final DataEvent e);
}

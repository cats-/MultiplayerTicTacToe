package cats.core.connection.event;

import cats.core.connection.Connection;

/**
 * Multiplayer TicTacToe
 * Josh
 * 06/07/13
 * 8:34 PM
 */
public class ConnectionEvent {

    private final Connection connection;
    private final long time;

    public ConnectionEvent(final Connection connection){
        this.connection = connection;

        time = System.currentTimeMillis();
    }

    public Connection connection(){
        return connection;
    }

    public long time(){
        return time;
    }
}

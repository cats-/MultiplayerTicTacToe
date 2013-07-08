package cats.core.data.event;

import cats.core.connection.Connection;
import cats.core.data.Data;

/**
 * Multiplayer TicTacToe
 * Josh
 * 06/07/13
 * 8:36 PM
 */
public class DataEvent {

    private final Connection connection;
    private final Data data;

    private final long time;

    public DataEvent(final Connection connection, final Data data){
        this.connection = connection;
        this.data = data;

        time = System.currentTimeMillis();
    }

    public Connection connection(){
        return connection;
    }

    public Data data(){
        return data;
    }

    public long time(){
        return time;
    }
}

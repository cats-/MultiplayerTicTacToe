package cats.core.data.message;

import cats.core.GameConstants;
import java.io.Serializable;

/**
 * Multiplayer TicTacToe
 * Josh
 * 06/07/13
 * 8:31 PM
 */
public class ServerMessage extends Message implements Serializable, GameConstants {

    private final byte id;
    private final byte data;

    public ServerMessage(final String msg, final byte id, final byte data){
        super("Server: " + msg);
        this.id = id;
        this.data = data;
    }

    public ServerMessage(final String msg, final byte id){
        this(msg, id, (byte)0);
    }

    public ServerMessage(final String msg){
        this(msg, MESSAGE);
    }

    public byte data(){
        return data;
    }

    public byte id(){
        return id;
    }
}

package cats.core.data.message;

import cats.core.data.Data;
import java.io.Serializable;

/**
 * Multiplayer TicTacToe
 * Josh
 * 06/07/13
 * 8:28 PM
 */
public class Message extends Data implements Serializable{

    public Message(final String msg){
        super(msg.getBytes());
    }

    public String message(){
        return new String(bytes);
    }

    public String toString(){
        return String.format("%s[%s]", getClass().getSimpleName(), message());
    }
}

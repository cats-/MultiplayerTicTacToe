package cats.core.data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Multiplayer TicTacToe
 * Josh
 * 06/07/13
 * 8:26 PM
 */
public class Data implements Serializable{

    protected final byte[] bytes;

    public Data(final byte... bytes){
        this.bytes = bytes;
    }

    public byte[] bytes(){
        return bytes;
    }

    public String toString(){
        return String.format("%s%s", getClass().getSimpleName(), Arrays.toString(bytes));
    }
}

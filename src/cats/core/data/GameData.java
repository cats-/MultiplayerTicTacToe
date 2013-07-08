package cats.core.data;

import java.io.Serializable;

/**
 * Multiplayer TicTacToe
 * Josh
 * 06/07/13
 * 8:31 PM
 */
public class GameData extends Data implements Serializable{

    public GameData(final byte row, final byte column, final byte id, final byte data){
        super(row, column, id, data);
    }

    public GameData(final byte id){
        this((byte)0, (byte)0, id, (byte)0);
    }

    public byte id(){
        return bytes[2];
    }

    public byte data(){
        return bytes[3];
    }

    public byte row(){
        return bytes[0];
    }

    public byte column(){
        return bytes[1];
    }
}

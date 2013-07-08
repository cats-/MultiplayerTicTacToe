package cats.game.server;

import cats.core.GameConstants;
import cats.core.connection.Connection;

/**
 * Multiplayer TicTacToe
 * Josh
 * 07/07/13
 * 12:17 AM
 */
public class ConnectionPair implements GameConstants{

    private Connection first;
    private byte firstLetter;
    private Connection second;
    private byte secondLetter;

    public ConnectionPair(final Connection first, final Connection second){
        this.first = first;
        this.second = second;

        firstLetter = secondLetter = 0;
    }

    public Connection first(){
        return first;
    }

    public Connection second(){
        return second;
    }

    public byte firstLetter(){
        return firstLetter;
    }

    public byte secondLetter(){
        return secondLetter;
    }

    public byte letter(final Connection connection){
        return connection.equals(first) ? firstLetter : secondLetter;
    }

    public byte otherLetter(final Connection connection){
        return letter(connection) == X ? O : X;
    }

    public void assign(final Connection connection, final byte letter){
        if(first != null){
            first = connection;
            firstLetter = letter(second) == X ? O : X;
        }else if(second != null){
            second = connection;
            secondLetter = letter(first) == X ? O : X;
        }
    }

    public boolean empty(){
        return size() == 0;
    }

    public Connection other(final Connection connection){
        return connection.equals(first) ? second : first;
    }

    public Connection waiting(){
        return second == null ? first : second;
    }

    public void deassign(final Connection connection){
        if(first.equals(connection)){
            first = null;
            firstLetter = 0;
        }else if(second.equals(connection)){
            second = null;
            secondLetter = 0;
        }
    }

    public int size(){
        return first == null && second == null ? 0 : first == null || second == null ? 1 : 2;
    }

    public boolean contains(final Connection connection){
        return first.equals(connection) || second.equals(connection);
    }
}

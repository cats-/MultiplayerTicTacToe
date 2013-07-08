package cats.core.connection;

import cats.core.data.Data;
import cats.core.data.event.DataEvent;
import cats.core.data.event.DataListener;
import cats.core.connection.event.ConnectionEvent;
import cats.core.connection.event.ConnectionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Multiplayer TicTacToe
 * Josh
 * 06/07/13
 * 8:16 PM
 */
public class Connection extends Thread implements Runnable{

    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    private final List<ConnectionListener> cListeners;
    private final List<DataListener> dListeners;

    public Connection(final Socket socket) throws IOException {
        this.socket = socket;

        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();

        input = new ObjectInputStream(socket.getInputStream());

        cListeners = new LinkedList<>();

        dListeners = new LinkedList<>();

        setPriority(MAX_PRIORITY);
        start();
    }

    public void close() throws IOException{
        input.close();
        output.close();
        socket.close();
    }

    public void run(){
        fireConnectionOpenedListeners(new ConnectionEvent(this));
        while(socket.isConnected()){
            try{
                final Data data = (Data)input.readObject();
                fireDataRecievedListeners(new DataEvent(this, data));
            }catch(Exception ex){
                ex.printStackTrace();
                break;
            }
        }
        fireConnectionClosedListeners(new ConnectionEvent(this));
    }

    protected void fireConnectionOpenedListeners(final ConnectionEvent e){
        cListeners.stream().forEach(c -> c.onConnectionOpened(e));
    }

    protected void fireConnectionClosedListeners(final ConnectionEvent e){
        cListeners.stream().forEach(c -> c.onConnectionClosed(e));
    }

    public boolean addConnectionListener(final ConnectionListener l){
        return cListeners.add(l);
    }

    public boolean removeConnectionListener(final ConnectionListener l){
        return cListeners.remove(l);
    }

    public ConnectionListener[] connectionListeners(){
        return cListeners.toArray(new ConnectionListener[cListeners.size()]);
    }

    protected void fireDataRecievedListeners(final DataEvent e){
        dListeners.stream().forEach(d -> d.onDataReceived(e));
    }

    protected void fireDataSentListeners(final DataEvent e){
        dListeners.stream().forEach(d -> d.onDataSent(e));
    }

    public boolean addDataListener(final DataListener l){
        return dListeners.add(l);
    }

    public boolean removeDataListener(final DataListener l){
        return dListeners.remove(l);
    }

    public DataListener[] dataListeners(){
        return dListeners.toArray(new DataListener[dListeners.size()]);
    }

    public void send(final Data data) throws IOException{
        output.writeObject(data);
        output.flush();
        fireDataSentListeners(new DataEvent(this, data));
    }

    public Socket socket(){
        return socket;
    }

    public ObjectInputStream input(){
        return input;
    }

    public ObjectOutputStream output(){
        return output;
    }

    public String toString(){
        return socket.toString();
    }
}

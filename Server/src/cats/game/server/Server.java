package cats.game.server;

import cats.core.GameConstants;
import cats.core.connection.Connection;
import cats.core.connection.event.ConnectionEvent;
import cats.core.connection.event.ConnectionListener;
import cats.core.data.Data;
import cats.core.data.GameData;
import cats.core.data.event.DataEvent;
import cats.core.data.event.DataListener;
import cats.core.data.message.ServerMessage;
import java.awt.BorderLayout;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Multiplayer TicTacToe
 * Josh
 * 06/07/13
 * 9:05 PM
 */
public class Server extends JFrame implements ConnectionListener, DataListener, Runnable, GameConstants {

    private final JTextArea area;
    private final JScrollPane scroll;

    private final List<ConnectionPair> pairs;

    private ServerSocket server;

    public Server(){
        super("Server");
        setLayout(new BorderLayout());

        pairs = new LinkedList<>();

        area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        scroll = new JScrollPane(area);

        add(scroll, BorderLayout.CENTER);
    }

    public void start(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);
        final Thread t = new Thread(this);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    private ConnectionPair needPair(){
        return pairs.stream().filter(p -> p.size() == 1).findFirst().orElse(null);
    }

    public void run(){
        try{
            log("Initializing Server...");
            server = new ServerSocket(PORT);
            log("Server running: " + server);
            while(true){
                try{
                    log("Looking for connections...");
                    final Socket socket = server.accept();
                    log("Found connection: " + socket);
                    final Connection connection = new Connection(socket);
                    connection.addConnectionListener(this);
                    connection.addDataListener(this);
                    connection.send(new ServerMessage("Searching for opponent..."));
                    final ConnectionPair pair = needPair();
                    if(pair == null){
                        final ConnectionPair p = new ConnectionPair();
                        p.assign(connection, X);
                        pairs.add(p);
                        connection.send(new ServerMessage(String.format("You have been assigned the letter %s", (char)X), ASSIGN, X));
                    }else{
                        final Connection waiting = pair.waiting();
                        final byte letter = pair.otherLetter(waiting);
                        pair.assign(connection, letter);
                        connection.send(new ServerMessage(String.format("You have been assigned the letter %s", (char) letter), ASSIGN, letter));
                        connection.send(new ServerMessage("Found oppoonent: " + waiting, MATCH));
                        waiting.send(new ServerMessage("Found opponent: " + connection, MATCH));
                        waiting.send(new ServerMessage("You will get to go first", DO_TURN));
                    }
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void log(final String msg){
        SwingUtilities.invokeLater(
                () -> {
                    area.append(msg + "\n");
                    area.repaint();
                }
        );
    }

    public void onConnectionClosed(final ConnectionEvent e){
        final Connection c = e.connection();
        log("Connection closed: " + c);
        final ConnectionPair pair = pairs.stream().filter(p -> p.contains(c)).findFirst().orElse(null);
        if(pair == null)
            return;
        pair.deassign(c);
        if(pair.empty()){
            pairs.remove(pair);
            return;
        }
        final Connection waiting = pair.waiting();
        try{
            waiting.send(new ServerMessage(c + " has disconnected from you", DISCONNECT));
            waiting.send(new ServerMessage("Searching for opponent..."));
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void onConnectionOpened(final ConnectionEvent e){}

    public void onDataReceived(final DataEvent e){
        final Connection c = e.connection();
        final Data d = e.data();
        log(String.format("%s >> %s", c, d));
        final ConnectionPair pair = pairs.stream().filter(p -> p.contains(c)).findFirst().orElse(null);
        if(pair == null)
            return;
        final Connection other = pair.other(c);
        try{
            other.send(d);
            if(d instanceof GameData){
                final GameData gd = (GameData)d;
                switch(gd.id()){
                    case WIN:
                        other.send(new ServerMessage("Other player has won", LOSE));
                        break;
                    case MOVE:
                        other.send(new ServerMessage("It is your turn", DO_TURN));
                        break;
                    case TIE:
                        other.send(new ServerMessage("Tie!", TIE));
                        break;
                    default:
                        other.send(gd);
                        break;
                }
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void onDataSent(final DataEvent e){
        log("Data sent: " + e.data());
    }

    public static void main(String args[]){
        final Server server = new Server();
        server.start();
    }
}

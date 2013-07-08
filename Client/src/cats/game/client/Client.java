package cats.game.client;

import cats.core.GameConstants;
import cats.core.connection.Connection;
import cats.core.data.Data;
import cats.core.data.GameData;
import cats.core.data.event.DataEvent;
import cats.core.data.event.DataListener;
import cats.core.data.message.Message;
import cats.core.data.message.ServerMessage;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Multiplayer TicTacToe
 * Josh
 * 07/07/13
 * 12:50 PM
 */
public class Client extends JFrame implements DataListener, GameConstants, ActionListener {

    public static final int ROWS = 3;
    public static final int COLUMNS = 3;

    private Connection connection;

    private final JButton[][] buttons;
    private final JPanel buttonPanel;

    private byte letter;
    private boolean enabled[][];

    private final JTextArea area;
    private final JScrollPane scroll;
    private final JTextField chatBox;
    private final JPanel chatPanel;

    public Client(){
        super("Tic-Tac-Toe");
        setLayout(new BorderLayout());

        enabled = new boolean[ROWS][COLUMNS];

        buttonPanel = new JPanel(new GridLayout(ROWS, COLUMNS, 2, 2));

        buttons = new JButton[ROWS][COLUMNS];
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLUMNS; c++){
                buttons[r][c] = new JButton();
                buttons[r][c].addActionListener(this);
                buttonPanel.add(buttons[r][c]);
            }
        }

        area = new JTextArea(20, 12);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        scroll = new JScrollPane(area);

        chatBox = new JTextField();
        chatBox.setEditable(false);
        chatBox.addActionListener(this);

        chatPanel = new JPanel(new BorderLayout());
        chatPanel.add(scroll, BorderLayout.CENTER);
        chatPanel.add(chatBox, BorderLayout.SOUTH);

        add(buttonPanel, BorderLayout.CENTER);
        add(chatPanel, BorderLayout.SOUTH);

        reset();
    }

    private void resetButtonText(final byte r, final byte c){
        SwingUtilities.invokeLater(
                () -> {
                    buttons[r][c].setText("");
                    buttons[r][c].repaint();
                }
        );
    }

    private void reset(){
        enabled(false);
        for(byte r = 0; r < ROWS; r++){
            for(byte c = 0; c < COLUMNS; c++){
                enabled[r][c] = false;
                resetButtonText(r, c);
            }
        }
    }

    private void enabled(final boolean enabled){
        SwingUtilities.invokeLater(
                () -> {
                    for(byte r = 0; r < ROWS; r++)
                        for(byte c = 0; c < COLUMNS; c++)
                            enabled(r, c, enabled);
                }
        );
    }

    private void turn(){
        for(byte r = 0; r < ROWS; r++)
            for(byte c = 0; c < COLUMNS; c++)
                enabled(r, c, enabled[r][c]);
    }

    private void enabled(final byte r, final byte c, final boolean enabled){
        SwingUtilities.invokeLater(
                () -> {
                    buttons[r][c].setEnabled(enabled);
                    buttons[r][c].repaint();
                }
        );
    }

    private void set(final byte r, final byte c, final byte letter){
        SwingUtilities.invokeLater(
                () -> {
                    buttons[r][c].setEnabled(false);
                    enabled[r][c] = false;
                    buttons[r][c].setText(Character.toString((char)letter));
                    buttons[r][c].repaint();
                }
        );
    }

    private void set(final GameData data){
        set(data.row(), data.column(), data.data());
    }

    public void actionPerformed(final ActionEvent e){
        final Object source = e.getSource();
        if(source.equals(chatBox)){
            final String text = chatBox.getText().trim();
            if(text.isEmpty())
                return;
            try{
                connection.send(new Message("Opponent: " + text));
                append("You: " + text);
            }catch(IOException ex){
                ex.printStackTrace();
            }finally{
                SwingUtilities.invokeLater(
                        () -> {
                            chatBox.setText("");
                            chatBox.repaint();
                        }
                );
            }
        }else{
            outer:
            for(byte r = 0; r < ROWS; r++){
                for(byte c = 0; c < COLUMNS; c++){
                    if(!buttons[r][c].equals(source))
                        continue;
                    try{
                        connection.send(new GameData(r, c, MOVE, letter));
                        set(r, c, letter);
                        enabled(false);
                        if(win())
                            connection.send(new GameData(r, c, WIN, letter));
                    }catch(IOException ex){
                        ex.printStackTrace();
                    }
                    break outer;
                }
            }
        }
    }

    public void start(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);
        try{
            append("Initializing connection...");
            connection = new Connection(new Socket(HOST, PORT));
            connection.addDataListener(this);
            append("Connection established");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void append(final String msg){
        SwingUtilities.invokeLater(
                () -> {
                    area.append(msg + "\n");
                    area.repaint();
                }
        );
    }

    public void onDataSent(final DataEvent e){

    }

    private char charAt(final byte r, final byte c){
        return buttons[r][c].getText().charAt(0);
    }

    private boolean checkVertical(final char letter){
        for(byte r = 0; r < ROWS; r++){
            final char c1 = charAt(r, (byte)0);
            final char c2 = charAt(r, (byte)1);
            final char c3 = charAt(r, (byte)2);
            if(letter == c1 && c1 == c2 && c2 == c3)
                return true;
        }
        return false;
    }

    private boolean checkHorizontal(final char letter){
        for(byte c = 0; c < COLUMNS; c++){
            final char c1 = charAt((byte)0, c);
            final char c2 = charAt((byte)1, c);
            final char c3 = charAt((byte)2, c);
            if(letter == c1 && c1 == c2 && c2 == c3)
                return true;
        }
        return false;
    }

    private boolean checkDiagonal(final char letter){
        final char mid = charAt((byte)1, (byte)1);
        final boolean first = charAt((byte)0, (byte)0) == mid && mid == charAt((byte)2, (byte)2) && mid == letter;
        final boolean second = charAt((byte)0, (byte)2) == mid && mid == charAt((byte)2, (byte)0) && mid == letter;
        return first || second;
    }

    private boolean win(){
        final char c = (char)letter;
        return checkVertical(c) || checkHorizontal(c) || checkDiagonal(c);
    }

    public void onDataReceived(final DataEvent e){
        final Data data = e.data();
        if(data instanceof GameData){
            final GameData gd = (GameData)data;
            set(gd);
            if(gd.id() == MOVE)
                turn();
        }else if(data instanceof Message){
            final Message msg = (Message)data;
            append(msg.message());
            if(data instanceof ServerMessage){
                final ServerMessage smsg = (ServerMessage)msg;
                final int id = smsg.id();
                if(id == ASSIGN)
                    letter = smsg.data();
                else if(id == WIN || id == LOSE || id == TIE || id == DISCONNECT)
                    reset();
                else if(id == DO_TURN)
                    turn();
            }
        }
    }

    public static void main(String args[]){
        final Client client = new Client();
        client.start();
    }
}

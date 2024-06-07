import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Main extends JFrame {

    static List<Node> nodeList = new ArrayList<>();

    private int nodes;
    private boolean logTokenDetails;
    
    public Main() {
        init();
    }

    private void init() {



        int index = (int) Math.random() * nodeList.size();
        Node randomStarterNode = nodeList.get(index);
        Token token = new Token().setNode(randomStarterNode);
        token.setLocation(randomStarterNode.getLocation().x, randomStarterNode.getLocation().y + randomStarterNode.getHeight());
        randomStarterNode.token = token;
        randomStarterNode.isPHold = true;

        // Start threads


        // Log token details
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(token);
            }
        });

        timer.setRepeats(true);
        if (logTokenDetails) {
            timer.start();
        }
    }

    public static void main(String[] args) {
        // Log the output to a file
        try {
            PrintStream fileStream = new PrintStream("outputLog.txt");
            System.setOut(fileStream);
        }
        catch (FileNotFoundException err) {
            err.printStackTrace();
        }
    }
}

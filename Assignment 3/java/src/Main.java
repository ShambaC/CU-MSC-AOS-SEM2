import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends JFrame {

    static Ring nodeList = Ring.getInstance();

    private int nodes;
    private boolean logTokenDetails;
    
    public Main() {
        setTitle("Token Ring");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
    }

    private void init() {
        JPanel container = new JPanel(new FlowLayout());

        nodes = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of nodes: "));

        int res = JOptionPane.showConfirmDialog(this, "Do you want to log each node details every second ?", "Detail Log", JOptionPane.YES_NO_OPTION);
        logTokenDetails = res == 0 ? true : false;

        for (int i = 0; i < nodes; i++) {
            Node node = new Node(Integer.toString(i));
            node.setSize(50, 50);
            node.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
            nodeList.add(node);
            container.add(node);
        }


        int index = (int) Math.random() * nodeList.size();
        Node randomStarterNode = nodeList.get(index);
        Token token = new Token().setNodeId(randomStarterNode.id);
        System.out.println("Token initialized at Node" + randomStarterNode.id);
        token.setLocation(randomStarterNode.getLocation().x, randomStarterNode.getLocation().y + randomStarterNode.getHeight());
        randomStarterNode.token = token;
        randomStarterNode.isPHold = true;

        add(container);

        // Start threads
        for (Node node : nodeList) {
            Thread t = new Thread(node);
            t.start();
        }


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

        Main m = new Main();
        m.setVisible(true);

        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m.repaint();
            }
        });

        timer.setRepeats(true);
        timer.start();
    }
}

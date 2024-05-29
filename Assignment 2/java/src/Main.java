import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Main extends JFrame {

    List<Node> nodeList = new ArrayList<>();

    int nodes;

    public Main() {
        setLayout(new BorderLayout());
        setTitle("Ricard Agrawala Algorithm Implementation");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
    }

    private void init() {
        JPanel container = new JPanel();

        nodes = Integer.parseInt(JOptionPane.showInputDialog("Enter the no. of nodes: "));

        for (int i = 0; i < nodes; i++) {
            nodeList.add(new Node(i, nodes));
            // nodeList.get(i).setFont(new Font("Bookman Old Style", 1, 20));
            container.add(nodeList.get(i));
        }

        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            for (int j = 0; j < nodeList.size(); j++) {
                if (i != j) {
                    node.nodeList.add(nodeList.get(j));
                }
            }
        }

        add(container);

        for (Node node : nodeList) {
            Thread t = new Thread(node);
            t.start();
        }

        System.out.println("All threads started");

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("-------------CURRENT STATUS------------");
                for (Node node : nodeList) {
                    System.out.println(node);
                }
            }
        });

        timer.setRepeats(true);
        timer.start();
    }

    public static void main(String[] args) {
        try {
            PrintStream fileStream = new PrintStream("outputLog.txt");
            System.setOut(fileStream);
        }
        catch (FileNotFoundException err) {
            err.printStackTrace();
        }

        Main m = new Main();
        m.setVisible(true);
    }
}
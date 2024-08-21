import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Main {

    private static Timer timer;

    private static void pauseApp() {
        timer.stop();
        JOptionPane.showMessageDialog(null, "Deadlock Detected", "INformation", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    public static void main(String[] args) {
        int size;

        List<Node> nodeList = new ArrayList<>();

        // Log the output to a file
        try {
            PrintStream fileStream = new PrintStream("outputLog.txt");
            System.setOut(fileStream);
        }
        catch (FileNotFoundException err) {
            err.printStackTrace();
        }

        size = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of nodes: "));

        for (int i = 0; i < size; i++) {
            Node node = new Node(Integer.toString(i), i);
            System.out.println("\nInitialized Node " + i + " : " + i + "/" + i);
            nodeList.add(node);
        }

        for (Node node : nodeList) {
            node.nodeList = nodeList;

            Thread t = new Thread(node);

            t.start();
        }

        // Pause program on deadlock
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = false;

                for (int i = 0; i < nodeList.size(); i++) {
                    Node node = nodeList.get(i);

                    node.detect();

                    if (node.isInDeadlock) {
                        flag = true;
                        break;
                    }
                }

                if (flag) {

                    for (int i = 0; i < nodeList.size(); i++) {
                        Node node = nodeList.get(i);
                        node.running = false;
                        System.out.println("\nStopping Node " + node.id);
                    }

                    pauseApp();
                }
            }
        });

        timer.setRepeats(true);
        timer.start();
    }
}

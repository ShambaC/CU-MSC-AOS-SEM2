import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;


/**
 * Main runner class
 */
public class Main {

    public static void main(String[] args) {
        // Log the output to a file
        try {
            PrintStream fileStream = new PrintStream("outputLog.txt");
            System.setOut(fileStream);
        }
        catch (FileNotFoundException err) {
            err.printStackTrace();
        }

        Ring nodeList = Ring.getInstance();

        int nodes;
        boolean logTokenDetails;
        Token token;

        // Take input for number of nodes
        nodes = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of nodes: "));

        int res = JOptionPane.showConfirmDialog(null, "Do you want to log each node details every second ?", "Detail Log", JOptionPane.YES_NO_OPTION);
        logTokenDetails = res == 0 ? true : false;

        for (int i = 0; i < nodes; i++) {
            Node node = new Node(Integer.toString(i));
            nodeList.add(node);
        }

        // Choose a random started node to hold the token
        Random random = new Random(System.currentTimeMillis());
        int index = random.nextInt(0, nodes);
        Node randomStarterNode = nodeList.get(index);
        token = new Token().setNodeId(randomStarterNode.id);

        System.out.println("Token initialized at Node" + randomStarterNode.id);
        System.out.println();

        randomStarterNode.token = token;
        randomStarterNode.isPHold = true;

        JFileChooser fc = new JFileChooser("./");
        int resChoice = fc.showOpenDialog(null);
        if (resChoice == JFileChooser.APPROVE_OPTION) {

            try {
                String content = new String(Files.readAllBytes(fc.getSelectedFile().toPath()));
                String[] lines = content.split("\\n");

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

                for (String nodeId : lines) {
                    nodeId = nodeId.trim();
                    Thread.sleep(2000);
                    for (int i = 0; i < nodeList.size(); i++) {
                        Node node = nodeList.get(i);

                        if (node.id.equalsIgnoreCase(nodeId)) {
                            node.doAction();
                            break;
                        }
                    }
                }

                System.exit(0);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

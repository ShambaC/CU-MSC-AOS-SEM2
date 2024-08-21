import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Main {
    
    static List<Node> nodeList = new ArrayList<>();

    public static void main(String[] args) {
        try {
            PrintStream fileStream = new PrintStream("outputLog.txt");
            System.setOut(fileStream);
        }
        catch (FileNotFoundException err) {
            err.printStackTrace();
        }

        JFileChooser fc = new JFileChooser("./");
        int res = fc.showOpenDialog(null);

        /**
         * File content should be nodes to activate in each rows, first line should be number of nodes
         */
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                String content = new String(Files.readAllBytes(fc.getSelectedFile().toPath())).trim();
                // String[] lines = content.split("\\n");
                String[] lines0 = content.split("\\n");
                char[] lines = new char[lines0.length];
                int k = 0;
                for (String s : lines0) {
                    lines[k++] = s.charAt(0);
                }


                // int nodes = Integer.parseInt(lines[0]);
                int nodes = lines[0] - '0';

                int resBool = JOptionPane.showConfirmDialog(null, "Do you want to log each node details every second ?", "Detail Log", JOptionPane.YES_NO_OPTION);
                boolean showDetailedLog = resBool == 0 ? true : false;

                for (int i = 0; i < nodes; i++) {
                    Node node = new Node(i, nodes);
                    nodeList.add(node);
                }

                for (int i = 0; i < nodeList.size(); i++) {
                    Node node = nodeList.get(i);
                    for (int j = 0; j < nodeList.size(); j++) {
                        if (i != j) {
                            node.nodeList.add(nodeList.get(j));
                        }
                    }
                }

                for (Node node : nodeList) {
                    Thread t = new Thread(node);
                    t.start();
                }

                System.out.println("All threads started");

                // Logging
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
                if (showDetailedLog)    timer.start();

                // Actions will be performed every 2 seconds
                for (int i = 1; i < lines.length; i++) {
                    // int nodeNum = Integer.parseInt(lines[i]);
                    int nodeNum = lines[i] - '0';
                    Node node = nodeList.get(nodeNum);
                    Thread.sleep(2000);
                    node.doAction();
                }

                System.exit(0);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            System.exit(-1);
        }
    }
}

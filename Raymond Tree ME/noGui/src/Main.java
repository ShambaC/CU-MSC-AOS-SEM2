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
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * Main class
 */
public class Main {
    // A list of all the nodes in the system

    public static void main(String[] args) {
        // Log all the output information in a file
        try {
            PrintStream fileStream = new PrintStream("outputLog.txt");
            System.setOut(fileStream);
        }
        catch (FileNotFoundException err) {
            err.printStackTrace();
        }

        List<Node> nodeList = new ArrayList<>();

        // File selection for the file that contains the input for the tree
        JFileChooser fc = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fc.setFileFilter(filter);
        int res = fc.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                String content = new String(Files.readAllBytes(fc.getSelectedFile().toPath()));

                int result = JOptionPane.showConfirmDialog(null, "Do you want to log each node details every second ?", "Detail Log", JOptionPane.YES_NO_OPTION);
                boolean showDetailedLog = result == 0 ? true : false;

                String[] lines = content.split("\\r\\n");

                // First line of the file contains the number of nodes
                int size = Integer.parseInt(lines[0].strip());

                // Create nodes dynamically with every edge
                for (int i = 1; i < lines.length; i++) {
                    if (lines[i].isBlank() || lines[i].startsWith("#"))     continue;

                    Node node1 = new Node(lines[i].split(" ")[0], size);
                    Node node2 = new Node(lines[i].split(" ")[1], size);

                    // Check if the nodes already exist
                    if (!nodeList.contains(node1)) {
                        nodeList.add(node1);
                    }
                    else {
                        node1 = nodeList.get(nodeList.indexOf(node1));
                    }
                    if (!nodeList.contains(node2)) {
                        nodeList.add(node2);
                    }
                    else {
                        node2 = nodeList.get(nodeList.indexOf(node2));
                    }

                    // Set parent
                    node1.parent = node2;
                }

                // Set the root node as priviledged
                for (int i = 0; i < nodeList.size(); i++) {
                    Node node = nodeList.get(i);
                    if (node.parent == null) {
                        node.isPrivileged = true;
                        break;
                    }
                }

                // Get the file with execution sequence
                JFileChooser fc2 = new JFileChooser(fc.getSelectedFile());
                int res2 = fc2.showOpenDialog(null);
                if (res2 == JFileChooser.APPROVE_OPTION) {
                    String sequenceContent = new String(Files.readAllBytes(fc2.getSelectedFile().toPath()));
                    String[] sequenceLines = sequenceContent.split("\\n");

                    // Start all the node threads
                    for (int i = 0; i < nodeList.size(); i++) {
                        Node node = nodeList.get(i);
                        Thread t = new Thread(node);
                        t.start();
                    }

                    // Logging
                    Timer timer = new Timer(1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("\n-------------CURRENT STATUS------------");
                            for (Node node : nodeList) {
                                System.out.println(node);
                            }
                        }
                    });

                    timer.setRepeats(true);
                    if (showDetailedLog)    timer.start();

                    for (String nodeId : sequenceLines) {
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
                }
            }
            catch (IOException | InterruptedException err) {
                err.printStackTrace();
            }
        }
    }
}
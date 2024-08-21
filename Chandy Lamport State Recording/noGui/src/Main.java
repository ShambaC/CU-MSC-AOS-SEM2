import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
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
 * Main class for GUI and running
 */
public class Main {

    private List<Node> graph = new ArrayList<>();

    public Main() {
        init();
    }

    private void init() {

        JOptionPane.showMessageDialog(null, "The input file must be in the following format:\nOne edge in each line in the format of <from Node><space><to node>\nBlank lines and lines starting with '#' are ignored", "Instructions", JOptionPane.INFORMATION_MESSAGE);

        // Take input for file
        JFileChooser fc = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fc.setFileFilter(filter);
        int res = fc.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                String content = new String(Files.readAllBytes(fc.getSelectedFile().toPath()));
                String[] lines = content.split("\\r\\n");

                for (int i = 0; i < lines.length; i++) {
                    if (lines[i].isBlank() || lines[i].startsWith("#")) continue;

                    String[] nodeIDs = lines[i].split(" ");

                    Node nodeA = new Node(nodeIDs[0]);
                    Node nodeB = new Node(nodeIDs[1]);

                    if (graph.contains(nodeA)) {
                        nodeA = graph.get(graph.indexOf(nodeA));
                    }
                    else {
                        graph.add(nodeA);
                    }

                    if (graph.contains(nodeB)) {
                        nodeB = graph.get(graph.indexOf(nodeB));
                    }
                    else {
                        graph.add(nodeB);
                    }

                    Channel channel = new Channel(nodeA, nodeB);
                    nodeA.outgoingChannels.add(channel);
                    nodeB.incomingChannels.add(channel);
                }

                // --------GRAPH DONE ---------------

                // Find the eligible starter node
                Node startNode = ParseGraph.findStarter(graph);
                System.out.println("Selected starter node : Node " + startNode.id);

                // Start each node
                for (int j = 0; j < graph.size(); j++) {
                    Thread t = new Thread(graph.get(j));
                    t.start();
                }

                // Start state recording from the eligible node after 3 seconds
                Timer startTimer = new Timer(3000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        startNode.startRecording();
                    }
                });
                startTimer.setRepeats(false);
                startTimer.start();

            }
            catch (IOException err) {
                err.printStackTrace();
            }
        }
        else {
            System.exit(0);
        }
    }

    /**
     * Method to see if state recording is complete
     */
    public void checkState() {
        /**
         * Flag variable
         */
        boolean isAllStateRecorded = true;

        // Iterate through each node and check if their state was recorded
        for (int i = 0; i < graph.size(); i++) {
            Node node = graph.get(i);

            if (!node.isStateRecorded) {
                isAllStateRecorded = false;
                break;
            }

            for (int j = 0; j < node.outgoingChannels.size(); j++) {
                Channel channel = node.outgoingChannels.get(j);

                // Iterate through all messages in a chanel to see if there are any marker messages
                for (Message m : channel) {
                    if (m.type == MessageType.Marker) {
                        isAllStateRecorded = false;
                        break;
                    }
                }
            }
        }

        // If state recording is done, store all the recorded information in a file and exit the program
        if (isAllStateRecorded) {
            StringBuffer nodeData = new StringBuffer();
            StringBuffer channelData = new StringBuffer();

            nodeData.append("----NODES----");
            channelData.append("\n\n----CHANNELS----");

            for (int i = 0; i < graph.size(); i++) {
                Node node = graph.get(i);

                nodeData.append("\n\nNode: ").append(node.id);

                nodeData.append("\nIncoming messages: [");
                for (Message m : node.recordedMessages) {
                    nodeData.append(m.messageID).append(", ");
                }
                nodeData.append("]");

                nodeData.append("\nOutgoing messages: [");
                for (Message m : node.outgoingMessages) {
                    nodeData.append(m.messageID).append(", ");
                }
                nodeData.append("]");

                for (Channel ch : node.outgoingChannels) {
                    channelData.append("\n\nChannel from Node ").append(ch.nodeA.id).append(" to Node ").append(ch.nodeB.id).append(": ");
                    channelData.append("\nRecorded State: [");

                    for (Message m : ch.state) {
                        channelData.append(m.messageID).append(", ");
                    }
                    channelData.append("]");
                }
            }

            File stateFile = new File("savedState.txt");
            StringBuffer savedState = new StringBuffer();
            savedState.append(nodeData.toString());
            savedState.append(channelData.toString());
            try {
                Files.write(stateFile.toPath(), savedState.toString().getBytes());
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            JOptionPane.showMessageDialog(null, "All states recorded and saved in a file.", "Finish Notice", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        // Log all the output information in a file
        try {
            PrintStream fileStream = new PrintStream("outputLog.txt");
            System.setOut(fileStream);
        }
        catch (FileNotFoundException err) {
            err.printStackTrace();
        }

        Main UI = new Main();

        // Refresh the gui
        Timer refreshTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.checkState();
            }
        });

        refreshTimer.setRepeats(true);
        refreshTimer.start();
    }
}
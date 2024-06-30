import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Utility class to create a round button
 */
class RoundedBorder implements Border{

	private int radius;

    RoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius, this.radius, this.radius, this.radius);
    }

    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.drawRoundRect(x, y, width, height, radius*5, radius*5);
    }
}

/**
 * Custom container for nodes which also draws the edges and messages
 */
class Container extends JPanel {
    /**
     * The node graph containing all the nodes
     */
    private List<Node> graph = new ArrayList<>();

    public Container() {
        super();
    }

    public void setList(List<Node> graph) {
        this.graph = graph;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Edge drawing
        for (int i = 0; i < graph.size(); i++) {
            Node node = graph.get(i);

            // Set color of the node to red if its state is already recorded
            if (node.isStateRecorded) {
                node.setBackground(new Color(255, 0, 0));
                node.setForeground(new Color(255, 255, 255));
            }

            // Iterate through each out going edge
            for (int j = 0; j < node.outgoingChannels.size(); j++) {
                Node toNode = node.outgoingChannels.get(j).nodeB;

                Graphics2D g2D = (Graphics2D) g;
                g2D.setPaint(node.outgoingChannels.get(j).channelColor);
                g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Control point coords for Bezier curve
                int middleControlPointX = 0;
                int middleControlPointY = Math.abs(toNode.getY() - node.getY() / 2);

                if (node.getX() < toNode.getX()) {

                    middleControlPointX = toNode.getX() - node.getX() / 2;
                }
                else {
                    middleControlPointX = node.getX() - toNode.getX() / 2;
                }

                Path2D.Double path = new Path2D.Double();

                // Create the curved line
                path.moveTo(node.getX(), node.getY());
                path.curveTo(node.getX(), node.getY(), middleControlPointX, middleControlPointY, toNode.getX() + toNode.getWidth(), toNode.getY());
                // Set stroke to make the line thicker
                g2D.setStroke(new BasicStroke(3));
                // Draw the curve
                g2D.draw(path);

                // Add a circle at the end of the curve to indicate direction
                g.setColor(Color.RED);
                g.fillOval(toNode.getX() + toNode.getWidth(), toNode.getY(), 7, 7);
                g.setColor(Color.BLACK);
            }
        }

        // Message drawing
        for (int i = 0; i < graph.size(); i++) {
            Node node = graph.get(i);

            for (int j = 0; j < node.outgoingChannels.size(); j++) {
                Channel channel = node.outgoingChannels.get(j);

                Node nodeB = channel.nodeB;

                g.setFont(new Font("Bookman Old Style", Font.BOLD, 16));

                // Iterate through messages in a chanel
                for (Message m : channel) {
                    m.timerCounter--;

                    // Normal message are coordinated with their respective channel color
                    if (m.type == MessageType.Normal) {
                        g.setColor(channel.channelColor);
                    }
                    // Marker messages are red
                    else {
                        g.setColor(Color.RED);
                    }

                    // Square messages are normal and marker messages are circle
                    if (m.timerCounter == 2) {
                        if(m.type == MessageType.Normal) {
                            g.fillRect(node.getX() + 25, node.getY() - 25, 40, 30);
                        }
                        else {
                            g.fillOval(node.getX() + 25, node.getY() - 25, 40, 30);
                        }
                        g.setColor(Color.WHITE);
                        g.drawString(m.messageID, node.getX() + 30, node.getY() - 10);
                    }
                    else if (m.timerCounter == 1) {
                        if(m.type == MessageType.Normal) {
                            g.fillRect((node.getX() + nodeB.getX()) / 2, (node.getY() + nodeB.getY()) / 2, 40, 30);
                        }
                        else {
                            g.fillOval((node.getX() + nodeB.getX()) / 2, (node.getY() + nodeB.getY()) / 2, 40, 30);
                        }                        
                        g.setColor(Color.WHITE);
                        g.drawString(m.messageID, ((node.getX() + nodeB.getX()) / 2) + 5, ((node.getY() + nodeB.getY()) / 2) + 15);
                    }
                }

                g.setColor(Color.BLACK);
            }
        }
    }
}

/**
 * Main class for GUI and running
 */
public class Main extends JFrame {

    private List<Node> graph = new ArrayList<>();

    private Container container = new Container();

    public Main() {
        setTitle("Chandy-Lamport State Recording");
        setSize(900, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        init();
    }

    private void init() {
        // Set UI theme as the system theme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        Color buttonBgIdle = new Color(185, 235, 255);
        Color buttonFg = new Color(0, 17, 61);

        container.setLayout(null);

        JOptionPane.showMessageDialog(this, "The input file must be in the following format:\nOne edge in each line in the format of <from Node><space><to node>\nBlank lines and lines starting with '#' are ignored", "Input format", JOptionPane.INFORMATION_MESSAGE);

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

                    nodeA.setSize(50, 50);
                    nodeA.setBorder(new RoundedBorder(20));
                    nodeA.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
                    nodeA.setContentAreaFilled(false);
                    nodeA.setOpaque(true);
                    nodeA.setBackground(buttonBgIdle);
                    nodeA.setForeground(buttonFg);

                    nodeB.setSize(50, 50);
                    nodeB.setBorder(new RoundedBorder(20));
                    nodeB.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
                    nodeB.setContentAreaFilled(false);
                    nodeB.setOpaque(true);
                    nodeB.setBackground(buttonBgIdle);
                    nodeB.setForeground(buttonFg);

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

                // Display in circle
                double angleIncrement = 2 * Math.PI / graph.size();
                Dimension window = getSize();
                double radius = window.getHeight() / 2 - 150;   // Radius of the circle
                double centerX = window.getHeight() / 2 - 20;   // Center X of the circle
                double centerY = window.getWidth() / 2 - 20;    // Center y of the circle

                for (int i = 0; i < graph.size(); i++) {
                    Node node = graph.get(i);

                    double angle = i * angleIncrement;
                    int x = (int) (centerX + radius * Math.cos(angle) - 40); // 40 is half of the button width for centering
                    int y = (int) (centerY + radius * Math.sin(angle) - 25); // 25 is half of the button height for centering

                    node.setBounds(x, y, 80, 50);

                    container.add(node);
                }

                container.setList(graph);

                // Find the eligible starter node
                Node startNode = ParseGraph.findStarter(graph);
                System.out.println("Selected starter node : Node " + startNode.id);

                add(container, BorderLayout.CENTER);

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

            JOptionPane.showMessageDialog(this, "All states recorded and saved in a file.", "Finish Notice", JOptionPane.INFORMATION_MESSAGE);
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
        UI.setVisible(true);

        // Refresh the gui
        Timer refreshTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.repaint();
                UI.checkState();
            }
        });

        refreshTimer.setRepeats(true);
        refreshTimer.start();
    }
}
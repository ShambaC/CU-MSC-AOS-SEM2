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

class Container extends JPanel {
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

            for (int j = 0; j < node.outgoingChannels.size(); j++) {
                Node toNode = node.outgoingChannels.get(j).nodeB;

                Graphics2D g2D = (Graphics2D) g;
                g2D.setPaint(node.outgoingChannels.get(j).channelColor);
                g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int middleControlPointX = 0;
                int middleControlPointY = Math.abs(toNode.getY() - node.getY() / 2);

                if (node.getX() < toNode.getX()) {

                    middleControlPointX = toNode.getX() - node.getX() / 2;
                }
                else {
                    middleControlPointX = node.getX() - toNode.getX() / 2;
                }

                Path2D.Double path = new Path2D.Double();

                path.moveTo(node.getX(), node.getY());
                path.curveTo(node.getX(), node.getY(), middleControlPointX, middleControlPointY, toNode.getX() + toNode.getWidth(), toNode.getY());
                g2D.setStroke(new BasicStroke(3));
                g2D.draw(path);

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

                for (Message m : channel) {
                    m.timerCounter--;

                    if (m.type == MessageType.Normal) {
                        g.setColor(channel.channelColor);
                    }
                    else {
                        g.setColor(Color.RED);
                    }

                    if (m.timerCounter == 2) {
                        g.fillRect(node.getX() + 25, node.getY() - 25, 40, 30);
                        g.setColor(Color.WHITE);
                        g.drawString(m.messageID, node.getX() + 30, node.getY() - 10);
                    }
                    else if (m.timerCounter == 1) {
                        g.fillRect((node.getX() + nodeB.getX()) / 2, (node.getY() + nodeB.getY()) / 2, 40, 30);
                        g.setColor(Color.WHITE);
                        g.drawString(m.messageID, ((node.getX() + nodeB.getX()) / 2) + 5, ((node.getY() + nodeB.getY()) / 2) + 15);
                    }
                }

                g.setColor(Color.BLACK);
            }
        }
    }
}

public class Main extends JFrame {

    private List<Node> graph = new ArrayList<>();
    private int size;

    private Container container = new Container();

    public Main() {
        setTitle("Chandy-Lamport State Recording");
        setSize(900, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        init();
    }

    private void init() {
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

        JFileChooser fc = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fc.setFileFilter(filter);
        int res = fc.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                String content = new String(Files.readAllBytes(fc.getSelectedFile().toPath()));
                String[] lines = content.split("\\r\\n");

                size = Integer.parseInt(lines[0]);

                for (int i = 1; i < lines.length; i++) {
                    if (lines[i].isBlank()) continue;

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

                Node startNode = ParseGraph.findStarter(graph);
                System.out.println("Selected starter node : Node " + startNode.id);

                add(container, BorderLayout.CENTER);

                for (int j = 0; j < graph.size(); j++) {
                    Thread t = new Thread(graph.get(j));
                    t.start();
                }

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

    public void checkState() {
        boolean isAllStateRecorded = true;

        for (int i = 0; i < graph.size(); i++) {
            Node node = graph.get(i);

            if (!node.isStateRecorded) {
                isAllStateRecorded = false;
                break;
            }

            for (int j = 0; j < node.outgoingChannels.size(); j++) {
                Channel channel = node.outgoingChannels.get(i);

                for (Message m : channel) {
                    if (m.type == MessageType.Marker) {
                        isAllStateRecorded = false;
                        break;
                    }
                }
            }
        }

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
                    channelData.append("\n\nChannel from Node ").append(ch.nodeA).append(" to Node ").append(ch.nodeB).append(": ");
                    channelData.append("\nRecorded State: [");

                    for (Message m : ch.state) {
                        channelData.append(m.messageID).append(", ");
                    }
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
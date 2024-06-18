import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

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

    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius, this.radius, this.radius, this.radius);
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.drawRoundRect(x, y, width, height, radius*5, radius*5);
    }
}

/**
 * Custom Jpanel class implementing methods to draw lines between nodes
 */
class Container extends JPanel {
    private List<Node> nodeList;

    public Container() {
        super();
    }

    public void setList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Color buttonBgIdle = new Color(185, 235, 255);
        Color buttonBgInCS = new Color(166, 88, 76);
        Color buttonBgRqst = new Color(201, 192, 133);

        // Draw the queue for each nodes when they are not empty
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);

            int x_pos = node.getLocation().x + 90;
            int y_pos = node.getLocation().y + 5;
            int Rectheight = 30;
            int Rectwidth = 20;

            g.setFont(new Font("Bookman Old Style", Font.BOLD, 16));

            Object[] queueArr = node.queue.toArray();

            // Iterate through queue and show node ids in the queue
            for (int j = 0; j < queueArr.length; j++) {
                Node currNode = (Node) queueArr[j];
                g.drawRect(x_pos, y_pos + 20, Rectwidth, Rectheight);
                g.drawString(currNode.id, x_pos + 5, y_pos + Rectheight + 5);
                x_pos += Rectwidth + 5;
            }

            // Set the color of the nodes according to their states
            if (node.isRequestingCS) {
                node.setBackground(buttonBgRqst);
            }
            else if (node.isInCS) {
                node.setBackground(buttonBgInCS);
            }
            else {
                node.setBackground(buttonBgIdle);
            }

            int width = node.getWidth();
            int height = node.getHeight();

            // Show connection between nodes if there exists any
            Point p1 = node.getLocation();
            if (node.parent != null) {
                Point p2 = node.parent.getLocation();

                try {
                    // Image for the arrow head and calculation of its rotation angle
                    BufferedImage arrowHead = ImageIO.read(getClass().getResource("/images/arrowHead.png"));

                    double slope = (p2.y - p1.y) / (p2.x - p1.x);
                    double theta = -Math.atan(slope);

                    AffineTransform at = new AffineTransform();

                    Graphics2D g2d = (Graphics2D) g;

                    // If parent node is above the child node
                    if (p2.y < p1.y) {
                        // Draw the connecting line
                        g.drawLine(p1.x + width/2, p1.y, p2.x + width/2, p2.y + height + 2);
                        // Set position of the arrow head
                        at.translate(p2.x + width/2, p2.y + height + 2);
                        // Rotate the arrow head
                        at.rotate(theta);
                        // Set position again as the rotation happens relative to a corner and the arrow head moves
                        at.translate(-arrowHead.getWidth(this) / 2, -arrowHead.getHeight(this) / 2);
                        // Draw the arrow head
                        g2d.drawImage(arrowHead, at, null);
                    }
                    else {
                        g.drawLine(p1.x + width/2, p1.y + height, p2.x + width/2, p2.y - 12);
                        at.translate(p2.x + width/2, p2.y - 12);
                        at.rotate(-theta);
                        at.translate(-arrowHead.getWidth(this) / 2, -arrowHead.getHeight(this) / 2);
                        g2d.drawImage(arrowHead, at, null);
                    }
                }
                catch (IOException err) {
                    err.printStackTrace();
                }
            }
        }
    }
}

/**
 * Main class for the GUI
 */
public class Main extends JFrame {
    // A list of all the nodes in the system
    private List<Node> nodeList = new ArrayList<>();
    private Container container = new Container();

    public Main() {
        setTitle("Raymond algorithm");
        setSize(900, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
    }

    private void init() {
        // Set the UI theme to the system theme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Define the colors
        Color buttonBgIdle = new Color(185, 235, 255);
        Color buttonFg = new Color(0, 17, 61);

        container.setLayout(null);

        // File selection for the file that contains the input for the tree
        JFileChooser fc = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fc.setFileFilter(filter);
        int res = fc.showOpenDialog(this);

        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                String content = new String(Files.readAllBytes(fc.getSelectedFile().toPath()));

                int result = JOptionPane.showConfirmDialog(this, "Do you want to log each node details every second ?", "Detail Log", JOptionPane.YES_NO_OPTION);
                boolean showDetailedLog = result == 0 ? true : false;

                JOptionPane.showMessageDialog(this, "A Red node is in CS, a yellow node is requesting CS, a blue node is idle", "Information", JOptionPane.INFORMATION_MESSAGE);

                String[] lines = content.split("\\r\\n");

                // First line of the file contains the number of nodes
                int size = Integer.parseInt(lines[0].strip());

                // Create nodes dynamically with every edge
                for (int i = 1; i < lines.length; i++) {
                    Node node1 = new Node(lines[i].split(" ")[0], size);
                    Node node2 = new Node(lines[i].split(" ")[1], size);

                    node1.setSize(50, 50);
                    node1.setBorder(new RoundedBorder(20));
                    node1.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
                    node1.setContentAreaFilled(false);
                    node1.setOpaque(true);
                    node1.setBackground(buttonBgIdle);
                    node1.setForeground(buttonFg);

                    node2.setSize(50, 50);
                    node2.setBorder(new RoundedBorder(20));
                    node2.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
                    node2.setContentAreaFilled(false);
                    node2.setOpaque(true);
                    node2.setBackground(buttonBgIdle);
                    node2.setForeground(buttonFg);

                    // Check if the nodes already exist
                    if (!nodeList.contains(node1)) {
                        nodeList.add(node1);
                        container.add(node1);
                    }
                    else {
                        node1 = nodeList.get(nodeList.indexOf(node1));
                    }
                    if (!nodeList.contains(node2)) {
                        nodeList.add(node2);
                        container.add(node2);
                    }
                    else {
                        node2 = nodeList.get(nodeList.indexOf(node2));
                    }

                    // Set parent
                    node1.parent = node2;
                }

                // Render the tree
                render();

                // Set the root node as priviledged
                for (int i = 0; i < nodeList.size(); i++) {
                    Node node = nodeList.get(i);
                    if (node.parent == null) {
                        node.isPrivileged = true;
                        break;
                    }
                }

                add(container, BorderLayout.CENTER);

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
            }
            catch (IOException err) {
                err.printStackTrace();
            }
        }
    }

    /**
     * Method to render the tree
     */
    private void render() {
        // A hashmap that stores the levels of each node in the tree
        Map<Node, Integer> nodeLevels = new HashMap<>();

        // Assign node levels
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            int level = calcLevel(node);

            nodeLevels.put(node, level);
        }

        // Draw the tree
        int frameHeight = getSize().height;
        int frameWidth = getSize().width;

        int xPadding = 50;
        int yPadding = 50;

        int treeHeight = Collections.max(nodeLevels.values()) + 1;

        // Vertical gap between each levels of the tree
        int vGap = (frameHeight - 2 * yPadding) / (treeHeight + 1);
        int y = vGap - yPadding;

        for (int i = 0; i < treeHeight; i++) {
            List<Node> levelList = new ArrayList<>();

            // Generate a list of nodes in a particular level of the tree
            for (int j = 0; j < nodeList.size(); j++) {
                Node node = nodeList.get(j);
                if (nodeLevels.get(node) == i) {
                    levelList.add(node);
                }
            }

            // Horizontal gap between each node in the same level
            int hGap = (frameWidth - 2 * xPadding) / (levelList.size() + 1);
            int x = xPadding + hGap;

            for (int j = 0; j < levelList.size(); j++) {
                Node node = levelList.get(j);
                node.setLocation(x, y);
                node.setBounds(x, y, 80, 50);
                x += hGap;
            }

            y += vGap;
        }

        container.setList(nodeList);
    }

    /**
     * Method to calculate the level of a node in the given tree.
     * <p>
     * This is a wrapper method around the actual implementation.
     * @param node The node whose level is to be calculated
     * @return The level of the node in the tree
     */
    private int calcLevel(Node node) {
        return calcLevel(node, 0);
    }

    /**
     * Overloaded method of {@link #calcLevel(Node)} that implements the logic for calculating the node.
     * <p>
     * It recursively traverses the tree until it reaches the root node, and then returns the level value
     * which was gradually incremented.
     * @param node The node whose level is to be determined
     * @param level The current level
     * @return The level of the node
     */
    private int calcLevel(Node node, int level) {
        if (node.parent == null) {
            return level;
        }

        return calcLevel(node.parent, ++level);
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
        Timer refreshTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.repaint();
            }
        });

        refreshTimer.setRepeats(true);
        refreshTimer.start();
    }
}
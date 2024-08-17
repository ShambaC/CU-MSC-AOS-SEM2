import java.awt.*;
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
import javax.swing.border.Border;

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
 * Custom panel class
 */
class Container extends JPanel {

    private List<Node> nodeList;
    private Image img;
    private boolean staticPainted = false;

    private Color buttonBgIdle = new Color(185, 235, 255);
    private Color buttonBgInCS = new Color(166, 88, 76);
    private Color buttonBgRqst = new Color(201, 192, 133);

    public Container() {
        super();
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d;

        if (img == null) {
            img = createImage(getSize().width, getSize().height);
        }

        g.drawImage(img, 0, 0, null);

        g.setFont(new Font("Bookman Old Style", Font.BOLD, 16));

    	for(int i = 0; i < nodeList.size(); i++) {
            Node currentNode = nodeList.get(i);

            // Set node background color to appropriate color
            if (currentNode.isRequestingCS) {
                currentNode.setBackground(buttonBgRqst);
            }
            else if (currentNode.isInCS) {
                currentNode.setBackground(buttonBgInCS);
            }
            else {
                currentNode.setBackground(buttonBgIdle);
            }

            // Set location for list placeholders
    		int x_pos = currentNode.getLocation().x + 70;
    		int y_pos = currentNode.getLocation().y - 50;
    		int height = 50;
    		int width = 50;
    		
    		int y_pos1 = currentNode.getLocation().y + 50;
            int y_pos2 = y_pos1 + 65;
    		
    		
    		for(int j = 0; j < nodeList.size() - 1; j++) {
                
                if (!staticPainted && img != null) {
                    g2d = (Graphics2D) img.getGraphics();

                    g2d.drawRect(x_pos, y_pos, width, height);
                    g2d.drawRect(x_pos, y_pos1, width, height);
                    g2d.drawString(Integer.toString(currentNode.nodeList.get(j).siteId), x_pos + 25, y_pos2);
                }

                if (currentNode.isRequestingCS || currentNode.isInCS) {

                    // Display request list
                    if (j <= currentNode.requestList.size() - 1) {
                        String tempSiteId = Integer.toString(currentNode.requestList.get(j).siteId);
                        g.drawString(tempSiteId, x_pos + 25, y_pos + 25);
                    }

                    // Display reply list
                    Node otherNode = currentNode.nodeList.get(j);
                    if (currentNode.replyList.containsKey(otherNode)) {
                        String replyRes = Boolean.toString(currentNode.replyList.get(otherNode));
                        g.drawString(replyRes, x_pos + 5, y_pos1 + 25);
                    }
                    
                }

                x_pos -= 50;
    		}
    	}
        staticPainted = true;
    }
}

/**
 * Main runner class
 */
public class Main extends JFrame {

    /**
     * List of nodes
     */
    static List<Node> nodeList = new ArrayList<>();

    int nodes;

    Container container;

    // Define the colors
    Color buttonBgIdle = new Color(185, 235, 255);
    Color buttonBgInCS = new Color(166, 88, 76);
    Color buttonBgRqst = new Color(201, 192, 133);

    public Main() {
        setTitle("Ricard Agrawala Algorithm Implementation");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
    }

    /**
     * Initialize all the GUI components
     */
    private void init() {
        container = new Container();
        container.setLayout(null);
        Color buttonFg = new Color(0, 17, 61);

        // Take input for number of nodes
        nodes = Integer.parseInt(JOptionPane.showInputDialog("Enter the no. of nodes: "));

        int res = JOptionPane.showConfirmDialog(this, "Do you want to log each node details every second ?", "Detail Log", JOptionPane.YES_NO_OPTION);
        boolean showDetailedLog = res == 0 ? true : false;

        JOptionPane.showMessageDialog(this, "A Red node is in CS, a yellow node is requesting CS, a blue node is neither of them", "Information", JOptionPane.INFORMATION_MESSAGE);
        
        double angleIncrement = 2 * Math.PI / nodes;
        int radius = 200; // Radius of the circle
        int centerX = 480; // Center x of the circle
        int centerY = 380; // Center y of the circle
       
        // Display the nodes in a circle
        for (int i = 0; i < nodes; i++) {
            double angle = i * angleIncrement;
            int x = (int) (centerX + radius * Math.cos(angle) - 40); // 40 is half of the button width for centering
            int y = (int) (centerY + radius * Math.sin(angle) - 25); // 25 is half of the button height for centering
            
            
            Node node = new Node(i, nodes);
            node.setSize(50, 50);
            node.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
            node.setBorder(new RoundedBorder(20));
            node.setBounds(x, y, 80, 50); // Set the button bounds
            node.setBackground(buttonBgIdle);
            node.setForeground(buttonFg);
            nodeList.add(node);
            container.add(node);
        }

        container.setNodeList(nodeList);

        // Setup other nodes in network list of each node
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            for (int j = 0; j < nodeList.size(); j++) {
                if (i != j) {
                    node.nodeList.add(nodeList.get(j));
                }
            }
        }
            
            
        add(container, BorderLayout.CENTER);

        // Start each node
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

        // Refresh the gui
        Timer refreshTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m.repaint();
            }
        });

        refreshTimer.setRepeats(true);
        refreshTimer.start();
    }
}


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;

/**
 * Utility class to create a round button
 */
class RoundedBorder implements Border {
    private int radius;

    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.drawRoundRect(x, y, width, height, radius * 5, radius * 5);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius, radius, radius, radius);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}

/**
 * Main runner class
 */
public class Main extends JFrame {

    /**
     * List of nodes in a custom circular queue
     */
    static Ring nodeList = Ring.getInstance();

    private int nodes;
    private boolean logTokenDetails;
    private Token token;
    
    public Main() {
        setTitle("Token Ring Based Algorithm");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
    }

    /**
     * Initialize all the GUI components
     */
    private void init() {
        JPanel container = new JPanel();
        container.setLayout(null);

        Color buttonFg = new Color(255, 255, 255);

        // Take input for number of nodes
        nodes = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of nodes: "));

        int res = JOptionPane.showConfirmDialog(this, "Do you want to log each node details every second ?", "Detail Log", JOptionPane.YES_NO_OPTION);
        logTokenDetails = res == 0 ? true : false;

        JOptionPane.showMessageDialog(this, "A Green node is in CS, a Red node is requesting CS and a Blue node is currently idle", "Information", JOptionPane.INFORMATION_MESSAGE);

        double angleIncrement = 2 * Math.PI / nodes;
        Dimension window = getSize();
        double radius = window.getHeight() / 2 - 150;   // Radius of the circle
        double centerX = window.getHeight() / 2 - 20;   // Center X of the circle
        double centerY = window.getWidth() / 2 - 20;    // Center y of the circle

        // Display the nodes in a circle
        for (int i = 0; i < nodes; i++) {
            Node node = new Node(Integer.toString(i));

            double angle = i * angleIncrement;
            int x = (int) (centerX + radius * Math.cos(angle) - 40); // 40 is half of the button width for centering
            int y = (int) (centerY + radius * Math.sin(angle) - 25); // 25 is half of the button height for centering

            node.setSize(50, 50);
            node.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
            node.setBorder(new RoundedBorder(20));
            node.setBounds(x, y, 80, 50);
            node.setBackground(Color.blue);
            node.setForeground(buttonFg);
            nodeList.add(node);
            container.add(node);
        }

        // Choose a random started node to hold the token
        Random random = new Random(System.currentTimeMillis());
        int index = random.nextInt(0, nodes);
        Node randomStarterNode = nodeList.get(index);
        token = new Token().setNodeId(randomStarterNode.id);
        System.out.println("Token initialized at Node" + randomStarterNode.id);
        Point tokenLoc = new Point(randomStarterNode.getLocation().x, randomStarterNode.getLocation().y);
        container.add(token);
        token.setLocation(tokenLoc);
        System.out.println();
        token.setSize(300, 300);
        randomStarterNode.token = token;
        randomStarterNode.isPHold = true;

        add(container, BorderLayout.CENTER);

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

        Main guiWindow = new Main();
        guiWindow.setVisible(true);

        // Refresh the gui window at 2 FPS
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiWindow.repaint();
                guiWindow.revalidate();
            }
        });

        timer.setRepeats(true);
        timer.start();
    }
}

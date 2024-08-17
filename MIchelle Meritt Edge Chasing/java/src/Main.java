import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
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

class Container extends JPanel {
    private List<Node> nodeList = new ArrayList<>();

    public Container() {
        super();
    }

    public void setList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            Point p1 = node.getLocation();

            for (int j = 0; j < node.blockingNodes.size(); j++) {
                Point p2 = node.blockingNodes.get(j).getLocation();

                // If point 1 is lower
                if (p1.y > p2.y) {
                    g.drawLine(p1.x + 60, p1.y, p2.x + 60, p2.y + 50);
                    g.fillOval(p2.x + 60, p2.y + 60, 7, 7);
                }
                else {
                    g.drawLine(p1.x + 60, p1.y + 50, p2.x + 60, p2.y);
                    g.fillOval(p2.x + 60, p2.y - 10, 7, 7);
                }
            }
        }
    }
}

public class Main extends JFrame {
    private int size;

    private List<Node> nodeList = new ArrayList<>();

    private Timer timer;

    public Main() {
        setTitle("Michell Meritt");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
    }

    private void init() {
        Container container = new Container();
        container.setLayout(null);

        size = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of nodes: "));

        JOptionPane.showMessageDialog(this, "A blue node is idle, a red node is in CS.\nThe values on the nodes are u and v.\nDependent nodes are shown using edges.", "Information", JOptionPane.INFORMATION_MESSAGE);

        double angleIncrement = 2 * Math.PI / size;
        Dimension window = getSize();
        double radius = window.getHeight() / 2 - 150;   // Radius of the circle
        double centerX = window.getHeight() / 2 - 20;   // Center X of the circle
        double centerY = window.getWidth() / 2 - 20;    // Center y of the circle

        for (int i = 0; i < size; i++) {
            Node node = new Node(Integer.toString(i), i);

            double angle = i * angleIncrement;
            int x = (int) (centerX + radius * Math.cos(angle) - 40); // 40 is half of the button width for centering
            int y = (int) (centerY + radius * Math.sin(angle) - 25); // 25 is half of the button height for centering

            node.setSize(50, 50);
            node.setFont(new Font("Bookman Old Style", Font.BOLD, 17));
            node.setBounds(x, y, 120, 50);
            node.setBackground(Color.cyan);
            nodeList.add(node);
            container.add(node);
        }
        
        container.setList(nodeList);

        add(container, BorderLayout.CENTER);

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
                        container.repaint();
                        container.revalidate();
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

    private void pauseApp() {
        timer.stop();
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

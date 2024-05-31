import java.awt.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

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

public class Main extends JFrame {

    static List<Node> nodeList = new ArrayList<>();

    int nodes;

    JPanel container;

    public Main() {
        setTitle("Ricard Agrawala Algorithm Implementation");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
    }

    private void init() {
        container = new JPanel();
        container.setLayout(null);
        Color buttonBg = new Color(185, 235, 255);
        Color buttonFg = new Color(0, 17, 61);

        nodes = Integer.parseInt(JOptionPane.showInputDialog("Enter the no. of nodes: "));

        int res = JOptionPane.showConfirmDialog(this, "Do you want to log each node details every second ?", "Detail Log", JOptionPane.YES_NO_OPTION);
        boolean showDetailedLog = res == 0 ? true : false;
        
        double angleIncrement = 2 * Math.PI / nodes;
        int radius = 200; // Radius of the circle
        int centerX = 480; // Center x of the circle
        int centerY = 380; // Center y of the circle
       

        for (int i = 0; i < nodes; i++) {
            double angle = i * angleIncrement;
            int x = (int) (centerX + radius * Math.cos(angle) - 40); // 40 is half of the button width for centering
            int y = (int) (centerY + radius * Math.sin(angle) - 25); // 25 is half of the button height for centering
            
            
            Node node = new Node(i, nodes);
            node.setSize(50, 50);
            node.setFont(new Font("Bookman Old Style", Font.BOLD, 20));
            node.setBorder(new RoundedBorder(20));
            node.setBounds(x, y, 80, 50); // Set the button bounds
            node.setBackground(buttonBg);
            node.setForeground(buttonFg);
            nodeList.add(node);
            container.add(node);
        }


        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            for (int j = 0; j < nodeList.size(); j++) {
                if (i != j) {
                    node.nodeList.add(nodeList.get(j));
                }
            }
        }
            
            
        add(container, BorderLayout.CENTER);

        for (Node node : nodeList) {
            Thread t = new Thread(node);
            t.start();
        }

        System.out.println("All threads started");

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
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);

    	for(int i = 0; i < nodeList.size(); i++) {
    		int x_pos = nodeList.get(i).getLocation().x + 70;
    		int y_pos = nodeList.get(i).getLocation().y - 35;
    		int height = 50;
    		int width = 50;
    		
    		int x_pos1 = nodeList.get(i).getLocation().x + 70;
    		int y_pos1 = nodeList.get(i).getLocation().y + 85;
    		
    		
    		for(int j = 0; j < nodeList.size() - 1; j++) {
    			g.drawRect(x_pos, y_pos, width, height);
    			g.drawRect(x_pos1, y_pos1, width, height);

                x_pos -= 50;
                x_pos1 -= 50;
    		}
    	}
    }
    

    public static void main(String[] args) {
        try {
            PrintStream fileStream = new PrintStream("outputLog.txt");
            System.setOut(fileStream);
        }
        catch (FileNotFoundException err) {
            err.printStackTrace();
        }

        Main m = new Main();
        m.setVisible(true);
    }
}


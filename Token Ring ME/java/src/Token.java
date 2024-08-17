import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.JPanel;

/**
 * Token class
 */
public class Token extends JPanel {
    public String nodeId;
    public Queue<Node> queue;

    private boolean isAtLocation = false;

    public Token() {
        this.nodeId = null;
        this.queue = new ArrayDeque<>();
    }

    public Token setNodeId(String nodeId) {
        this.nodeId = nodeId;
        
        return this;
    }

    public boolean isAtLocation() {
        return this.isAtLocation;
    }
    public void setIsAtLocation(boolean value) {
        this.isAtLocation = value;
    }

    /**
     * TO display the token data
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int x_pos = 90;
        int y_pos = 5;
        int height = 30;
        int width = 20;

        g.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        g.setColor(Color.black);

        g.drawString("<TOKEN>", x_pos, y_pos + 10);

        Object[] queueArr = queue.toArray();

        for (int i = 0; i < queueArr.length; i++) {
            Node currNode = (Node) queueArr[i];
            g.drawRect(x_pos, y_pos + 20, width, height);
            g.drawString(currNode.id, x_pos + 5, y_pos + height + 5);
            x_pos += width + 5;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 300);
    }

    @Override
    public String toString() {
        String outString = "\n\n<----TOKEN---->\n";

        outString += isAtLocation ? "Current Location: Node " + nodeId : "Target Location: Node" + nodeId;
        outString += "\nQueue: [";

        Object[] queuArr = queue.toArray();

        for (int i = 0; i < queuArr.length; i++) {
            Node currNode = (Node) queuArr[i];
            outString += "Node " + currNode.id + ", ";
        }

        outString += "]\n\n";

        return outString;
    }
}
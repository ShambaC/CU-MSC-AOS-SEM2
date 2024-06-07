import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.JPanel;
 
public class Token extends JPanel {
    public Node node;
    public Queue<Node> queue;

    private boolean isAtLocation = false;

    public Token() {
        this.node = null;
        this.queue = new ArrayDeque<>();
    }

    public Token setNode(Node node) {
        this.node = node;
        
        return this;
    }

    public boolean isAtLocation() {
        return this.isAtLocation;
    }
    public void setIsAtLocation(boolean value) {
        this.isAtLocation = value;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Point coords = getLocation();
    }

    @Override
    public String toString() {
        String outString = "\n\n<----TOKEN---->\n";

        outString += isAtLocation ? "Current Location: Node " + node.id : "Target Location: Node" + node.id;
        outString += "\nQueue: [";

        Node[] queuArr = (Node[]) queue.toArray();

        for (int i = 0; i < queuArr.length; i++) {
            outString += "Node " + queuArr[i].id + ", ";
        }

        outString += "]\n\n";

        return outString;
    }
}
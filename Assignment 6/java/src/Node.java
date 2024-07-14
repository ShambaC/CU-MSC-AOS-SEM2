import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;

public class Node extends JButton implements Runnable {
    public String id;            // Identifier for the node
    public int u, v;             // Public and private labels for the node

    public boolean isInCS = false;       // Flag to indicate if node is in critical section
    public boolean isInDeadlock = false; // Flag to indicate if node is in deadlock

    public List<Node> waitForGraph;
    public List<Node> depending_nodes;
    private Main ob; 

    public Node(String id, int init, Main ob) {
        super(init + " / " + init);

        this.id = id;
        this.u = this.v = init; // Initialize u and v with the same value initially
        this.depending_nodes = new ArrayList<>();
        this.ob = ob;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (!isInCS) {
                    // Randomly decide if going into CS
                    Random random = new Random();
                    if (random.nextDouble() < 0.5) {
                        isInCS = true;
                      
                        // Randomly decide if it depends on another node
                        
                        }
                    }
                }
            }
        }
    
/*Check if node is dependent then pass the blocked node to block*/
   
    public synchronized void block(Node blockedNode) {
        

        // Set new values u, v for the blocked process
        int k = Math.max(u, blockedNode.u);
        blockedNode.u = k;
        blockedNode.v = k;
    }
    public synchronized void transmit(Node blockedNode) {
        // Transmit public label to nodes in waitForGraph
        for (Node waitingNode : waitForGraph) {
            if (waitingNode.u < this.u) {
                waitingNode.u = this.u;
            }
        }
    }

  
    private boolean isDeadlock() {
        // Check if this node is waiting for itself in a circular dependency
        if (waitForGraph.contains(this)) {
            return true;
        }
        // Check for deadlock condition based on Mitchell Merritt's algorithm
        for (Node node : waitForGraph) {
            if (node.u == node.v && node.v == this.u) {
            	ob.pauseApp();
                //return true;
            }
        }
        return false;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isInCS) {
            setBackground(Color.red);
            setForeground(Color.white);
        } else {
            setBackground(Color.cyan);
            setForeground(Color.black);
        }
    }
}

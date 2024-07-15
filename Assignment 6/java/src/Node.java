import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;

public class Node extends JButton implements Runnable {

    /**
     * Custom class for storing blocked nodes list
     * Calls the block method every time a node is added to it
     */
    class BlockedList extends ArrayList<Node> {
        
        public BlockedList() {
            super();
        }
    
        @Override
        public boolean add(Node e) {
            boolean res = super.add(e);
    
            block(e);
    
            return res;
        }
    }

    public String id;                       // Identifier for the node
    public int u, v;                        // Public and private labels for the node

    public boolean isInCS = false;          // Flag to indicate if node is in critical section
    public boolean isInDeadlock = false;    // Flag to indicate if node is in deadlock
    public boolean running = true;          // Flag to keep a thread running

    public List<Node> nodeList = new ArrayList<>();
    public List<Node> blockingNodes = new ArrayList<>();
    public List<Node> blockedNodes = new BlockedList();

    private double chance;                  // Helps in determining the chance of a node to go into CS

    public Node(String id, int init) {
        super(id + ", " + init + " / " + init);

        this.id = id;
        this.u = this.v = init;             // Initialize u and v with the same value initially

        Random random = new Random();
        chance = random.nextDouble();
    }

    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Random random = new Random();
                if (!isInCS) {
                    // Randomly decide if going into CS
                    if (random.nextDouble() < chance) {
                        isInCS = true;                       
                    }
                }

                if (isInCS) {
                    // Randomly decide if it depends on another node
                    if (random.nextDouble() > 0.2) {
                        boolean foundblock = false;
                        int tries = 0;
                        // Try assigning a node to block the current node
                        do {
                            int choice = random.nextInt(nodeList.size());
                            Node blockingNode = nodeList.get(choice);

                            if (!blockingNode.equals(this) && blockingNode.isInCS && !blockingNodes.contains(blockingNode)) {
                                foundblock = true;
                                blockingNodes.add(blockingNode);
                                blockingNode.blockedNodes.add(this);

                                System.out.println("\nNode " + id + " is blocked by Node " + blockingNode.id);
                            }

                            tries++;
                            if (tries == 5) foundblock = true;
                        } while (!foundblock && running);
                    }
                }
            }
        }
    }
    
    /**
     * Method to update the label of a node.
     * Calls the transmit method to check for it
     * @param u public label
     * @param v private label
     */
    public void updateLabels(int u, int v) {
        this.u = u;
        this.v = v;

        setText(id + ", " + u + " / " + v);
        System.out.println("\nSetting u, v values of Node " + id + " to (" + u + ", " + v + ")");

        for (int i = 0; i < blockedNodes.size(); i++) {
            Node node = blockedNodes.get(i);

            transmit(node);
        }
    }
    
    /**
     * Method to set labels upon blocking
     * @param blockedNode The node getting blocked by current node
     */
    public void block(Node blockedNode) {
        // Set new values u, v for the blocked process
        System.out.println("\nBLOCK :: Updating u,v values of Node " + blockedNode.id);
        int k = Math.max(u, blockedNode.u) + 1;
        blockedNode.updateLabels(k, k);
    }

    /**
     * Method to update a propagate public label when a connection is made
     * @param node blocking node
     */
    public void transmit(Node node) {
        // Transmit public label to nodes in waitForGraph
        if (this.u > node.u) {
            System.out.println("\nTRANSMIT :: from Node " + id + ", updating label of Node " + node.id);
            node.updateLabels(this.u, node.v);
        }
    }

    /**
     * Method to detect deadlock by checking the labels
     */
    public void detect() {
        if (this.u == this.v) {
            for (int i = 0; i < blockingNodes.size(); i++) {
                Node node = blockingNodes.get(i);

                if (this.u == node.u) {
                    System.out.println("\nDEADLOCK DETECTED");

                    this.isInDeadlock = true;
                    node.isInDeadlock = true;
                    break;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isInCS) {
            setBackground(Color.red);
            setForeground(Color.white);
        }
        else {
            setBackground(Color.cyan);
            setForeground(Color.black);
        }

        if (isInDeadlock) {
            setBackground(Color.blue);
            setForeground(Color.white);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)    return false;
        if (!(obj instanceof Node)) return false;

        Node obj_t = (Node) obj;
        return this.id.equalsIgnoreCase(obj_t.id);
    }
}

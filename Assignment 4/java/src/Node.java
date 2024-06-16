import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.JButton;

public class Node extends JButton implements Runnable {
    
    public String id;
    public Node parent;
    public Queue<Node> queue;
    public boolean isPrivileged;

    public Node(String id, int size) {
        super(id);

        this.id = id;
        this.parent = null;
        this.queue = new ArrayDeque<>(size);
        this.isPrivileged = false;
    }

    public Node setParent(Node parent) {
        this.parent = parent;

        return this;
    }

    @Override
    public void run() {
        
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)    return false;
        if (!(obj instanceof Node))   return false;

        Node oNode = (Node) obj;
        return oNode.id.equalsIgnoreCase(this.id);
    }
}

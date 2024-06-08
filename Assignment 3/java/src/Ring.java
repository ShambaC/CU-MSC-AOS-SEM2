import java.util.LinkedList;

/**
 * Singleton class that represents a distributed system connected in a ring topology
 */
public class Ring extends LinkedList<Node> {
    /**
     * Instance variable of the class
     */
    private static Ring instance;
 
    /**
     * Private constructor to avoid making new copies of this class's object
     */
    private Ring() {
        super();
    }

    /**
     * Method to get the only instance of this class
     * @return An object of this class
     */
    public static Ring getInstance() {
        if (instance == null) {
            instance = new Ring();
        }

        return instance;
    }

    /**
     * Method to add a node to the system
     * @param node The node to be added to the system
     */
    @Override
    public boolean add(Node node) {
        boolean res = true;

        if (!this.isEmpty()) {
            Node lastNode = this.getLast();
            lastNode.setNextNode(node);
            res = super.add(node);
 
            Node firstNode = this.getFirst();
            node.setNextNode(firstNode);
        }
        else {
            res = super.add(node);
        }

        return res;
    }
}
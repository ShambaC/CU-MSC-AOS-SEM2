import java.util.LinkedList;

/**
 * Singleton class that represents a distributed system connected in a ring topology
 */
public class Ring {
    /**
     * Instance variable of the class
     */
    private static Ring instance;

    /**
     * List to store all the nodes in the system
     */
    private LinkedList<Node> nodeList;
 
    /**
     * Private constructor to avoid making new copies of this class's object
     */
    private Ring() {
        nodeList = new LinkedList<>();
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
    public void add(Node node) {
        if (!nodeList.isEmpty()) {
            Node lastNode = nodeList.getLast();
            lastNode.setNextNode(node);
            nodeList.add(node);
 
            Node firstNode = nodeList.getFirst();
            node.setNextNode(firstNode);
        }
        else {
            nodeList.add(node);
        }
    }
}
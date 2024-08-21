import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Token class
 */
public class Token {
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
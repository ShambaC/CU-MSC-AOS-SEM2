import java.util.ArrayDeque;
import java.util.Queue;
 
public class Token {
    public Node node;
    public Queue<Node> queue;

    public Token() {
        this.node = null;
        this.queue = new ArrayDeque<>();
    }

    public Token setNode(Node node) {
        this.node = node;
        
        return this;
    }
}
import java.util.ArrayDeque;

public class Channel extends ArrayDeque<Message> {
    
    public Node nodeA;
    public Node nodeB;

    public Channel(Node nodeA, Node nodeB) {
        super();

        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }
}

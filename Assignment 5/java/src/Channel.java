import java.util.List;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Channel extends ArrayDeque<Message> {
    
    public Node nodeA;
    public Node nodeB;

    public List<Message> state = new ArrayList<>();

    public Channel(Node nodeA, Node nodeB) {
        super();

        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }
}

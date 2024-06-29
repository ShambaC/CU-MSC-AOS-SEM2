import java.util.List;
import java.util.Random;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Channel extends ArrayDeque<Message> {
    
    public Node nodeA;
    public Node nodeB;

    public List<Message> state = new ArrayList<>();

    public Color channelColor;

    public Channel(Node nodeA, Node nodeB) {
        super();

        this.nodeA = nodeA;
        this.nodeB = nodeB;

        Random random = new Random();

        float r = random.nextFloat();
        float g = random.nextFloat() / 1.5f;
        float b = random.nextFloat();

        this.channelColor = new Color(r, g, b);
    }
}

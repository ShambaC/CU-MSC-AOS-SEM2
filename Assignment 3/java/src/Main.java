import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class Main extends JFrame {

    static List<Node> nodeList = new ArrayList<>();

    private int nodes;
    private boolean logTokenDetails;
    
    public Main() {
        init();
    }

    private void init() {



        int index = (int) Math.random() * nodeList.size();
        Node randomStarterNode = nodeList.get(index);
        Token token = new Token().setNode(randomStarterNode);
        token.setLocation(randomStarterNode.getLocation().x, randomStarterNode.getLocation().y + randomStarterNode.getHeight());
        randomStarterNode.token = token;
        randomStarterNode.isPHold = true;
    }
}

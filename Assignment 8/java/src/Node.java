import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;

public class Node extends JLabel implements Runnable {
    private static final float MIN_WEIGHT = 0.01f;
    
    public String id;
    public float weight;
    public boolean isIdle;

    private Controller controller;
    private List<Node> nodeList;

    public Node(String id, Controller controller) {
        super(id + ", 0.0");

        this.id = id;
        this.weight = 0.0f;
        this.isIdle = true;

        this.controller = controller;
        this.nodeList = new ArrayList<>();
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    private void sendMsg(Node target) {
        Random random = new Random(System.currentTimeMillis());
        float weightToSend = random.nextFloat(MIN_WEIGHT, this.weight);

        this.weight -= weightToSend;

        // System.out.println("\nNode " + this.id + " sending msg to Node " + target.id + " with weight " + weightToSend);

        target.receiveMsg(this, weightToSend);
    }

    public void receiveMsg(Node sender, float weight) {
        this.weight += weight;

        if (this.weight > 0) {
            this.isIdle = false;
        }
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if (!isIdle) {
                isIdle = random.nextBoolean();
                
                if (!isIdle) {
                    int choice = random.nextInt(0, nodeList.size());
                    Node targetNode = nodeList.get(choice);
                    sendMsg(targetNode);
                }
                else {
                    // TODO: send all weight to controller
                    break;
                }
            }
        }
    }
}

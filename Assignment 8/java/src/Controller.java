import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;

public class Controller extends JLabel implements Runnable {
    
    public float weight;
    
    private List<Node> nodeList;
    private boolean isTerminated = false;
    private static final float MIN_WEIGHT = 0.01f;

    public Controller() {
        super("Controller, 1.0");

        this.weight = 1.0f;
        this.nodeList = new ArrayList<>();
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public void sendMsg(Node target) {
        Random random = new Random(System.currentTimeMillis());
        float weightToSend = random.nextFloat(MIN_WEIGHT, this.weight);

        this.weight -= weightToSend;

        target.receiveMsg(null, weightToSend);
    }

    public void returnWeight(Node sender, float weight) {
        this.weight += weight;

        if (weight == 1.0f) {
            isTerminated = true;
        }
    }

    @Override
    public void run() {
        Random random = new Random();

        while(!isTerminated) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean sendMessage = random.nextBoolean();

            if (sendMessage) {
                int choice = random.nextInt(0, nodeList.size());
                Node targetNode = nodeList.get(choice);
                sendMsg(targetNode);
            }
        }

        // TODO: Termination code here
    }
}

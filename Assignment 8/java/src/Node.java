import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JTextArea;

public class Node extends JLabel implements Runnable {
    private static final float MIN_WEIGHT = 0.01f;
    
    public String id;
    public float weight;
    public boolean isIdle;

    private Controller controller;
    private List<Node> nodeList;
    private JTextArea tArea;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Node(String id, Controller controller, JTextArea tArea) {
        super("Node " + id + ", 0.0");

        this.id = id;
        this.weight = 0.0f;
        this.isIdle = true;

        this.controller = controller;
        this.nodeList = new ArrayList<>();
        this.tArea = tArea;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    private void updateLabel() {
        setText("Node " + id + ", " + this.weight);
    }

    private void sendMsg(Node target) {
        if (this.weight < MIN_WEIGHT) {
            return;
        }

        Random random = new Random(System.currentTimeMillis());
        float weightToSend = random.nextFloat(MIN_WEIGHT, this.weight);

        this.weight -= weightToSend;
        updateLabel();

        // System.out.println("\nNode " + this.id + " sending msg to Node " + target.id + " with weight " + weightToSend);
        LocalDateTime now = LocalDateTime.now();
        tArea.append("\n" + dtf.format(now) + " - Node " + this.id + " sending msg to Node " + target.id + " with weight " + weightToSend);

        target.receiveMsg(this, weightToSend);
    }

    public void receiveMsg(Node sender, float weight) {
        this.weight += weight;
        updateLabel();

        if (this.weight > 0) {
            this.isIdle = false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (sender != null) {
            tArea.append("\n" + dtf.format(now) + " - Node " + this.id + " Received weight " + weight + " from Node " + sender.id);
        }
        else {
            tArea.append("\n" + dtf.format(now) + " - Node " + this.id + " Received weight " + weight + " from Controller");
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

            updateLabel();
            
            if (!isIdle) {
                isIdle = (random.nextDouble() > 0.7);
                
                if (!isIdle) {
                    int choice = random.nextInt(0, nodeList.size());
                    Node targetNode = nodeList.get(choice);
                    if (targetNode.id.equalsIgnoreCase(this.id)) {
                        continue;
                    }
                    sendMsg(targetNode);
                }
                else {
                    controller.returnWeight(this, this.weight);

                    LocalDateTime now = LocalDateTime.now();
                    tArea.append("\n" + dtf.format(now) + " - Node " + this.id + " returning all weight " + this.weight + " to controller");

                    this.weight = 0.0f;
                    updateLabel();

                    break;
                }
            }
        }
    }
}

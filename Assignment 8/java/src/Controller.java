import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Controller extends JLabel implements Runnable {
    
    public float weight;
    
    private List<Node> nodeList;
    private boolean isTerminated = false;
    private JTextArea tArea;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Controller(JTextArea tArea) {
        super("Controller, 1.0");

        this.weight = 1.0f;
        this.nodeList = new ArrayList<>();
        this.tArea = tArea;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public void sendMsg(Node target) {
        float weightToSend = 0.9f;

        this.weight -= weightToSend;
        updateLabel();

        LocalDateTime now = LocalDateTime.now();
        tArea.append("\n" + dtf.format(now) + " - Controller sending msg to Node " + target.id + " with weight " + weightToSend);

        target.receiveMsg(null, weightToSend);
    }

    public void returnWeight(Node sender, float weight) {
        this.weight += weight;
        updateLabel();

        LocalDateTime now = LocalDateTime.now();
        tArea.append("\n" + dtf.format(now) + " - Controller Received weight " + weight + " from Node " + sender.id);

        if (this.weight == 1.0f) {
            isTerminated = true;
        }
    }

    @Override
    public void run() {
        Random random = new Random();
        boolean sendMessage = false;

        while(!isTerminated) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateLabel();

            if (!sendMessage) {
                int choice = random.nextInt(0, nodeList.size());
                Node targetNode = nodeList.get(choice);
                sendMsg(targetNode);
                sendMessage = true;
            }

            if (isTerminated)   break;
        }

        JOptionPane.showMessageDialog(this, "The system has now terminated", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateLabel() {
        setText("Controller" + ", " + this.weight);
    }
}

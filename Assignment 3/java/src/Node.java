import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class Node extends JButton implements Runnable {
 
    public String id;
    public boolean isPHold;
    public Token token;

    private Node nextNode;

    public boolean isRequestingCS = false;
    public boolean isInCS = false;

    private boolean isRequestSent = false;

    private ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (!isRequestingCS && !isInCS) {
                isRequestingCS = true;
            }
        };
    };
 
    public Node(String id) {
        super(id);

        this.id = id;
        this.isPHold = false;
        this.token = null;
        this.nextNode = null;

        this.addActionListener(actionListener);
    }
 
    public void setNextNode(Node node) {
        this.nextNode = node;
    }
    public Node getNextNode() {
        return this.nextNode;
    }

    public void sendRequest(Node node) {

        if (this.isPHold) {
            if (!this.id.equals(node.id)){
                token.queue.add(node);
            }
        }
        else {
            if (this.nextNode != null) {
                this.nextNode.sendRequest(node);
            }
        }
    }

    @Override
    public void run() {
        if (isRequestingCS) {
            if (isPHold) {

            }
            else if (!isRequestSent) {
                this.nextNode.sendRequest(this);
                isRequestSent = true;
            }
        }
    }
}
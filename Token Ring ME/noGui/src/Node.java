import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Random;

import javax.swing.Timer;

/**
 * Node class represents a node in a distributed system
 */
public class Node implements Runnable {
 
    public String id;
    public boolean isPHold;
    public Token token;

    private Node nextNode;

    public boolean isRequestingCS = false;
    public boolean isInCS = false;

    private boolean isRequestSent = false;

    public void doAction() {
        if (!isRequestingCS && !isInCS) {
            System.out.println("\nNode " + id + " is requesting for CS");
            isRequestingCS = true;
        }
    }
 
    public Node(String id) {

        this.id = id;
        this.isPHold = false;
        this.token = null;
        this.nextNode = null;
    }
 
    public void setNextNode(Node node) {
        this.nextNode = node;
    }
    public Node getNextNode() {
        return this.nextNode;
    }

    /**
     * Method to send request to next nodes in the network requesting for CS
     * @param node The node sending the request
     */
    public void sendRequest(Node node) {
        System.out.println("\nNode " + id + " has recieved a request from Node " + node.id);

        if (this.isPHold) {
            if (isInCS) {
                if (!this.id.equals(node.id)){
                    System.out.println("\nNode " + id + " is in CS, adding the request of Node " + node.id + " to the queue");
                    token.queue.add(node);
                }
            }
            else {
                System.out.println("\nNode " + id + " is p_hold but not in CS, forwarding the token to next Node " + this.nextNode.id + ", destination: Node " + node.id);
                token.setNodeId(node.id);
                this.isPHold = false;
                this.nextNode.sendToken(token);
            }
        }
        else {
            if (this.nextNode != null) {
                System.out.println("\nNode " + id + " is not the p_hold, forwarding the request to Node " + this.nextNode.id);
                this.nextNode.sendRequest(node);
            }
        }
    }

    /**
     * Method to pass the token along the network
     * @param recievedToken the token of the network
     */
    public void sendToken(Token recievedToken) {
        if (recievedToken.nodeId.equals(this.id)) {
            System.out.println("\nRecieved token at Node " + id + ", this is the required destination. Making it p_hold");
            this.isPHold = true;
            this.token = recievedToken;
            this.token.setIsAtLocation(true);
        }
        else {
            System.out.println("\nRecieved token at Node " + id + ", not the required destination, forwarding token to Node " + this.nextNode.id);
            recievedToken.setIsAtLocation(false);
            nextNode.sendToken(recievedToken);
        }
    }

    /**
     * Method to start CS and stay in CS for a random amount of time
     */
    private void startCS() {
        Random random = new Random(System.currentTimeMillis());
        int randomDelay = random.nextInt(2, 11);

        System.out.println("\nNode " + id + " going into CS for " + randomDelay + "s\n");

        Timer timer = new Timer(randomDelay * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopCS();
            }
        });

        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Method to stop CS and reset required flags and handle token
     */
    private void stopCS() {
        System.out.println("\nNode " + id + " is done with CS.");
        isInCS = false;

        if (!token.queue.isEmpty()) {
            Node nextInQueue = token.queue.poll();

            System.out.println("Passing the token to the next node " + this.nextNode.id);
            token.setNodeId(nextInQueue.id);
            isPHold = false;
            this.nextNode.sendToken(token);
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized(this) {
                if (isRequestingCS) {
                    if (isPHold) {
                        isRequestingCS = false;
                        isRequestSent = false;
                        isInCS = true;
                        startCS();
                    }
                    else if (!isRequestSent) {
                        this.nextNode.sendRequest(this);
                        isRequestSent = true;
                    }
                }
            }
        }
    }
}
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.Timer;

/**
 * Node class represents a node in a distributed system
 */
public class Node extends JButton implements Runnable {
    public int siteId;
    public List<Node> nodeList;
    public List<Node> requestList;
    public Map<Node, Boolean> replyList;
    public EventClock localClock;

    private EventClock CSTimeStamp;

    public boolean isRequestingCS;
    public boolean isInCS;

    private boolean allRequestSent = false;

    private ActionListener nodeButtonListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (!isRequestingCS && !isInCS) {
                System.out.println("\nNode" + siteId + " requesting for CS at local clock counter: " + (localClock.counter + 1));
                isRequestingCS = true;
                localClock.counter += 1;
                CSTimeStamp.counter = localClock.counter;
            }
        };
    };

    public Node(int siteId, int numNodes) {
        super(Integer.toString(siteId));

        this.siteId = siteId;
        this.nodeList = new ArrayList<>(numNodes - 1);
        this.requestList = new ArrayList<>(numNodes - 1);
        this.replyList = new HashMap<>();
        this.localClock = new EventClock(0, siteId);
        this.CSTimeStamp = new EventClock(0, siteId);

        this.isRequestingCS = false;
        this.isInCS = false;

        this.addActionListener(nodeButtonListener);
    }

    /**
     * Method to request for CS
     * 
     * @param node Node making the request
     * @return Approval Reply
     */
    public boolean request(Node node) {
        System.out.println("\n" + siteId + " has received a request message from " + node.siteId);
        localClock.counter = Math.max(node.localClock.counter, localClock.counter) + 1;

        boolean replyVal;

        EventClock CSEvent = node.CSTimeStamp;
        if (isRequestingCS) {
            if (CSEvent.counter < CSTimeStamp.counter) {
                replyVal = true;
            }
            else if (CSEvent.counter > CSTimeStamp.counter) {
                System.out.println("Node" + siteId + " added Node" + node.siteId + " to its request List");
                requestList.add(node);
                replyVal = false;
            }
            else {
                if (CSEvent.siteId < CSTimeStamp.siteId) {
                    replyVal = true;
                }
                else {
                    System.out.println("Node" + siteId + " added Node" + node.siteId + " to its request List");
                    requestList.add(node);
                    replyVal = false;
                }
            }
        }
        else if (isInCS) {
            System.out.println("Node" + siteId + " added Node" + node.siteId + " to its request List");
            requestList.add(node);
            replyVal = false;
        }
        else {
            replyVal = true;
        }

        System.out.println("Node" + siteId + " replied " + replyVal + " to Node" + node.siteId);
        return replyVal;        
    }

    /**
     * Method to recieve custom reply from a node
     * @param node Node sending the reply
     * @param value Reply value
     */
    public void reply(Node node, boolean value) {
        if (isRequestingCS) {
            localClock.counter = Math.max(node.localClock.counter, localClock.counter) + 1;
            replyList.put(node, value);
        }
    }

    /**
     * Method to reset data when exiting CS
     */
    private void stopCS() {
        System.out.println("\nNode" + siteId + " is now done with CS");

        isInCS = false;
        for (Node node : requestList) {
            System.out.println("Node" + siteId + " gave approval to Node" + node.siteId + " after CS");
            localClock.counter += 1;
            node.reply(this, true);
        }
        requestList.clear();
        replyList.replaceAll((key, oldValue) -> false);
        allRequestSent = false;
    }

    /**
     * Method to Enter CS for a random time
     */
    private void CSTimer() {
        Random random = new Random(System.currentTimeMillis());
        int randomDelay = random.nextInt(1, 11) * 1000;

        System.out.println("\nNode" + siteId + " going into CS for " + randomDelay / 1000 + "s\n");

        Timer timer = new Timer(randomDelay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stopCS();
            }
        });

        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void run() {
        System.out.println("\nNode" + siteId + " thread run\n");
        while (true) {

            synchronized(this) {
                if (isRequestingCS) {
                    // Send request to all nodes
                    if (!allRequestSent) {
                        for (Node node : nodeList) {
                            localClock.counter += 1;
                            replyList.put(node, node.request(this));
                        }
                        allRequestSent = true;
                    }
    
                    // Check reply from all nodes
                    boolean allRequestApproved = true;
                    for (Node node : nodeList) {
                        if (!replyList.get(node)) {
                            allRequestApproved = false;
                        }
                    }
                    if (allRequestApproved) {
                        isRequestingCS = false;
                        isInCS = true;
                        CSTimer();
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        String outString = "\n";
        outString += "Node: " + siteId;
        outString += "\nLocal Clock: " + localClock;
        outString += "\nCSTimeStamp: " + CSTimeStamp;
        outString += "\nisRequestingCS: " + isRequestingCS;
        outString += "\nisInCS: " + isInCS;

        outString += "\nNodeList: [";
        for (Node node : nodeList) {
            outString += "'Node" + node.siteId + "', ";
        }
        outString += "]";

        outString += "\nRequestList: [";
        for (int i = 0; i < requestList.size(); i++) {
            Node node = requestList.get(i);
            outString += "'Node" + node.siteId + "', ";
        }
        outString += "]";

        outString += "\nReplyList: {";
        for (Node key : replyList.keySet()) {
            outString += "'Node" + key.siteId + "': " + replyList.get(key) + ", ";
        }
        outString += "}";

        outString += "\n";

        return outString;
    }
}
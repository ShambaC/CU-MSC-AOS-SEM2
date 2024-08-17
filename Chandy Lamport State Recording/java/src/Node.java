import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.JButton;

/**
 * This class represents a Node in a distributed system
 */
public class Node extends JButton implements Runnable {

    /**
     * ID of the node
     */
    public String id;
    /**
     * Incoming edges
     */
    public List<Channel> incomingChannels = new ArrayList<>();
    /**
     * Outgoing edges
     */
    public List<Channel> outgoingChannels = new ArrayList<>();

    public boolean isStateRecorded = false;
    public boolean visited = false;

    /**
     * Incoming messages since the last time this node received a marker message
     */
    public List<Message> incomingMessages = new ArrayList<>();
    /**
     * Outgoing message over lifetime of one recording
     */
    public List<Message> outgoingMessages = new ArrayList<>();
    /**
     * Incoming messages recorded the first time receiving a marker message
     */
    public List<Message> recordedMessages = new ArrayList<>();
    private int messageCounter = 0;

    public Node(String id) {
        super(id);

        this.id = id;
    }

    /**
     * This method starts the state recording algorithm.
     * <p>
     * Only to be called by the starter node.
     */
    public void startRecording() {
        isStateRecorded = true;

        recordedMessages.addAll(incomingMessages);
        incomingMessages.clear();

        StringBuffer logBuffer = new StringBuffer();
        logBuffer.append("\nRecording the state of Node ").append(id);
        System.out.println(logBuffer.toString());
        logBuffer.setLength(0);
                                    
        for (int j = 0; j < outgoingChannels.size(); j++) {
            Channel outChannel = outgoingChannels.get(j);

            messageCounter++;
            Message message = new Message(messageCounter + id, MessageType.Marker, this);
            outChannel.add(message);

            logBuffer.append("\nNode ").append(id).append(" sending marker to Node ").append(outChannel.nodeB.id);
            System.out.println(logBuffer.toString());
        }
    }

    @Override
    public void run() {
        Random random = new Random(System.currentTimeMillis());

        while (true) {
            synchronized (this) {
                // Perform all operations at a gap of 1s
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // If state isnt recorded yet, then the ndoe can send messages to otehr ndoes
                if (!isStateRecorded) {
                    for (int i = 0; i < outgoingChannels.size(); i++) {
                        Channel channel = outgoingChannels.get(i);

                        // A 1/3 random chance on when to send a message
                        int pull = random.nextInt(3);
                        if (pull == 1) {
                            messageCounter++;
                            Message message = new Message(messageCounter + id, MessageType.Normal, this);
                            channel.add(message);
                            outgoingMessages.add(message);

                            StringBuffer logBuffer = new StringBuffer();
                            logBuffer.append("\nNode ").append(id).append(" sent a normal message ").append(message.messageID).append(" to Node ").append(channel.nodeB.id);
                            System.out.println(logBuffer.toString());
                        }
                    }
                }

                // Go through incoming channels to get all messages
                for (int i = 0; i < incomingChannels.size(); i++) {
                    Channel channel = incomingChannels.get(i);

                    if (!channel.isEmpty()) {
                        Message front = channel.element();

                        // It takes 3 seconds for a message to reach its destination.
                        if (front.timer == 0) {
                            channel.poll();

                            if (front.type == MessageType.Normal) {
                                incomingMessages.add(front);

                                StringBuffer logBuffer = new StringBuffer();
                                logBuffer.append("\nNode ").append(id).append(" received a normal message ").append(front.messageID).append(" from Node ").append(front.source.id);
                                System.out.println(logBuffer.toString());
                            }
                            else {
                                // if receiving marker for the first time
                                if (!isStateRecorded) {
                                    isStateRecorded = true;

                                    StringBuffer logBuffer = new StringBuffer();
                                    logBuffer.append("\nNode ").append(id).append(" received a marker message from Node ").append(front.source.id);
                                    System.out.println(logBuffer.toString());
                                    logBuffer.setLength(0);

                                    logBuffer.append("\nRecording the state of Node ").append(id);
                                    System.out.println(logBuffer.toString());
                                    logBuffer.setLength(0);

                                    // save all incoming messages
                                    recordedMessages.addAll(incomingMessages);
                                    // Clear current incoming list
                                    incomingMessages.clear();
                                    // Set the state of the channel as empty
                                    channel.state.clear();
                                    
                                    // Send marker to all other nodes
                                    for (int j = 0; j < outgoingChannels.size(); j++) {
                                        Channel outChannel = outgoingChannels.get(j);

                                        messageCounter++;
                                        Message message = new Message(messageCounter + id, MessageType.Marker, this);
                                        outChannel.add(message);

                                        logBuffer.append("\nNode ").append(id).append(" sending marker to Node ").append(outChannel.nodeB.id);
                                        System.out.println(logBuffer.toString());
                                    }
                                }
                                else {
                                    // Clear and add appropriate messages to the channel state
                                    
                                    channel.state.clear();
                                    channel.state.addAll(incomingMessages.stream().filter(message -> message.source.id.equalsIgnoreCase(channel.nodeA.id)).collect(Collectors.toList()));

                                    StringBuffer logBuffer = new StringBuffer();
                                    logBuffer.append("\nNode ").append(id).append(" received a marker message from Node ").append(front.source.id);
                                    logBuffer.append("\n^== This node is already recorded, recording channel state");
                                    System.out.println(logBuffer.toString());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Node))
            return false;

        Node tmp_o = (Node) obj;
        return tmp_o.id.equalsIgnoreCase(id);
    }
}
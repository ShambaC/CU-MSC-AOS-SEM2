import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;

public class Node extends JButton implements Runnable {

    public String id;
    public List<Channel> incomingChannels = new ArrayList<>();
    public List<Channel> outgoingChannels = new ArrayList<>();

    public boolean isStateRecorded = false;
    public boolean visited = false;

    public List<Message> incomingMessages = new ArrayList<>();
    public List<Message> outgoingMessages = new ArrayList<>();
    public List<Message> recordedMessages = new ArrayList<>();
    private int messageCounter = 0;

    public Node(String id) {
        super(id);

        this.id = id;
    }

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
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!isStateRecorded) {
                    for (int i = 0; i < outgoingChannels.size(); i++) {
                        Channel channel = outgoingChannels.get(i);

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

                for (int i = 0; i < incomingChannels.size(); i++) {
                    Channel channel = incomingChannels.get(i);

                    if (!channel.isEmpty()) {
                        Message front = channel.element();

                        if (front.timer == 0) {
                            channel.poll();

                            if (front.type == MessageType.Normal) {
                                incomingMessages.add(front);

                                StringBuffer logBuffer = new StringBuffer();
                                logBuffer.append("\nNode ").append(id).append(" received a normal message ").append(front.messageID).append(" from Node ").append(front.source.id);
                                System.out.println(logBuffer.toString());
                            }
                            else {
                                if (!isStateRecorded) {
                                    isStateRecorded = true;

                                    StringBuffer logBuffer = new StringBuffer();
                                    logBuffer.append("\nNode ").append(id).append(" received a marker message from Node ").append(front.source.id);
                                    System.out.println(logBuffer.toString());
                                    logBuffer.setLength(0);

                                    logBuffer.append("\nRecording the state of Node ").append(id);
                                    System.out.println(logBuffer.toString());
                                    logBuffer.setLength(0);

                                    recordedMessages.addAll(incomingMessages);
                                    incomingMessages.clear();
                                    channel.state.clear();
                                    
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
                                    channel.state.clear();
                                    channel.state.addAll(incomingMessages);

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
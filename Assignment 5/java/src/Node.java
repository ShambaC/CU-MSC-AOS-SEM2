import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Node implements Runnable {

    public String id;
    public List<Channel> incomingChannels = new ArrayList<>();
    public List<Channel> outgoingChannels = new ArrayList<>();

    public boolean isStateRecorded = false;

    private List<Message> incomingMessages = new ArrayList<>();
    private List<Message> outgoingMessages = new ArrayList<>();
    private List<Message> recordedMessages = new ArrayList<>();
    private int messageCounter = 0;

    public Node(String id) {
        this.id = id;
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

                        int pull = random.nextInt(2);
                        if (pull == 1) {
                            messageCounter++;
                            Message message = new Message(messageCounter + id, MessageType.Normal, this);
                            channel.add(message);
                            outgoingMessages.add(message);
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
                            }
                            else {
                                if (!isStateRecorded) {
                                    isStateRecorded = true;

                                    recordedMessages.addAll(incomingMessages);
                                    incomingMessages.clear();
                                    channel.state.clear();
                                    
                                    for (int j = 0; j < outgoingChannels.size(); j++) {
                                        Channel outChannel = outgoingChannels.get(j);

                                        messageCounter++;
                                        Message message = new Message(messageCounter + id, MessageType.Marker, this);
                                        outChannel.add(message);
                                    }
                                }
                                else {
                                    channel.state.clear();
                                    channel.state.addAll(channel);
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
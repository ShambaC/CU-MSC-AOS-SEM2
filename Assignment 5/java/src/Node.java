import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Node implements Runnable {
    
    public String id;
    public List<Channel> channels = new ArrayList<>();
    public boolean isStateRecorded = false;

    private List<Message> incomingMessages = new ArrayList<>();
    private List<Message> outgoingMessages = new ArrayList<>();
    private int messageCounter = 0;

    public Node(String id) {
        this.id = id;
    }

    @Override
    public void run() {
        Random random = new Random(System.currentTimeMillis());

        while (true) {
            synchronized (this) {
                if (!isStateRecorded) {
                    try {
                        Thread.sleep(1000);

                        for (int i = 0; i < channels.size(); i++) {
                            Channel channel = channels.get(i);

                            int pull = random.nextInt(2);
                            if (pull == 1) {
                                messageCounter++;
                                Message message = new Message(id + "A", MessageType.Normal, this);
                                channel.add(message);
                            }
                        }
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Node)) return false;

        Node tmp_o = (Node) obj;
        return tmp_o.id.equalsIgnoreCase(id);
    }
}
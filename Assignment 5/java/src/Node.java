import java.util.ArrayList;
import java.util.List;

public class Node implements Runnable {
    
    public String id;
    public List<Channel> channels = new ArrayList<>();
    public boolean isStateRecorded = false;

    private List<Message> incomingMessages = new ArrayList<>();
    private List<Message> outgoingMessages = new ArrayList<>();

    public Node(String id) {
        this.id = id;
    }

    @Override
    public void run() {
        
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Node)) return false;

        Node tmp_o = (Node) obj;
        return tmp_o.id.equalsIgnoreCase(id);
    }
}
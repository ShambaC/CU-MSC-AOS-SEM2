import java.util.ArrayList;
import java.util.List;

class Event {
    public int counter;
    public int siteId;

    public Event() {
        this.counter = 0;
        this.siteId = 0;
    }

    public Event(int counter, int siteId) {
        this.counter = counter;
        this.siteId = siteId;
    }
}

class Node extends Thread {
    public int id;
    public List<Node> nodeList = new ArrayList<>();

    @Override
    public void run() {
        
    }
}

public class RAA {
    
}
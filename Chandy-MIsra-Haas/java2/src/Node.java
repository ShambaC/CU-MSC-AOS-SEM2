import java.util.ArrayList;
import java.util.List;

public class Node {
    
    public String nodeID;

    public List<Node> blockedByList = new ArrayList<>();
    public List<Node> blockingList = new ArrayList<>();

    public List<Node> sentMsgs = new ArrayList<>();
    public List<Node> recMsgs = new ArrayList<>();

    public boolean engagingQuery = true;

    public void query(Node node) {
        if (engagingQuery) {
            engagingQuery = false;
            if (blockedByList.size() > 0) {
                recMsgs.add(node);
                for (Node block : blockedByList) {
                    this.sentMsgs.add(block);
                }
                for (int i = 0; i < sentMsgs.size(); i++) {
                    Node msg = sentMsgs.get(i);
                    msg.query(this);
                }
            }
            else {
                return;
            }
        }
        else {
            node.reply(this);
        }
    }

    public void reply(Node node) {
        if (sentMsgs.contains(node)) {
            sentMsgs.remove(node);

            if (sentMsgs.isEmpty()) {
                if (!recMsgs.isEmpty()) {
                    for (Node block : recMsgs) {
                        block.reply(this);
                    }
                }
            }
        }
    }

    public Node(String nodeID) {
        this.nodeID = nodeID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Node)) {
            return false;
        }

        Node obj_t = (Node) obj;
        return this.nodeID.equalsIgnoreCase(obj_t.nodeID);
    }
}

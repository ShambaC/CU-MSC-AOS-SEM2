package graph;

import java.util.ArrayList;
import java.util.List;

public class Resource {
    
    public int resourceID;

    public Node currentHeldNode = null;
    public List<Node> waitingNodes = new ArrayList<>();

    public Resource(int resourceID) {
        this.resourceID = resourceID;
    }

    public boolean request(Node node) {
        if (node == null) {
            return false;
        }

        if (currentHeldNode == null) {
            currentHeldNode = node;
            return true;
        }
        else {
            waitingNodes.add(node);
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)    return false;
        if (!(obj instanceof Resource)) return false;

        Resource obj_t = (Resource) obj;
        return this.resourceID == obj_t.resourceID;
    }
}

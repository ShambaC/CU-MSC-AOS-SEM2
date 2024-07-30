package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Class 
 */
public class Node implements Runnable {
    
    public int nodeID;

    public List<Resource> heldResources = new ArrayList<>();
    public List<Resource> requestedResources = new ArrayList<>();

    private List<Resource> globalResourceList = new ArrayList<>();

    public Node(int nodeID) {
        this.nodeID = nodeID;
    }

    public void setResourceList(List<Resource> globalResourceList) {
        this.globalResourceList = globalResourceList;
    }

    @Override
    public void run() {
        // TODO: Randomly choose resources to request
        // Call the request method of a resource object
        // Add no timers
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)    return false;
        if (!(obj instanceof Node)) return false;

        Node obj_t = (Node) obj;
        return this.nodeID == obj_t.nodeID;
    }
}

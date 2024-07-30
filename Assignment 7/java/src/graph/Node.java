package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a node or a process
 */
public class Node implements Runnable {
    
    public int nodeID;

    /**
     * A list of all resources that this node is currently holding
     */
    public List<Resource> heldResources = new ArrayList<>();
    /**
     * A list of all resources this node is currently requesting
     */
    public List<Resource> requestedResources = new ArrayList<>();

    /**
     * A list of all globally available resources
     */
    private List<Resource> globalResourceList = new ArrayList<>();

    /**
     * Constructor to initialise a node with an ID
     * @param nodeID
     */
    public Node(int nodeID) {
        this.nodeID = nodeID;
    }

    /**
     * A method to set the global resource list
     * @param globalResourceList
     */
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

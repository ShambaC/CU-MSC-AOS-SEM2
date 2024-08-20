package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a node or a process
 */
public class Node{
    
    public int nodeID;
    public int siteID;

    /**
     * A list of all resources that this node is currently holding
     */
    public List<Resource> heldResources = new ArrayList<>();
    /**
     * A list of all resources this node is currently requesting
     */
    public List<Resource> requestedResources = new ArrayList<>();
    


    /**
     * Constructor to initialise a node with an ID
     * @param nodeID
     * @param siteID
     */
    public Node(int nodeID, int siteID) {
        this.nodeID = nodeID;
        this.siteID = siteID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)    return false;
        if (!(obj instanceof Node)) return false;

        Node obj_t = (Node) obj;
        return (this.nodeID == obj_t.nodeID && this.siteID == obj_t.siteID);
    }
}

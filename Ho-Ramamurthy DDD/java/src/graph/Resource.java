package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a resource that a process needs to work in a system
 */
public class Resource {
    
    /**
     * The resources ID
     */
    public int resourceID;
    public int siteID;

    /**
     * The node that is currently holding the process and blocking others from accessing this
     */
    public Node currentHeldNode = null;
    /**
     * A list of nodes that have requested access for this resource
     */
    public List<Node> waitingNodes = new ArrayList<>();

    /**
     * Constructor to initialise a resource with an ID
     * @param resourceID
     */
    public Resource(int resourceID, int siteID) {
        this.resourceID = resourceID;
        this.siteID = siteID;
    }

    /**
     * A method to request a resource by a node/process
     * @param node The node requesting for this resource
     * @return the result, whether the requesting node was assigned this resource
     */
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
        return (this.resourceID == obj_t.resourceID && this.siteID == obj_t.siteID);
    }
}

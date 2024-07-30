package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import statTableRows.processRow;
import statTableRows.resourceRow;

public class Site {

    public int siteID;
    
    public List<Node> nodeList = new ArrayList<>();
    public List<Resource> resourceList = new ArrayList<>();

    /**
     * The resource status table
     * <p>
     * Has the following columns
     * <ul>
     * <li>ResourceID</li>
     * <li>Holder</li>
     * <li>Wait Queue</li>
     * </ul>
     * 
     */
    public Map<Resource, resourceRow> RST = new HashMap<>();

    /**
     * The process status table
     * <p>
     * Has the following columns
     * <ul>
     * <li>ProcessID</li>
     * <li>Resources Held</li>
     * <li>Resources Requested</li>
     * </ul>
     * 
     */
    public Map<Node, processRow> PST = new HashMap<>();

    public Site(int siteID) {
        this.siteID = siteID;
    }

    private void createRST() {

        for (Resource resource : resourceList) {
            resourceRow data = new resourceRow(resource.currentHeldNode, resource.waitingNodes);
            RST.put(resource, data);
        }
    }

    private void createPST() {
        
        for (Node node : nodeList) {
            processRow data = new processRow(node.heldResources, node.requestedResources);
            PST.put(node, data);
        }
    }

    public void generateData() {
        createPST();
        createRST();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)    return false;
        if (!(obj instanceof Site)) return false;

        Site obj_t = (Site) obj;
        return this.siteID == obj_t.siteID;
    }
}

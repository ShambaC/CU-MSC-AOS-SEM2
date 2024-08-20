package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import statTableRows.processRow;
import statTableRows.resourceRow;

/**
 * A class representing a site.
 * <p>
 * It is a collection of nodes and resources
 */
public class Site implements Runnable{

    /**
     * The site ID
     */
    public int siteID;
    
    public List<Site> siteList = new ArrayList<>();
    
    /**
     * The list of nodes in the site
     */
    public List<Node> nodeList = new ArrayList<>();
    /**
     * The list of resources in the site
     */
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

    /**
     * A method to generate the Resource Status Table for the resources on the site
     */
    private void createRST() {

        for (Resource resource : resourceList) {
            resourceRow data = new resourceRow(resource.currentHeldNode, resource.waitingNodes);
            RST.put(resource, data);
        }
    }

    /**
     * A method to generate the Process Status Table for the processses/nodes on the site
     */
    private void createPST() {
        
        for (Node node : nodeList) {
            processRow data = new processRow(node.heldResources, node.requestedResources);
            PST.put(node, data);
        }
    }

    /**
     * Public method that generates the required data. Called by the controller
     */
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

	@Override
	public void run() {
    	Random random = new Random();

        while(true) {
            synchronized(this) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
    
                Node nodeSelect = this.nodeList.get(random.nextInt(this.nodeList.size()));
                Resource res = this.resourceList.get(random.nextInt(this.resourceList.size()));
    
                if (nodeSelect.heldResources.contains(res) || nodeSelect.requestedResources.contains(res)) {
                    continue;
                }
                    
                boolean result = res.request(nodeSelect);
                System.out.println("\n[Site " + this.siteID + "] Node " + nodeSelect.nodeID + " sent request to Resource " + res.resourceID);
                if (result) {
                    nodeSelect.heldResources.add(res);
                    System.out.println("\n[Site " + this.siteID + "] Node " + nodeSelect.nodeID + " is now holding Resource " + res.resourceID);
                }
                else {
                    nodeSelect.requestedResources.add(res);
                    System.out.println("\n[Site " + this.siteID + "] Node " + nodeSelect.nodeID + " is now waiting for Resource " + res.resourceID);
                }
            }
        }
	}
}

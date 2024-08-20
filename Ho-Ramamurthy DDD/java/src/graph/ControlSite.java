package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import statTableRows.resourceRow;

/**
 * A custom class defining a wait for graph data structure.
 * <p>
 * It is essentially an adjacency list of nodes that define a directed graph. It additionally has methods that are useful for a WFG.
 */
class WaitForGraph {
    /**
     * The adjacency list storing the directed graph
     */
    public Map<Node, List<Node>> adjList = new HashMap<>();

    /**
     * Wrapper method to get the list of child nodes from the adjacency list
     * @param node The node whose list is needed
     * @return List of children for the given node
     */
    public List<Node> get(Node node) {
        return adjList.get(node);
    }

    /**
     * Method to add a single node as a child to a given parent node
     * @param node  Parent node
     * @param child Child node ot be appended to the adjacency list of the parent node
     */
    public void add(Node node, Node child) {

        if (adjList.get(node) == null) {
            adjList.put(node, new ArrayList<Node>());
        }

        List<Node> children = adjList.get(node);
        children.add(child);

        adjList.put(node, children);
    }

    /**
     * Method to put a whole adjacency list for a parent node
     * @param node      Parent node
     * @param children  List of children nodes
     */
    public void put(Node node, List<Node> children) {
        adjList.put(node, children);
    }


    /**
     * Method to get the keys or nodes in the graph as a Set
     * @return A node Set of the nodes in the graph
     */
    public Set<Node> keySet() {
        return adjList.keySet();
    }

    /**
     * A Method to remove any duplicate nodes as child of the nodes in the graph.
     */
    public void removeDuplicate() {
        for (Node node : adjList.keySet()) {
            List<Node> children = adjList.get(node);

            if (children == null) {
                continue;
            }

            Set<Node> set = new LinkedHashSet<>();
            set.addAll(children);

            children.clear();
            
            children.addAll(set);
            adjList.put(node, children);
        }
    }

    /**
     * A method that checks if a graph has a cycle using DFS
     * @param node      Current node in DFS traversal
     * @param visited   A map marking the visited nodes in the graph
     * @param recStack  A map marking the nodes that are in the recursion stack
     * @return          Returns whether there is a cycle in the given graph
     */
    private boolean hasCycleUtil(Node node, Map<Node, Boolean> visited, Map<Node, Boolean> recStack) {
        
        if (recStack.get(node)) {
            return true;
        }

        if (visited.get(node)) {
            return false;
        }

        // Mark the current node as visited and a part of the recursion stack
        visited.put(node, true);
        recStack.put(node, true);

        List<Node> children = adjList.get(node);

        if (children != null) {
            for (Node child : children) {

                if (hasCycleUtil(child, visited, recStack)) {
                    return true;
                }
            }
        }

        recStack.put(node, false);

        return false;
    }

    /**
     * Method to traverse through all graphs in a forest to check for cycles using the util method
     * @return whether the forest has a cycle
     */
    public boolean hasCycle() {

        Map<Node, Boolean> visited = new HashMap<>();
        Map<Node, Boolean> recStack = new HashMap<>();

        for (Node key : adjList.keySet()) {
            visited.put(key, false);
            recStack.put(key, false);
        }

        for (Node key : adjList.keySet()) {
            if (hasCycleUtil(key, visited, recStack)) {
                return true;
            }
        }

        return false;
    }
}

/**
 * A class representing the control site or the controller of the system.
 */
public class ControlSite {
    
    /**
     * List of all sites in the system
     */
    public List<Site> siteList = new ArrayList<>();

    /**
     * A Map that maps all the sites to their corresponding local Wait for Graph
     */
    public Map<Site, WaitForGraph> LWFGMap = new HashMap<>();
    /**
     * The global wait for graph for the system
     */
    public WaitForGraph GWFG;

    /**
     * The constrcutor for control site the assigns the list of sites in the system
     * @param siteList The list of sites in the system
     */
    public ControlSite(List<Site> siteList) {
        this.siteList = siteList;
    }

    /**
     * Method to create the Local Wait For Graph for all the sites
     */
    public void createLWFG() {

        // Iterate through each site
        for (Site site : siteList) {

            site.generateData();

            WaitForGraph localGraph = new WaitForGraph();
            // Initialise the graph
            for (Node node : site.nodeList) {
                localGraph.add(node, null);
            }

            // Iterate through the resource status table
            for (Resource resource : site.RST.keySet()) {
                // Get the data for each resource
                resourceRow data = site.RST.get(resource);

                for (Node child : data.waitList) {
                    localGraph.add(data.currNode, child);
                }
            }

            localGraph.removeDuplicate();
            LWFGMap.put(site, localGraph);
        }
    }

    /**
     * Method to create the Global Wait For Graph
     */
    public void createGWFG() {
        
        GWFG = new WaitForGraph();
        
        for (Site site : LWFGMap.keySet()) {
            
            WaitForGraph localGraph = LWFGMap.get(site);

            for (Node node : localGraph.keySet()) {
                List<Node> children = localGraph.get(node);

                for (Node child : children) {
                    GWFG.add(node, child);
                }
            }
        }

        GWFG.removeDuplicate();
    }

    /**
     * Method that returns whether there is a deadlock in the system
     * @return the status
     */
    public boolean isInDeadlock() {
        return GWFG.hasCycle();
    }
}

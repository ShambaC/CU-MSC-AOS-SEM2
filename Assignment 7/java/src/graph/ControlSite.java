package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import statTableRows.resourceRow;

class WaitForGraph {
    public Map<Node, List<Node>> adjList = new HashMap<>();

    public List<Node> get(Node node) {
        return adjList.get(node);
    }

    public void add(Node node, Node child) {

        if (adjList.get(node) == null) {
            adjList.put(node, new ArrayList<Node>());
        }

        List<Node> children = adjList.get(node);
        children.add(child);

        adjList.put(node, children);
    }

    public void put(Node node, List<Node> children) {
        adjList.put(node, children);
    }

    public Set<Node> keySet() {
        return adjList.keySet();
    }

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

public class ControlSite {
    
    public List<Site> siteList = new ArrayList<>();

    public Map<Site, WaitForGraph> LWFGMap = new HashMap<>();
    public WaitForGraph GWFG;

    public ControlSite(List<Site> siteList) {
        this.siteList = siteList;
    }

    public void createLWFG() {

        for (Site site : siteList) {

            WaitForGraph localGraph = new WaitForGraph();
            for (Node node : site.nodeList) {
                localGraph.add(node, null);
            }

            for (Resource resource : site.RST.keySet()) {
                resourceRow data = site.RST.get(resource);

                for (Node child : data.waitList) {
                    localGraph.add(data.currNode, child);
                }
            }

            localGraph.removeDuplicate();
            LWFGMap.put(site, localGraph);
        }
    }

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

    public boolean isInDeadlock() {
        return GWFG.hasCycle();
    }
}

package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import statTableRows.processRow;
import statTableRows.resourceRow;

public class Site {
    
    public List<Node> nodeList = new ArrayList<>();
    public List<Resource> resourceList = new ArrayList<>();

    public Map<Resource, resourceRow> RST = new HashMap<>();
    public Map<Node, processRow> PST = new HashMap<>();
}

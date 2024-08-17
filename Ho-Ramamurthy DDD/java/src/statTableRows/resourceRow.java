package statTableRows;

import java.util.ArrayList;
import java.util.List;

import graph.Node;

public class resourceRow {

    public Node currNode;
    public List<Node> waitList = new ArrayList<>();

    public resourceRow(Node currNode, List<Node> waitList) {
        this.currNode = currNode;
        this.waitList = waitList;
    }
}

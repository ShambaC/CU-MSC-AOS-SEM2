public class Node {
 
    private String id;
    private Node nextNode;
 
    public Node(String id) {
        this.id = id;
        this.nextNode = null;
    }
 
    public void setNextNode(Node node) {
        this.nextNode = node;
    }
}
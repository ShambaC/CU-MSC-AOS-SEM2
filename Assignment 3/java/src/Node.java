public class Node {
 
    public String id;
    public boolean isPHold;
    private Node nextNode;
 
    public Node(String id) {
        this.id = id;
        this.isPHold = false;
        this.nextNode = null;
    }
 
    public void setNextNode(Node node) {
        this.nextNode = node;
    }
    public Node getNextNode() {
        return this.nextNode;
    }
}
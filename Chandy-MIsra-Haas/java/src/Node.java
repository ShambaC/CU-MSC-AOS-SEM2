import java.util.ArrayList;
import java.util.List;

public class Node {
	
	public int nodeID;
	public List<Node> blockedBy = new ArrayList<>();
	public List<Node> blocking = new ArrayList<>();
	public Boolean probeMessage;
	
	public Node(int nodeID) {
		this.nodeID = nodeID;
	}
	
	public void reply(Node node) {
		node.probeMessage = true;
	}
	
}

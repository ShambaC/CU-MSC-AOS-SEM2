import java.util.List;

import javax.swing.JOptionPane;

public class Deadlock {
	
	//finding the node with minimum indegree for selecting initiator node
	public Node selectInitiator(List<Node> nodeList) {
		int min = Integer.MAX_VALUE, currLen;
		Node minNode = null;
		
		for (Node node : nodeList) {
			currLen = node.blocking.size();
			if(currLen == 0) {
				return node;
			}
			else {
				if(currLen < min) {
					min = currLen;
					minNode = node;
				}
			}
		}
		return minNode;
	}
	
	//deadlock is detected if the probe message for the initiator node is true
	public void deadlock(Node initNode) {
		Node currNode = initNode;
		currNode.probeMessage = false;
		
		while(!currNode.blockedBy.isEmpty()) {
			currNode = currNode.blockedBy.remove(0);
		}
		while(!currNode.blocking.isEmpty() && !currNode.blockedBy.isEmpty()) {
			currNode.reply(currNode.blocking.get(0));
			currNode = currNode.blocking.remove(0);
		}
		
		if(initNode.probeMessage) {
			JOptionPane.showMessageDialog(null, "DeadLock", "Information", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(null, "No DeadLock", "Information", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}

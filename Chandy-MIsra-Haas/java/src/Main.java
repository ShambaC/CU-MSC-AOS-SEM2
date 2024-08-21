import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	//Contains all the nodes
	public static List<Node> nodeList = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		File file = new File("F:\\College Crap\\AOS\\Practical\\Chandy-MIsra-Haas\\java\\src\\input.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		StringBuffer sb = new StringBuffer();
		
		String lines;
		while((lines = br.readLine()) != null) {
			sb.append(lines);
			sb.append("\n");
		}
		
		// The first line of the file contains no. of nodes
		int nodesNo = Integer.parseInt(Character.toString(sb.charAt(0)));
		
		//Creating nodes for each index
		for (int i = 1; i <= nodesNo; i++) {
			Node node = new Node(i);
			nodeList.add(node);
		}
		
		//removing white-space
		String[] tokens = sb.toString().split("\\s+");
		
		/* Node Blocked   |   Blocked By  
		 *    Node A      |     Node B    */
        for (int i = 1; i < tokens.length; i += 2) {
            if (tokens.length > i + 1) {
                Node nodeA = nodeList.get(Integer.parseInt(tokens[i]) - 1);
                Node nodeB = nodeList.get(Integer.parseInt(tokens[i + 1]) - 1);
                
//                System.out.println("Blocked :"+nodeA.nodeID+", Blocked By :"+nodeB.nodeID);

                nodeA.blockedBy.add(nodeB);
                nodeB.blocking.add(nodeA);
            }
        }
        
        //Creating deadlock class's object
        Deadlock dl = new Deadlock();
        
        //finding initiator node
        Node initNode = dl.selectInitiator(nodeList);

        //finding if deadlock exists or not
		dl.deadlock(initNode);
	}

}

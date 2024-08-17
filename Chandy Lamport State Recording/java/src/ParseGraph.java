// import java.lang.classfile.components.ClassPrinter;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * Utility class to parse the graph and find out the starter node
 */
public class ParseGraph {

    /**
     * Static method to find an eligible starter node
     * @param graph The graph to be parsed
     * @return  first eligible starter node in the graph
     */
    public static Node findStarter(List<Node> graph) {

        Node starterNode = null;

        // Iterate over each nodes to check for starter
    	for(int i = 0; i < graph.size(); i++){
            boolean flag = false;

            Queue<Node> queue = new ArrayDeque<>();
            queue.add(graph.get(i));

            // Run BFS to check if a node can reach every other node
            bfs(graph.get(i), queue);

            for(int j = 0; j < graph.size(); j++){
                if (!graph.get(j).visited){
                    flag = true;
                    break;
                }
            }

            // Found starter node
            if(!flag){
                starterNode =  graph.get(i);
                break;
            }

            // Reset all node data
            for (int j = 0; j < graph.size(); j++){
                Node item = graph.get(j);
                item.visited = false;
            }

        }
        return starterNode;
    }


    /**
     * Method to do Breadth First Searching with a given node
     * @param i Node to check for reachability
     * @param queue The BFS queue
     */
    public static void bfs(Node i, Queue<Node> queue){
        i.visited = true;

        while(!queue.isEmpty()){
            Node start = queue.poll();
            int outChannelSize = start.outgoingChannels.size();

            for(int j = 0; j < outChannelSize; j++){
                Channel adjChannel = start.outgoingChannels.get(j);
                if (!adjChannel.nodeB.visited){
                    queue.add(adjChannel.nodeB);
                    adjChannel.nodeB.visited = true;
                }
            }
        }
    }
}

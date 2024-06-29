// import java.lang.classfile.components.ClassPrinter;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class ParseGraph {

    public static Node findStarter(List<Node> graph) {

        Node starterNode = null;

    	for(int i = 0; i < graph.size(); i++){
            boolean flag = false;

            Queue<Node> queue = new ArrayDeque<>();
            queue.add(graph.get(i));

            bfs(graph.get(i), queue);

            for(int j = 0; j < graph.size(); j++){
                if (!graph.get(j).visited){
                    flag = true;
                    break;
                }
            }

            if(!flag){
                starterNode =  graph.get(i);
                break;
            }

            for (int j = 0; j < graph.size(); j++){
                Node item = graph.get(j);
                item.visited = false;
            }

        }
        return starterNode;
    }


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

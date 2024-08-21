import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.swing.JFileChooser;

public class Main {

    public static Map<String, Boolean> BFS(String start, Map<String, List<String>> graph) {

        Map<String, Boolean> visited = new HashMap<>();
        for (String key : graph.keySet()) {
            visited.put(key, false);
        }

        Queue<String> queue = new ArrayDeque<>();
        queue.addAll(graph.get(start));

        visited.put(start, true);

        while(!queue.isEmpty()) {
            String node = queue.poll();
            visited.put(node, true);
            
            for (String child : graph.get(node)) {
                if (!visited.get(child)) {
                    queue.add(child);
                    visited.put(child, true);
                }
            }
        }

        return visited;
    }

    public static void main(String[] args) {
        Map<String, List<String>> adjList = new HashMap<>();

        JFileChooser fc = new JFileChooser("./");
        int res = fc.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            try {

                String content = new String(Files.readAllBytes(fc.getSelectedFile().toPath()));
                String[] lines = content.split("\\n");

                // First line strictly needs to be the number of vertices
                int num = Integer.parseInt(lines[0].trim());

                for (int i = 1; i < lines.length; i++) {
                    if (lines[i].isBlank() || lines[i].startsWith("#")) {
                        continue;
                    }

                    String vertex1 = lines[i].split(" ")[0].trim();
                    String vertex2 = lines[i].split(" ")[1].trim();

                    if (adjList.keySet().contains(vertex1)) {
                        List<String> children = adjList.get(vertex1);
                        
                        if (!children.contains(vertex2)) {
                            children.add(vertex2);
                        }
                    }
                    else {
                        List<String> children = new ArrayList<>();
                        children.add(vertex2);

                        adjList.put(vertex1, children);
                    }

                    if (!adjList.keySet().contains(vertex2)) {
                        List<String> children = new ArrayList<>();

                        adjList.put(vertex2, children);
                    }
                }

                // ---- GRAPH READY NOW -----
                for (String key : adjList.keySet()) {
                    Map<String, Boolean> visited = BFS(key, adjList);

                    boolean flag = false;

                    for (String node : visited.keySet()) {
                        if (!visited.get(node)) {
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        System.out.println("Node " + key + " cannot be an initiator.");
                    }
                    else {
                        System.out.println("Node " + key + " can be an initiator.");
                    }
                }

                System.exit(0);

                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.exit(1);
        }
    }
}
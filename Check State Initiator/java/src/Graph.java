import java.io.*;
import java.util.Arrays;

public class Graph {
    private List[] adjList;
    private boolean[] visited;
    private int[] queue;
    private int front;
    private int rear;
    private int vertices;

    public Graph(int vertices) {
        this.vertices = vertices;
        adjList = new List[vertices];
        visited = new boolean[vertices];
        queue = new int[vertices];
        front = 0;
        rear = -1;

        for (int i = 0; i < vertices; i++) {
            adjList[i] = new List();
            adjList[i].head = new Node(i);
        }
    }

    private Node createNode(int vertex) {
        return new Node(vertex);
    }

    public void addEdge(int src, int dst) {
        Node tmp = adjList[src].head;
        while (tmp.next != null) {
            tmp = tmp.next;
        }
        Node dstNode = createNode(dst);
        tmp.next = dstNode;
        adjList[src].outDegree++;
    }

    private void bfs(int start) {
        Arrays.fill(visited, false); // Reset visited array
        front = 0;
        rear = -1;
        queue[++rear] = start;
        visited[start] = true;

        while (front <= rear) {
            int current = queue[front++];
            Node listHead = adjList[current].head;

            while (listHead != null) {
                if (!visited[listHead.vertex]) {
                    queue[++rear] = listHead.vertex;
                    visited[listHead.vertex] = true;
                }
                listHead = listHead.next;
            }
            front++;
        }
    }

    public void processFile(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            // Read the number of vertices
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }
                vertices = Integer.parseInt(line.trim());
                break;
            }

            adjList = new List[vertices];
            for (int i = 0; i < vertices; i++) {
                adjList[i] = new List();
                adjList[i].head = new Node(i);
            }

            // Parse edges
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }
                String[] tokens = line.trim().split(" ");
                if (tokens.length != 2) {
                    System.err.println("Invalid edge format in file.");
                    continue;
                }

                int src = parseVertex(tokens[0]);
                int dst = parseVertex(tokens[1]);

                if (src == -1 || dst == -1) {
                    System.err.println("Invalid vertex label in file.");
                    continue;
                }

                addEdge(src, dst);
            }

            // Output results
            //try (PrintWriter writer = new PrintWriter(new FileWriter("outputLog.txt"))) {
                System.out.println("All edges added successfully!");

                for (int i = 0; i < vertices; i++) {
                    bfs(i);

                    boolean isConnected = true;
                    for (boolean v : visited) {
                        if (!v) {
                            isConnected = false;
                            break;
                        }
                    }

                    char vertexLabel = (char) (i + 'A'); 
                    if (isConnected) {
                        System.out.println(vertexLabel + " node can be used as an initiator!");
                    } else {
                        System.out.println(vertexLabel + " node cannot be used as an initiator.");
                    }
                }
            }
        }
    

    private int parseVertex(String vertexLabel) {
        if (vertexLabel.length() != 1) {
            return -1; // Invalid format
        }
        char ch = vertexLabel.charAt(0);
        if (ch >= 'A' && ch <= 'Z') {
            return ch - 'A'; // Handling A-Z
        }
        return -1; // Invalid vertex label
    }
}

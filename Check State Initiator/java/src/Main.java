
import java.io.IOException;


public class Main {


	
	    public static void main(String[] args) {
	        String fileName = "file.txt"; 

	        Graph graph = new Graph(100); // Initialize with a large enough number of vertices
	        try {
	            graph.processFile(fileName);
	        } catch (IOException e) {
	            System.err.println("Error reading the file: " + e.getMessage());
	        }
	    }
	}



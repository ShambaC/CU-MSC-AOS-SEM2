#include<iostream>
#include<fstream>
#include<cctype>
#include<string.h>

using namespace std;

/**
 * Struct for a Node in the graph.
 * Stores the vertex value and the pointer to the next node in the adjacency list.
*/
typedef struct Node {
    int vertex;
    struct Node* next;
} Node;

/**
 * Struct to store all the unique nodes of the graph as head to their adjacency list
 * ALso stores the outdegree of each node.
 * <i> The outdegree is calculated when adding edges. </i>
*/
typedef struct List {
    Node* head;
    int outDegree;
} List;

/**
 * Method to create a node by allocating space and initializing it with the vertex label/value
 * @param vertex The vertex value/label for the node
 * @returns The pointer to the node
*/
Node* createNode(int vertex) {
    Node* node = (Node*) malloc(sizeof(Node));

    node -> vertex = vertex;
    node -> next = NULL;

    return node;
}

/**
 * Method to add edge to the graph
 * @param adjList The array containing all the head pointers to all the individual nodes
 * @param src The source vertex from which the edge starts
 * @param dst The destination vertex to which the edge points
*/
void addEdge(List* adjList[], int src, int dst) {

    Node* tmp = adjList[src] -> head;
    while(tmp -> next != NULL) {
        tmp = tmp -> next;
    }

    Node* dstNode = createNode(dst);
    tmp -> next = dstNode;

    adjList[src] -> outDegree++;
}

/**
 * An utility method to display the adjacency matrix.
 * <i> Currently unused </i>
 * @param adjList The array containing all the head pointers to all the individual nodes
 * @param size The size of the headlist or the number of vertices in the graph
*/
void DisplayAdjList(List* adjList[], int size) {
    for(int i = 0; i < size; i++) {
        Node* iterator = adjList[i] -> head;
        while(iterator != NULL) {
            printf("%d", iterator -> vertex);
            if(iterator -> next != NULL) {
                printf("->");
            }
            iterator = iterator -> next;
        }
        printf("\n");
    }
}

/**
 * The front marker for the BFS queue
*/
int front = 0;
/**
 * The rear marker for the BFS queue
*/
int rear = -1;

/**
 * This method traverses the graph in Breadth First Search manner.
 * This helps in checking if a whole graph can be traversed starting from a particular vertex.
 * @param adjList The array containing all the head pointers to all the individual nodes
 * @param start The node to be checked for initiation
 * @param visited The boolean array keeping track of nodes that have already been parsed.
 * @param queue The BFS queue that stores the vertices as the traversal occurs
*/
void BFS(List* adjList[], int start, bool visited[], int queue[]) {
    
    // Add the current vertext to the end of the queue
    queue[++rear] = adjList[start] -> head -> vertex;

    // WHile queue is not empty
    while(front <= rear) {
        // Get all the adjacent connected vertices and add them to the BFS queue
        Node* listHead = adjList[queue[front]] -> head;
        visited[listHead -> vertex] = true;

        while(listHead != NULL) {
            if(!visited[listHead -> vertex]) {
                queue[++rear] = listHead -> vertex;
                visited[listHead -> vertex] = true;
            }

            listHead = listHead -> next;
        }
        front++;
    }
}


/**
 * Main method for running the program
 * @param argc The number of arguments passed when starting the execution
 * @param argv The arguments
*/
int main(int argc, char const *argv[])
{
    // Show program usage
    if (argc != 2) {
        cout << "Program usage: init_loop help | init_loop <filename>";
        return -1;
    }

    string helpText = "help";

    // Show the help text
    if (helpText.compare(argv[1]) == 0) {
        cout << endl << endl;
        cout << "This is a command line only program. The built exe cannot be run on its own.\n";
        cout << "To the run the program, type in terminal: {program Name | default: 'init_loop'} <filename.withExtension>\n";
        cout << "The program reads its required data from the given file.\n";
        cout << "Ensure that the provided file is a text file.\n";
        cout << "The file to be provided should be in the following format.\n";
        cout << "1. Can have blank lines. \n2. Lines starting with '#' will be skipped as comments. \n3. First line with content has to be the number of vertices.\n";
        cout << "4. Following contents of lines should include the edges with one edge in each line.\n";
        cout << "5. The edge should be in the format <source><space><destination>\n";
        cout << "Restriction is that the vertex label can be of only one of the three types at once: \n";
        cout << "1. 1-10\n2. A-Z\n3. a-z\n";
        cout << "After file parsing, all nodes will be checked for eligibility.\n\n";
        return 0;
    }

    // Get the filename from the command line argument
    string fileName = argv[1];
    // string fileName = "file.txt";
    // Create input stream for the file
    ifstream inputFile(fileName);

    // Check if the file exists by opening it
    if(!inputFile.is_open()) {
        cerr << "Error in opening the file";
        return -1;
    }

    int vertices;

    // Read line by line to first parse the number of vertices
    string line;
    while(getline(inputFile, line)) {
        if (line[0] == '#' || isspace(line[0]) || line.empty()) {
            continue;
        }

        vertices = stoi(line);
        break;
    }

    // Initialize the required variables
    List* adjList[vertices];
    bool visited[vertices];
    int queue[vertices];

    for (int i = 0; i < vertices; i++) {
        adjList[i] = (List*) malloc(sizeof(List));
        adjList[i] -> head = createNode(i);
        adjList[i] -> outDegree = 0;

        visited[i] = false;
    }

    /**
     * Stores the type and incrementing of the labels used in the graph
     * 49 => 1-10
     * 65 => A-Z
     * 97 => a-z
    */
    int labelTypeIncrement = 0;

    // parse the file to get the edges
    while(getline(inputFile, line)) {
        if (line[0] == '#' || isspace(line[0]) || line.empty()) {
            continue;
        }

        string token = strtok(line.data(), " ");
        string Source = token;
        token = strtok(NULL, " ");
        string Dest = token;

        int src = Source[0];
        int dst = Dest[0];

        // label 1-10
        if (src > 48 && src < 58) {
            if (dst > 57) {
                cout << "Do not use both digits and characters as labels.";
                return -1;
            }
            else {
                src = stoi(Source) - 1;
                dst = stoi(Dest) - 1;
                if(labelTypeIncrement == 0)  labelTypeIncrement = 1;
            }
        }
        // label A-Z
        else if (src > 64 && src < 91) {
            if (dst < 65 || dst > 90) {
                cout << "Do not use both digits and different case characters as labels.";
                return -1;
            }
            else {
                src -= 65;
                dst -= 65;
                if(labelTypeIncrement == 0)  labelTypeIncrement = 65;
            }
        }
        // label a-z
        else if (src > 96 && src < 123) {
            if (dst < 97 || dst > 122) {
                cout << "Do not use both digits and different case characters as labels.";
                return -1;
            }
            else {
                src -= 97;
                dst -= 97;
                if(labelTypeIncrement == 0)  labelTypeIncrement = 97;
            }
        }
        else {
            cout << "Do not use labels other than the ones specified";
            return -1;
        }

        addEdge(adjList, src, dst);
    }

    cout << "All edges added successfully !" << endl;

    for (int i = 0; i < vertices; i++) {
        bool flag = false;

         // Perform BFS to check connectivity
        BFS(adjList, i, visited, queue);

        for (int j = 0; j < vertices; j++) {
            if (!visited[j]) {
                cout << (labelTypeIncrement == 1 ? i+1 : (char)(i + labelTypeIncrement)) << " node cannot be used as an initiator.\n";
                flag = true;
                break;
            }
        }

        if (!flag) {
            cout << (labelTypeIncrement == 1 ? i+1 : (char)(i + labelTypeIncrement)) << " node can be used as an initiator!\n";
        }

        // Reset
        for (int j = 0; j < vertices; j++) {
            visited[j] = false;
        }

        front = 0;
        rear = -1;
    }

    return 0;
}
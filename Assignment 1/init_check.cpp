#include<iostream>
#include<fstream>
#include<cctype>

using namespace std;

typedef struct Node {
    int vertex;
    struct Node* next;
} Node;

typedef struct List {
    Node* head;
    int outDegree;
} List;

Node* createNode(int vertex) {
    Node* node = (Node*) malloc(sizeof(Node));

    node -> vertex = vertex;
    node -> next = NULL;

    return node;
}

void addEdge(List* adjList[], int src, int dst) {

    Node* tmp = adjList[src] -> head;
    while(tmp -> next != NULL) {
        tmp = tmp -> next;
    }

    Node* dstNode = createNode(dst);
    tmp -> next = dstNode;

    adjList[src] -> outDegree++;
}

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

int front = 0;
int rear = -1;

void BFS(List* adjList[], int start, bool visited[], int queue[]) {
    
    queue[++rear] = adjList[start] -> head -> vertex;

    while(front <= rear) {
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

int main(int argc, char const *argv[])
{
    if (argc != 2) {
        cout << "Program usage: init help | init <filename>";
        return -1;
    }

    string helpText = "help";

    if (helpText.compare(argv[1]) == 0) {
        cout << "TODO FORMATED README";
        return 0;
    }

    string fileName = argv[1];
    ifstream inputFile(fileName);

    if(!inputFile.is_open()) {
        cerr << "Error in opening the file";
        return -1;
    }

    int vertices;

    string line;
    while(getline(inputFile, line)) {
        if (line[0] == '#' || isspace(line[0]) || line.empty()) {
            continue;
        }

        vertices = stoi(line);
        break;
    }

    List* adjList[vertices];
    bool visited[vertices];
    int queue[vertices];

    for (int i = 0; i < vertices; i++) {
        adjList[i] = (List*) malloc(sizeof(List));
        adjList[i] -> head = createNode(i);
        adjList[i] -> outDegree = 0;

        visited[i] = false;
    }

    while(getline(inputFile, line)) {
        if (line[0] == '#' || isspace(line[0]) || line.empty()) {
            continue;
        }

        int src = line[0];
        int dst = line[2];

        if (src > 48 && src < 58) {
            if (dst > 57) {
                cout << "Do not use both digits and characters as labels.";
                return -1;
            }
            else {
                src -= 49;
                dst -= 49;
            }
        }
        else if (src > 64 && src < 91) {
            if (dst < 65 || dst > 90) {
                cout << "Do not use both digits and different case characters as labels.";
                return -1;
            }
            else {
                src -= 65;
                dst -= 65;
            }
        }
        else if (src > 96 && src < 123) {
            if (dst < 97 || dst > 122) {
                cout << "Do not use both digits and different case characters as labels.";
                return -1;
            }
            else {
                src -= 97;
                dst -= 97;
            }
        }
        else {
            cout << "Do not use labels other than the ones specified";
            return -1;
        }

        addEdge(adjList, src, dst);
    }

    cout << "All edges added successfully !" << endl;

    int start;
    char node;
    cout << "Enter the node to check for initiator: ";
    cin >> node;

    start = node;

    if (start > 64 && start < 91) {
        start -= 65;
    }
    else if (start > 48 && start < 58) {
        start -= 49;
    }
    else if (start > 96 && start < 123) {
        start -= 97;
    }

    if (adjList[start] -> outDegree == 0) {
        cout << "This node cannot be used as an initiator.";
        return 0;
    }

    BFS(adjList, start, visited, queue);

    for (int i = 0; i < vertices; i++) {
        if (!visited[i]) {
            cout << "This node cannot be used as an initiator.";
            return 0;
        }
    }

    cout << "This node can be used as an initiator!";

    return 0;
}
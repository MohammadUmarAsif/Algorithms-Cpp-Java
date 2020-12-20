// Including required header files
#include <iostream>
#include <vector>
#include <chrono>
#include <random>
#include <regex>
#include <string>
#include <queue>
#include <climits>
#include <iomanip>
#include <stack>

// Including required entities from std namespace
using std::cout;
using std::cin;
using std::endl;
using std::vector;

// Function to generate a random flow network
void generate_random_network (vector<vector<int>> &adjacency_matrix, int vertices, int edges, int source, int sink) {
    
    // Setting random seed
    std::default_random_engine generator; 
    generator.seed(std::chrono::system_clock::now().time_since_epoch().count());
    
    std::uniform_int_distribution<int> vertice_set (0,vertices-1);
    std::uniform_int_distribution<int> edge_capacity_set (1,vertices*3);
    
    // Generating edges with capacities
    for(int i=0; i<edges; i++) {
        int u = vertice_set(generator);
        int v = vertice_set(generator);
            
        while(adjacency_matrix[u][v] != 0 || adjacency_matrix[v][u] != 0 || v == u || u == sink || v == source) {
            u = vertice_set(generator);
            v = vertice_set(generator);
        }
        
        int weight = edge_capacity_set(generator);
        
        adjacency_matrix[u][v] = weight;
    }
}

// Function to generate user-provided flow network
void generate_user_network (vector<vector<int>> &adjacency_matrix, int edges) {
    
    cout<<"Enter the edges:"<<endl;
    cout<<"[Format: vertexOne-vertexTwo=capacityOfEdge] (eg 0-2=10) [Vertex numbering starts from 0]"<<endl;
    
    // Obtaining edges with capacities
    for(int i=0; i<edges; i++) {
    
        std::regex pattern ("(\\d+)-(\\d+)=(\\d+)");
        std::smatch sm;
        
        std::string input;
        cin>>input;
        
        if (std::regex_match (input, sm, pattern)) {
            int source = stoi(sm.str(1));
            int destination = stoi(sm.str(2));
            int weight = stoi(sm.str(3));
                
            adjacency_matrix[source][destination] = weight;
        }
    }
}

// Function to perform Depth-First-Search recursively
void dfs (vector<vector<int>> &residual_graph, int u, int sink, vector<bool> &visited, vector<int> &parent) {
    
    // Mark vertex as visited
    visited[u] = true;

    if (u == sink)
        return;

    // Exploring adjacent vertices
    for(int i = 0; i < residual_graph.size(); i++) {

        // If neighbour not visited & residual flow is not 0
        if(visited[i] == false && residual_graph[u][i] > 0){
                
            parent[i] = u;
            dfs(residual_graph, i, sink, visited, parent);
        }
    }
}

// Function to call dfs function 
bool dfs_init (vector<vector<int>> &residual_graph, int source, int sink, vector<int> &parent) {

    // Marking all vertices as unvisited
    vector<bool> visited (residual_graph.size(), false);

    dfs(residual_graph, source, sink, visited, parent);

    // If augmenting path found, return true
    if (visited[sink])
        return true;

    return false;
}

// Function to perform Breadth-First-Search
bool bfs (vector<vector<int>> &residual_graph, int source, int sink, vector<int> &parent) {
    
    // Marking all vertices as unvisited
    vector<bool> visited (residual_graph.size(), false);
    
    // Queue to hold vertices to be explored    
    std::queue<int> to_explore;

    to_explore.push(source);
    visited[source] = true;

    while(!to_explore.empty()) {
        
        int u = to_explore.front();
        to_explore.pop();
        
        // Exploring adjacent vertices
        for(int i = 0; i < residual_graph.size(); i++) {

            // If neighbour not visited & residual flow is not 0
            if(visited[i] == false && residual_graph[u][i] > 0){
                
                // Mark vertex as visited & explore it
                to_explore.push(i);
                parent[i] = u;
                visited[i] = true;
            }
        }
    }
    
    // If augmenting path found, return true
    if(visited[sink])
        return true;
        
    return false;
}

// Function to implement the Ford-Fulkerson & Edmonds-Karp algorithms
int maximum_flow_algorithm (vector<vector<int>> &adjacency_matrix, int source, int sink, bool flag) {
    
    // Set max-flow to 0
    int maximum_flow = 0;
    
    vector<vector<int>> residual_graph;
    
    // Prepare a residual graph from the flow network
    for(int i = 0; i < adjacency_matrix.size(); i++) {
        vector<int> row;
        residual_graph.push_back(row);
        
        for(int j = 0; j < adjacency_matrix[i].size(); j++)
            residual_graph[i].push_back(adjacency_matrix[i][j]);
    }
    
    // Declare parent vector to keep track of augmenting path
    vector<int> parent (adjacency_matrix.size(), -1);
    
    cout<<"Initialized Maximum Flow to 0 and created Residual Graph"<<endl;
    cout<<"------------------------------------------------------------------"<<endl;

    // While augmenting path exists, following steps are performed
    while(flag?dfs_init(residual_graph, source, sink, parent):bfs(residual_graph, source, sink, parent)) {
        
        cout<<"Augmenting Path found using "<<(flag?"DFS:":"BFS:")<<endl;
        std::stack<vector<int>> paths;

        int bottle_neck = INT_MAX;
         
        // Obtain the bottleneck-capacity for the path
        int vertex = sink;
        while(vertex != source) {
            int u = parent[vertex];
  
            bottle_neck = std::min(bottle_neck, residual_graph[u][vertex]);
            
            vector<int> temp {u, vertex};
            paths.push(temp);

            vertex = u;
        }
        
        bool first = true;
        
        // Display the path
        while(!paths.empty()) {
            if(first) {
                cout<<paths.top()[0]<<"->"<<paths.top()[1];
                first = false;
            }
            else 
                cout<<"->"<<paths.top()[1];
            paths.pop();
        }
        
        cout<<endl<<"Bottleneck Capacity for this path = "<<bottle_neck<<endl;
        cout<<"------------------------------------------------------------------"<<endl;
        
        std::stack<vector<int>> edge_capacities;

        // Modify the flow along the path using bottleneck-capacity
        vertex = sink;
        while(vertex != source) {
            int u = parent[vertex];
            
            vector<int> temp {u, vertex, residual_graph[u][vertex], residual_graph[vertex][u]};
            edge_capacities.push(temp);
            
            // Subtract for forward edges & add for reverse edges
            residual_graph[u][vertex] -= bottle_neck;
            residual_graph[vertex][u] += bottle_neck;
            
            vertex = u;
        }
        
        cout<<"Updating flow in the edges (forward & reverse): "<<endl;

        // Display the modification in the flow for forward & reverse edges
        while(!edge_capacities.empty()) {
            cout<<edge_capacities.top()[0]<<"->"<<edge_capacities.top()[1]<<": "<<edge_capacities.top()[2]<<"-"<<bottle_neck<<"="<<residual_graph[edge_capacities.top()[0]][edge_capacities.top()[1]]<<endl;
            cout<<edge_capacities.top()[1]<<"->"<<edge_capacities.top()[0]<<": "<<edge_capacities.top()[3]<<"+"<<bottle_neck<<"="<<residual_graph[edge_capacities.top()[1]][edge_capacities.top()[0]]<<endl;
            edge_capacities.pop();
        }
        cout<<"------------------------------------------------------------------"<<endl;
        
        // Display the updated residual graph
        cout<<"Updated Residual Graph:"<<endl;
        for(int i = 0; i < residual_graph.size(); i++) {
            cout<<"|";
            for(int j = 0; j < residual_graph[i].size(); j++) {
                cout<<std::setw(4)<<residual_graph[i][j]<<"|";
            }
            cout<<endl;
        }
        cout<<"------------------------------------------------------------------"<<endl;

        // Add flow from the path to the total flow
        maximum_flow += bottle_neck;
    }
    
    return maximum_flow;
}

// Main function
int main() {
    
    cout<<"This is a C++ program to find Maximum Flow in a given Flow Network"<<endl;
    cout<<"=====Using Ford-Fulkerson Algorithm & Edmonds-Karp Algorithm======"<<endl;
    cout<<"=================================================================="<<endl;

    cout<<"Select an Input Method: "<<endl;
    cout<<"1. User Input"<<endl;
    cout<<"2. Random Input"<<endl;

    int option;
    cout<<"Your Selection: ";
    cin>>option;
    cout<<"=================================================================="<<endl;
    
    // Set values to default
    int vertices = 6;
    int edges = 12;
    int source = 0;
    int sink = 5;

    vector<vector<int>> network; 
    
    if (option == 1) {
        cout<<"Enter the number of vertices: ";
        cin>>vertices;
        cout<<"Enter the number of edges: ";
        cin>>edges;
        cout<<"Enter the source vertex [Vertex numbering starts from 0]: ";
        cin>>source;
        cout<<"Enter the sink vertex [Vertex numbering starts from 0]: ";
        cin>>sink;
        cout<<"=================================================================="<<endl;
        
        // Initialize adjacency matrix for network
        for(int i = 0; i < vertices; i++) {
            vector<int> row;
            network.push_back(row);
            
            for(int j = 0; j < vertices; j++)
                network[i].push_back(0);
        }

        generate_user_network(network, edges);   
    }
    else if (option == 2) {
        cout<<"Following are the Constraints:"<<endl;
        cout<<"------------------------------------------------------------------"<<endl;
        cout<<"Number of Vertices: "<<vertices<<endl;
        cout<<"Number of Edges: "<<edges<<endl;
        cout<<"Capacity Range: 1-"<<vertices*3<<endl;
        cout<<"=================================================================="<<endl;
        
        // Initialize adjacency matrix for network
        for(int i = 0; i < vertices; i++) {
            vector<int> row;
            network.push_back(row);
            
            for(int j = 0; j < vertices; j++)
                network[i].push_back(0);
        }
        
        generate_random_network(network, vertices, edges, source, sink);
    }

    // Displaying the network
    cout<<"Following is the Network:"<<endl;
    cout<<"------------------------------------------------------------------"<<endl;
    for(int i = 0; i < network.size(); i++) {
        cout<<"|";
        for(int j = 0; j < network[i].size(); j++) {
            cout<<std::setw(4)<<network[i][j]<<"|";
        }
        cout<<endl;
    }
    
    // Displaying max-flow from Ford-Fulkerson Method
    cout<<"=================================================================="<<endl;
    cout<<"FORD-FULKERSON METHOD"<<endl;
    cout<<"=================================================================="<<endl;
    int maximum_flow_ff = maximum_flow_algorithm(network, source, sink, true);
    cout<<"The maximum flow (using Ford-Fulkerson) for the given network is: "<<maximum_flow_ff<<endl;
    
    // Displaying max-flow from Edmonds-Karp Method
    cout<<"=================================================================="<<endl;
    cout<<"EDMONDS-KARP METHOD"<<endl;
    cout<<"=================================================================="<<endl;
    int maximum_flow_ek = maximum_flow_algorithm(network, source, sink, false);
    cout<<"The maximum flow (using Edmonds-Karp) for the given network is: "<<maximum_flow_ek<<endl;

    cout<<"=================================================================="<<endl;
}

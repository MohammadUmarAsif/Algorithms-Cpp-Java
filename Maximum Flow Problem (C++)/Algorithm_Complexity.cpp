// Including required header files
#include <iostream>
#include <vector>
#include <chrono>
#include <random>
#include <queue>
#include <climits>
#include <iomanip>

// Including required entities from std namespace
using std::cout;
using std::cin;
using std::endl;
using std::vector;
using std::chrono::duration_cast;
using std::chrono::high_resolution_clock;
using std::chrono::nanoseconds;

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

    // While augmenting path exists, following steps are performed
    while(flag?dfs_init(residual_graph, source, sink, parent):bfs(residual_graph, source, sink, parent)) {
        
        int bottle_neck = INT_MAX;
         
        // Obtain the bottleneck-capacity for the path
        int vertex = sink;
        while(vertex != source) {
            int u = parent[vertex];
  
            bottle_neck = std::min(bottle_neck, residual_graph[u][vertex]);
            
            vertex = u;
        }
        
        // Modify the flow along the path using bottleneck-capacity
        vertex = sink;
        while(vertex != source) {
            int u = parent[vertex];
            
            // Subtract for forward edges & add for reverse edges
            residual_graph[u][vertex] -= bottle_neck;
            residual_graph[vertex][u] += bottle_neck;
            
            vertex = u;
        }

        // Add flow from the path to the total flow
        maximum_flow += bottle_neck;
    }
    
    return maximum_flow;
}

// Main function
int main() {
    
    cout<<"===This is a C++ program to analyze the complexity of Maximum-Flow-Algorithms==="<<endl;
    cout<<"=====Execution time of Ford-Fulkerson & Edmonds-Karp Algorithms is measured====="<<endl;
    cout<<"================================================================================"<<endl;

    // Set values to default
    int vertices = 6;
    int edges = 12;
    int source = 0;
    int sink = 5;
     
    cout<<"Following are the Constraints:"<<endl;
    cout<<"--------------------------------------------------------------------------------"<<endl;
    cout<<"Number of Vertices: "<<vertices<<endl;
    cout<<"Number of Edges: "<<edges<<endl;
    cout<<"Source Vertex: "<<source<<endl;
    cout<<"Sink Vertex: "<<sink<<endl;
    cout<<"Capacity Range: 1-"<<vertices*3<<endl;
    cout<<"================================================================================"<<endl;
   
    float loops = 10;
    auto average_ff = 0.0;
    auto average_ek = 0.0;

	for(int i=0; i<loops; i++) {
	    
        // Initialize adjacency matrix for network
	    vector<vector<int>> network (vertices, vector<int> (vertices, 0));
	    generate_random_network(network, vertices, edges, source, sink);
	   
        // Displaying the network
	    cout<<"Network for Instance #"<<i+1<<":"<<endl;
	    cout<<"--------------------------------------------------------------------------------"<<endl;
	    for(int i = 0; i < network.size(); i++) {
	        cout<<"|";
	        for(int j = 0; j < network[i].size(); j++) {
	            cout<<std::setw(4)<<network[i][j]<<"|";
	        }
	        cout<<endl;
	    }
	    
        // Calculating execution time for Ford-Fulkerson Method
	    auto start_ff = high_resolution_clock::now(); 
	    int maximum_flow_ff = maximum_flow_algorithm(network, source, sink, true);
	    auto stop_ff = high_resolution_clock::now();

	    auto duration_ff = duration_cast<nanoseconds>(stop_ff - start_ff); 
	    
        // Displaying execution time for Ford-Fulkerson Method
	    cout<<"--------------------------------------------------------------------------------"<<endl;
	    cout<<"Ford-Fulkerson Execution Time = "<<duration_ff.count()<<" nanoseconds or "<<duration_ff.count()/1000.0<<" microseconds"<<endl;
	    cout<<"The maximum flow (using Ford-Fulkerson) for the given network is: "<<maximum_flow_ff<<endl;

        // Calculating execution time for Edmonds-Karp Method
	    auto start_ek = high_resolution_clock::now(); 
	    int maximum_flow_ek = maximum_flow_algorithm(network, source, sink, false);
	    auto stop_ek = high_resolution_clock::now();

	    auto duration_ek = duration_cast<nanoseconds>(stop_ek - start_ek); 
	    
        // Displaying execution time for Edmonds-Karp Method
	    cout<<"--------------------------------------------------------------------------------"<<endl;
	    cout<<"Edmonds-Karp Execution Time = "<<duration_ek.count()<<" nanoseconds or "<<duration_ek.count()/1000.0<<" microseconds"<<endl;
	    cout<<"The maximum flow (using Edmonds-Karp) for the given network is: "<<maximum_flow_ek<<endl;
	    cout<<"================================================================================"<<endl;

	    average_ff += duration_ff.count();
	    average_ek += duration_ek.count();
	}

	average_ff /= loops;
	average_ek /= loops;

    // Displaying average execution time for both methods
	cout<<"Ford-Fulkerson Average Execution Time = "<<average_ff<<" nanoseconds or "<<average_ff/1000.0<<" microseconds"<<endl;
	cout<<"Edmonds-Karp Average Execution Time = "<<average_ek<<" nanoseconds or "<<average_ek/1000.0<<" microseconds"<<endl;

    cout<<"================================================================================"<<endl;
}

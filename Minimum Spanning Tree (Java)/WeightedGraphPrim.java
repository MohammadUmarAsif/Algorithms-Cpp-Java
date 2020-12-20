import java.util.LinkedList;
import java.util.ArrayList;

// Weighted Graph for Prim's Algorithm
public class WeightedGraphPrim {
	
	// A vertex in the graph
	static class Vertex {
		Vertex p;
		double key;
		int id;
		
		// Constructor
		public Vertex(int id) {
			this.id = id;
		}
	}
	
	// An edge connecting 2 vertices
	static class Edge {
		Vertex source;
		Vertex destination;
		double weight;
		
		// Constructor
		public Edge(Vertex source, Vertex destination, double weight) {
			this.source = source;
			this.destination = destination;
			this.weight = weight;
		}	
	}
	
	int total;
	LinkedList<Edge> [] adjacencylist;
	Vertex [] vertices;
	Vertex root;
	ArrayList<Edge> edges;
	
	// Constructor
	public WeightedGraphPrim(int total) {
		this.total = total;
		adjacencylist = new LinkedList[total];
		vertices = new Vertex[total];
		edges = new ArrayList<Edge>();
		
		for(int i=0; i<total; i++) {
			adjacencylist[i] = new LinkedList<>();
			vertices[i] = new Vertex(i);
		}
	}
		
	// Adding an edge to the graph
	public void addEdge(int source, int destination, double weight) {
		Edge edge = new Edge(vertices[source], vertices[destination], weight); 
		edges.add(edge);
		adjacencylist[source].addFirst(edge);
	}
	
	// Specifying the root vertex
	public void addRoot(int id) {
		root = vertices[id];
	}
	
	// Obtaining all edges which the source vertex is a part of
	public LinkedList<Edge> getEdges(int source) {
		return adjacencylist[source];
	}
	
	// Checking if an edge already exists in the graph
	public boolean findEdge(int source, int destination) {
		for(Edge edge: edges) {
			if(edge.source.id == source && edge.destination.id == destination)
				return true;
		}
		return false;
	}
}



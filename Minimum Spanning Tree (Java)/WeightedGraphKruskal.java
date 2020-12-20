import java.util.LinkedList;
import java.util.ArrayList;

// Weighted Graph for Kruskal's Algorithm
public class WeightedGraphKruskal {

	// A vertex in the graph
	static class Vertex {
		Vertex p;
		int rank;
		int id;
		
		// Constructor
		public Vertex(int id) {
			this.id = id;
		}
	}
	
	// An edge connecting 2 vertices
	static class Edge implements Comparable<Edge> {
		Vertex source;
		double weight;
		Vertex destination;
		
		// Constructor
		public Edge(Vertex source, Vertex destination, double weight) {
			this.source = source;
			this.destination = destination;
			this.weight = weight;
		}
		
		// To allow sorting of edges through Collections.sort()
		@Override
	    public int compareTo(Edge edge) {
	        double edgeWeight = edge.weight;
	        
	        return (int)(this.weight-edgeWeight);
		}
	}
	
	int total;
	LinkedList<Edge> [] adjacencylist;
	Vertex [] vertices;
	ArrayList<Edge> edges;
	
	// Constructor
	public WeightedGraphKruskal(int total) {
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
}


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// Implementations of Prim's and Kruskal's algorithms to analyze execution time (NO OUTPUT SHOWN)
public class AlgorithmComplexity {
	
	// Generating a random graph
	static void generateGraph(WeightedGraphPrim graphPrim, WeightedGraphKruskal graphKruskal, int vertices, int edges, int flag) {
		
		Random rand = new Random(); 
		
		for(int i=0; i<edges; i++) {
			int source = rand.nextInt(vertices);
			int destination = rand.nextInt(vertices);
			
			// Vertices of an edge cannot be same
			while(destination == source) {
				destination = rand.nextInt(vertices);
			}
			
			// Edge should not be already present in graph
			while(graphPrim.findEdge(source, destination)) {
				source = rand.nextInt(vertices);
				destination = rand.nextInt(vertices);
				while(destination == source) {
					destination = rand.nextInt(vertices);
				}
			
			}
			int weight = rand.nextInt(edges)+1;
			
			graphPrim.addEdge(source, destination, weight);
			graphKruskal.addEdge(source, destination, weight);
			
			graphPrim.addEdge(destination, source, weight);
			graphKruskal.addEdge(destination, source, weight);
			
			if(flag!=0)
				System.out.println("Edge: " + source + "-" + destination + "=" + weight);
		}
	
		int root = rand.nextInt(vertices);
		graphPrim.addRoot(root);
		
		if(flag!=0)
			System.out.println("Root: " + root);
	}
	
	// Implementation of prim's algorithm (NO OUTPUT)
	static void mstPrim(WeightedGraphPrim graph) {
		
		for(int i=0; i<graph.total; i++) {
			graph.vertices[i].key = Double.POSITIVE_INFINITY;
			graph.vertices[i].p = null;
		}
		
		graph.root.key = 0;
		
		FibonacciMinHeap queue = new FibonacciMinHeap();
		
		for(int i=0; i<graph.total; i++) {
			FibonacciMinHeap.Node n = new FibonacciMinHeap.Node(graph.vertices[i].key, i);
			queue.fibHeapInsert(n);
		}
		
		while (queue.getMin() != null) {
			WeightedGraphPrim.Vertex u = graph.vertices[queue.fibHeapExtractMin().id];
			
			for(WeightedGraphPrim.Edge edge: graph.getEdges(u.id)) {
				WeightedGraphPrim.Vertex v = edge.destination;
				
				if(queue.findNode(v.id) && edge.weight < v.key) {	
					v.p = u;
					queue.fibHeapDecreaseKey(queue.getNode(v.id), edge.weight);
					v.key = edge.weight;
				}
			}	
		}
	}
	
	// Making a set of each vertex
	static void makeSet(WeightedGraphKruskal.Vertex x) {
		x.p = x;
		x.rank = 0;
	}
	
	// Checking if a vertex exists in a set
	static WeightedGraphKruskal.Vertex findSet(WeightedGraphKruskal.Vertex x) {
		if(x!=x.p)
			x.p = findSet(x.p);

		return x.p;
	}
	
	// Finding the 2 vertices to link
	static void union(WeightedGraphKruskal.Vertex x, WeightedGraphKruskal.Vertex y) {
		link(findSet(x), findSet(y));
	}
	
	// Linking/Joining the vertices based on rank
	static void link(WeightedGraphKruskal.Vertex x, WeightedGraphKruskal.Vertex y) {
		if (x.rank>y.rank)
			y.p = x;
		else {
			x.p = y;
			if (x.rank == y.rank)
				y.rank += 1;
		}
	}
	
	// Implementation of kruskal's algorithm (NO OUTPUT)
	static ArrayList<WeightedGraphKruskal.Edge> mstKruskal(WeightedGraphKruskal graph) {
		
		ArrayList<WeightedGraphKruskal.Edge> mstEdges = new ArrayList<WeightedGraphKruskal.Edge>();
		
		for(int i=0; i<graph.total; i++){
			makeSet(graph.vertices[i]);
		}
		
		Collections.sort(graph.edges);
		
		for (WeightedGraphKruskal.Edge edge: graph.edges) {
			
			if(findSet(edge.source) != findSet(edge.destination)) {
				mstEdges.add(edge);
				union(edge.source, edge.destination);
			}
		}
		
		return mstEdges;
	}
	
	// Main function
	public static void main(String[] args) {
		
		System.out.println("------This is a Java program to analyze complexity of prim and kruskal algorithms on undirected-weighted graph------");
		System.out.println("====================================================================================================================");
		
		WeightedGraphPrim graphPrim;
		WeightedGraphKruskal graphKruskal;
		int vertices = 10;
		int edges = 20;
		int loops = 10;
		
		long avgPrim = 0;
		long avgKruskal = 0;
		
		for(int i=0; i<loops+1; i++)
		{
			graphPrim = new WeightedGraphPrim(vertices);
			graphKruskal = new WeightedGraphKruskal(vertices);
			
			if(i!=0) {
				System.out.println("Graph: " + i);
				System.out.println("====================================================================================================================");
			}
			
			generateGraph(graphPrim, graphKruskal, vertices, edges, i);
			
			// Measuring time taken to execute prim's algorithm
			long startPrim = System.nanoTime();
			mstPrim(graphPrim);
			long endPrim = System.nanoTime();
			
			long timePrim = endPrim - startPrim;
			
			if(i!=0) {
				avgPrim += timePrim;
				System.out.println("====================================================================================================================");
				System.out.println("Execution time for Prim: " + timePrim + " nanoseconds or " + timePrim/1000000.0 + " milliseconds");
			}
			
			// Measuring time taken to execute kruskal's algorithm
			long startKruskal = System.nanoTime();
			ArrayList<WeightedGraphKruskal.Edge> mstEdges = mstKruskal(graphKruskal);
			long endKruskal = System.nanoTime();
			
			long timeKruskal = endKruskal - startKruskal;
			
			if(i!=0) {
				avgKruskal += timeKruskal;
				System.out.println("Execution time for Kruskal: " + timeKruskal + " nanoseconds or " + timeKruskal/1000000.0 + " milliseconds");
				System.out.println("====================================================================================================================");
			}
		}
		
		// Calculating average time taken
		avgPrim /= loops;
		avgKruskal /= loops;
		
		System.out.println("Average execution for Prim: " + avgPrim + " nanoseconds or " + avgPrim/1000000.0 + " milliseconds");
		System.out.println("Average execution for Kruskal: " + avgKruskal + " nanoseconds or " + avgKruskal/1000000.0 + " milliseconds");
		
		System.out.println("====================================================================================================================");
	}
}

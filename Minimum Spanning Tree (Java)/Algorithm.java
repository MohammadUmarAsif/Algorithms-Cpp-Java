import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Implementations of Prim's and Kruskal's algorithms to view step-by-step outputs
public class Algorithm {
	
	// Creating a custom graph based on user-input
	static void userGraph(WeightedGraphPrim graphPrim, WeightedGraphKruskal graphKruskal, int edges, Scanner keyboard) {
		
		System.out.println("Enter the edges:");
		System.out.println("[Format: vertexOne-vertexTwo=weightOfEdge] (eg 0-2=10)");
		
		for(int i=0; i<edges; i++) {
			
			// Using regular expression to obtain source & destination vertices along with weight
			Pattern pattern = Pattern.compile("(\\d+)-(\\d+)=(\\d+)");
			String input = keyboard.findInLine(pattern);
			keyboard.nextLine();
			Matcher match = pattern.matcher(input);
			
			if(match.find()) {
				int source = Integer.parseInt(match.group(1));
				int destination = Integer.parseInt(match.group(2));
				double weight = Integer.parseInt(match.group(3));
				
				graphPrim.addEdge(source, destination, weight);
				graphPrim.addEdge(destination, source, weight);
				
				graphKruskal.addEdge(source, destination, weight);
				graphKruskal.addEdge(destination, source, weight);
			}
		}
		
		// Obtaining root vertex for prim's algorithm
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Enter the root vertex of the MST:");
		int root = keyboard.nextInt();
		keyboard.nextLine();
		
		graphPrim.addRoot(root);
	}
	
	// Creating a pre-made graph for quick demonstration
	static void premadeGraph(WeightedGraphPrim graphPrim, WeightedGraphKruskal graphKruskal) {
		
		graphPrim.addEdge(0, 1, 4);
		graphPrim.addEdge(0, 2, 3);
		graphPrim.addEdge(1, 2, 5);
		graphPrim.addEdge(1, 3, 2);
		graphPrim.addEdge(2, 3, 7);
		graphPrim.addEdge(3, 4, 2);
		graphPrim.addEdge(3, 5, 5);
		graphPrim.addEdge(4, 0, 4);
		graphPrim.addEdge(4, 1, 4);
		graphPrim.addEdge(4, 5, 6);
		
		graphPrim.addEdge(1, 0, 4);
		graphPrim.addEdge(2, 0, 3);
		graphPrim.addEdge(2, 1, 5);
		graphPrim.addEdge(3, 1, 2);
		graphPrim.addEdge(3, 2, 7);
		graphPrim.addEdge(4, 3, 2);
		graphPrim.addEdge(5, 3, 5);
		graphPrim.addEdge(0, 4, 4);
		graphPrim.addEdge(1, 4, 4);
		graphPrim.addEdge(5, 4, 6);
		
		graphPrim.addRoot(3);
		
		graphKruskal.addEdge(0, 1, 4);
		graphKruskal.addEdge(0, 2, 3);
		graphKruskal.addEdge(1, 2, 5);
		graphKruskal.addEdge(1, 3, 2);
		graphKruskal.addEdge(2, 3, 7);
		graphKruskal.addEdge(3, 4, 2);
		graphKruskal.addEdge(3, 5, 5);
		graphKruskal.addEdge(4, 0, 4);
		graphKruskal.addEdge(4, 1, 4);
		graphKruskal.addEdge(4, 5, 6);
		
		graphKruskal.addEdge(1, 0, 4);
		graphKruskal.addEdge(2, 0, 3);
		graphKruskal.addEdge(2, 1, 5);
		graphKruskal.addEdge(3, 1, 2);
		graphKruskal.addEdge(3, 2, 7);
		graphKruskal.addEdge(4, 3, 2);
		graphKruskal.addEdge(5, 3, 5);
		graphKruskal.addEdge(0, 4, 4);
		graphKruskal.addEdge(1, 4, 4);
		graphKruskal.addEdge(5, 4, 6);
	}
	
	// Implementation of prim's algorithm with outputs at each step
	static void mstPrim(WeightedGraphPrim graph) {
		
		for(int i=0; i<graph.total; i++) {
			graph.vertices[i].key = Double.POSITIVE_INFINITY;
			graph.vertices[i].p = null;
		}
		
		System.out.println("Step 1: Key and parent of each vertex set to infinity and null respectively.");
		System.out.println("---------------------------------------------------------------------------------------------------------");
		
		graph.root.key = 0;
		
		System.out.println("Step 2: Key of root set to 0.");
		System.out.println("---------------------------------------------------------------------------------------------------------");
		
		FibonacciMinHeap queue = new FibonacciMinHeap();
		
		for(int i=0; i<graph.total; i++) {
			FibonacciMinHeap.Node n = new FibonacciMinHeap.Node(graph.vertices[i].key, i);
			queue.fibHeapInsert(n);
		}
		
		System.out.println("Step 3: Inserted " + graph.total + " vertices in queue.");
		System.out.println("---------------------------------------------------------------------------------------------------------");
		System.out.println("Step 4: " + "Edges added to the MST.");
		
		while (queue.getMin() != null) {
			WeightedGraphPrim.Vertex u = graph.vertices[queue.fibHeapExtractMin().id];
			
			System.out.println("-----------------------------------------------------------");
			
			for(WeightedGraphPrim.Edge edge: graph.getEdges(u.id)) {
				WeightedGraphPrim.Vertex v = edge.destination;
				
				System.out.println("Current edge: " + edge.source.id + "-" + edge.destination.id + "=" + edge.weight);
				System.out.println("Condition: " + "Vertex " + v.id +" found in queue ? " + queue.findNode(v.id) + " && " + edge.weight + " < " + v.key);
				
				if(queue.findNode(v.id) && edge.weight < v.key) {
					
					System.out.println("Vertex " + v.id + " updated with parent = " + u.id + " and key = " + edge.weight);
					
					v.p = u;
					queue.fibHeapDecreaseKey(queue.getNode(v.id), edge.weight);
					v.key = edge.weight;
				}
				else
					System.out.println("Vertex " + v.id + " not updated!");
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
	
	// Implementation of kruskal's algorithm with outputs at each step
	static ArrayList<WeightedGraphKruskal.Edge> mstKruskal(WeightedGraphKruskal graph) {
		
		ArrayList<WeightedGraphKruskal.Edge> mstEdges = new ArrayList<WeightedGraphKruskal.Edge>();
		
		for(int i=0; i<graph.total; i++){
			makeSet(graph.vertices[i]);
		}
		
		System.out.println("Step 1: " + graph.total + " sets created, one for each vertex.");
		System.out.println("---------------------------------------------------------------------------------------------------------");
		
		Collections.sort(graph.edges);
		
		System.out.println("Step 2: " + "Edges sorted in non-decreasing order based on weight.");
		System.out.println("---------------------------------------------------------------------------------------------------------");
		
		for (WeightedGraphKruskal.Edge edge: graph.edges) {
			System.out.println(edge.source.id + "-" + edge.destination.id + "=" + edge.weight);
		}
		
		System.out.println("---------------------------------------------------------------------------------------------------------");
		System.out.println("Step 3: " + "Edges added to the MST.");
		
		for (WeightedGraphKruskal.Edge edge: graph.edges) {
			
			System.out.println("--------------------------------------");
			System.out.println("Current edge: " + edge.source.id + "-" + edge.destination.id + "=" + edge.weight);
			System.out.println("Condition: " + findSet(edge.source).id + "!=" + findSet(edge.destination).id);
			
			if(findSet(edge.source) != findSet(edge.destination)) {
				
				System.out.println("Edge added!");
				
				mstEdges.add(edge);
				union(edge.source, edge.destination);
			}
			else 
				System.out.println("Edge skipped!");
		}
		
		return mstEdges;
	}
	
	// Printing the MST obtained through prim's algorithm
	static void printMstPrim(WeightedGraphPrim graph) {
		
		System.out.println("---------------------------------------------------------------------------------------------------------");
		System.out.println("Following are the edges in the MST: ");
		
		double sum = 0;
		
		for (int i=0; i<graph.total; i++) {
			if (graph.vertices[i].p != null) {
				System.out.println(graph.vertices[i].p.id + "-" + graph.vertices[i].id + "=" + graph.vertices[i].key);
				sum += graph.vertices[i].key;
			}
		}
		
		System.out.println("---------------------------------------------------------------------------------------------------------");
		System.out.println("Total cost of MST = " + sum);
	}
	
	// Printing the MST obtained through kruskal's algorithm
	static void printMstKruskal(ArrayList<WeightedGraphKruskal.Edge> mstEdges) {
		
		System.out.println("---------------------------------------------------------------------------------------------------------");
		System.out.println("Following are the edges in the MST: ");
		
		double sum = 0;
		
		for (WeightedGraphKruskal.Edge edge: mstEdges) {
			System.out.println(edge.source.id + "-" + edge.destination.id + "=" + edge.weight);
			sum += edge.weight;
		}
		
		System.out.println("---------------------------------------------------------------------------------------------------------");
		System.out.println("Total cost of MST = " + sum);
	}
	
	// Main function
	public static void main(String[] args) {
		
		System.out.println("------This is a Java program to find the Minimum Spanning Tree (MST) of a undirected-weighted graph------");
		System.out.println("=========================================================================================================");
		
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Choose an input method:");
		System.out.println("1. User Input");
		System.out.println("2. Pre-made Graph");
		int input = keyboard.nextInt();
		keyboard.nextLine();
		
		WeightedGraphPrim graphPrim;
		WeightedGraphKruskal graphKruskal;
		
		// If custom graph is preferred
		if(input == 1) {
			
			System.out.println("---------------------------------------------------------------------------------------------------------");
			System.out.println("Enter the number of vertices in the graph:");
			int vertices = keyboard.nextInt();
			keyboard.nextLine();
			System.out.println("---------------------------------------------------------------------------------------------------------");
			
			System.out.println("Enter the number of edges in the graph:");
			int edges = keyboard.nextInt();
			keyboard.nextLine();
			System.out.println("---------------------------------------------------------------------------------------------------------");
			
			graphPrim = new WeightedGraphPrim(vertices);
			graphKruskal = new WeightedGraphKruskal(vertices);
			
			userGraph(graphPrim, graphKruskal, edges, keyboard);
			
			System.out.println("=========================================================================================================");
			System.out.println("--------------------------------------------Prim's Algorithm---------------------------------------------");
			System.out.println("=========================================================================================================");
			mstPrim(graphPrim);
			printMstPrim(graphPrim);
			
			System.out.println("=========================================================================================================");
			System.out.println("-------------------------------------------Kruskal's Algorithm-------------------------------------------");
			System.out.println("=========================================================================================================");
			ArrayList<WeightedGraphKruskal.Edge> mstEdges = mstKruskal(graphKruskal);
			printMstKruskal(mstEdges);
		}
		
		// If pre-made graph is preferred
		else if(input == 2) {
			
			graphPrim = new WeightedGraphPrim(6);
			graphKruskal = new WeightedGraphKruskal(6);
			
			premadeGraph(graphPrim, graphKruskal);
			
			System.out.println("=========================================================================================================");
			System.out.println("--------------------------------------------Prim's Algorithm---------------------------------------------");
			System.out.println("=========================================================================================================");
			mstPrim(graphPrim);
			printMstPrim(graphPrim);
			
			System.out.println("=========================================================================================================");
			System.out.println("-------------------------------------------Kruskal's Algorithm-------------------------------------------");
			System.out.println("=========================================================================================================");
			ArrayList<WeightedGraphKruskal.Edge> mstEdges = mstKruskal(graphKruskal);
			printMstKruskal(mstEdges);
		}
		
		keyboard.close();
		
		System.out.println("=========================================================================================================");
	}
}

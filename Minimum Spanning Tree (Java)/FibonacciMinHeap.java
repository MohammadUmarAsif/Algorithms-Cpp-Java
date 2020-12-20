import java.lang.Math;
import java.util.Hashtable;

// Fibonacci Heap implementation of min-priority queue
public class FibonacciMinHeap {
	
	// Node in a tree representing a vertex in the graph
	static class Node {
		Node p;
		Node child;
		Node left;
		Node right;
		
		int id;
		double key;
		int degree;
		boolean mark;
		
		// Constructor
		public Node (double key, int id) {
			this.key = key;
			this.id = id;
			this.degree = 0;
			this.p = null;
			this.child = null;
			this.left = this;
			this.right = this;
			this.mark = false;
		}
	}
	
	// Used to find maximum degree of a node in a tree
	private static final double oneOverLogPhi = 1.0 / Math.log((1.0 + Math.sqrt(5.0)) / 2.0);
	
	private Node min;
	private int n;
	private Hashtable<Integer, Node> nodes = new Hashtable<Integer, Node>();
	
	
	// Constructor
	public FibonacciMinHeap() {
		this.min = null;
		this.n = 0;
	}
	
	// Obtaining node with minimum key
	public Node getMin() {
		return this.min;
	}
	
	// Obtaining node of a given id
	public Node getNode(int id) {
		return this.nodes.get(id);
	}
	
	// Inserting a node into the heap
	public void fibHeapInsert(Node x) {
		
		if(this.min == null) {
			this.min = x;
		}
		else {
			// Add to root list
			x.left = this.min;
			x.right = this.min.right;
			this.min.right = x;
			x.right.left = x;
			
			if(x.key < this.min.key)
				this.min = x;
		}
		
		this.n += 1;
		this.nodes.put(x.id, x);
	}
	
	// Extracting a node from the heap
	public Node fibHeapExtractMin () {
		Node z = this.min;	
		
		/* Adding all the children of the node to the root list
		 * Removing the node from the root list
		 * Calling consolidation function to re-construct the heap
		 */
		if (z != null) {
			int children = z.degree;
			Node x = z.child;
			Node ptr;
			
			while(children>0) {
				ptr = x.right;
				
				x.left.right = x.right;
				x.right.left = x.left;
				
				x.left = this.min;
				x.right = this.min.right;
				this.min.right = x;
				x.right.left = x;
				
				x.p = null;
				x = ptr;
				children--;
			}
			
			z.left.right = z.right;
			z.right.left = z.left;
			
			if (z == z.right) {
				this.min = null;
			}
			else {
				this.min = z.right;
				consolidate();
			}
			
			this.n -= 1;
			this.nodes.remove(z.id);
		}
		
		return z;
	}
	
	// Performing consolidation of nodes
	private void consolidate() {
		int depth =  ((int) Math.floor(Math.log(this.n) * oneOverLogPhi)) + 1;
		
		Node [] A = new Node[depth];
		
		for(int i=0; i<depth; i++) {
			A[i] = null;
		}
		
		Node x = this.min;
		int roots = 0;
		
		// Obtaining number of nodes in the root list
		if (x != null) {
            roots++;
            x = x.right;

            while (x != this.min) {
                roots++;
                x = x.right;
            }
        }
		
		while(roots > 0) {
			int d = x.degree;
			Node ptr = x.right;
			
			// If there are nodes of the same degree
			for (;;) {
				Node y = A[d];
				
				// If none, break out
				if(y==null)
					break;
				
				// Making one node a child of the other based on key value
				if(x.key>y.key) {
					Node temp = y;
					y = x;
					x = temp;
				}
				
				// y is removed from root list
				fibHeapLink(y,x);
				
				A[d] = null;
				d++;
			}
			
			A[d] = x;
			x = ptr;
			roots--;
			
		}
		
		// Re-constructing the heap
		this.min = null;
		
		for(int i=0; i<depth; i++) {
			if(A[i] != null) {
				
				if(this.min == null) {
					this.min = A[i];
				}
				else {
					A[i].left.right = A[i].right;
					A[i].right.left = A[i].left;

					// Add to root list
					A[i].left = this.min;
					A[i].right = this.min.right;
	                this.min.right = A[i];
	                A[i].right.left = A[i];
					
					
					if(A[i].key < this.min.key) {
						this.min = A[i];
					}
				}
			}
		}
	}
	
	// Making y a child of x 
	private void fibHeapLink(Node y, Node x) {
		
		// Remove y form root list
		y.left.right = y.right;
		y.right.left = y.left;
		
		// Make it child of x
		y.p = x;
		
		if(x.child == null) {
			x.child = y;
			y.right = y;
			y.left = y;
		}
		else {
			y.left = x.child;
            y.right = x.child.right;
            x.child.right = y;
            y.right.left = y;
		}
				
	    x.degree += 1;
		y.mark = false;
	}

	// Decreasing value of a node with new key value
	public void fibHeapDecreaseKey(Node x, double k) {
		
		x.key = k;
		Node y = x.p;
		
		if (y!= null && x.key<y.key) {
			cut(x, y);
			cascadingCut(y);
		}
		
		if (x.key<this.min.key)
			this.min = x;
	}
	
	// Removes x from child list of y
	private void cut(Node x, Node y) {
		
		// Remove x as a child
		x.left.right = x.right;
		x.right.left = x.left;
		
		y.degree--;
		
		if(y.child == x) {
			y.child = x.right;
		}
		
		if(y.degree == 0) {
			y.child = null;
		}
		
		// Add to root list
		x.left = this.min;
		x.right = this.min.right;
		this.min.right = x;
		x.right.left = x;
	    
		x.p = null;
		x.mark = false;
	}

	/* Performs a cascading cut operation
	 * Cuts y from its parent
     * Does the same for its parent, and so on up the tree
     */
	private void cascadingCut(Node y) {
		Node z = y.p;
		
		// If parent exists
		if (z != null) {
			if(y.mark == false)
				y.mark = true;
			
			// If marked, cut y from z, and cut z and so on.
			else {
				cut(y, z);
				cascadingCut(z);
			}
		}
	}
	
	// True if node is present in heap, false if not
	public boolean findNode(int id) {
		return nodes.containsKey(id);
	}
}

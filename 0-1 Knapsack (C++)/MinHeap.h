#ifndef _MINHEAP_H_
#define _MINHEAP_H_

// Defining the Node
struct Node { 
    float cost; 
    int upper_bound; 
    int level; 
    bool flag; 
    float total_profit;
    int total_weight;
};

// Declaring the heap class
class MinHeap { 

    // Attributes
    Node* heap;
    int n; 
    int size;

public: 

    //Methods
    MinHeap(int num_items);
    
    void MinHeapify(int i); 
  
    int parent(int i) { return (i-1)/2; } 
  
    int left(int i) { return (2*i + 1); } 
  
    int right(int i) { return (2*i + 2); } 
 
    Node extractMin(); 

    void insertNode(Node a); 

    bool isEmpty();
    
    void display(float min_upper_bound);
}; 

#endif
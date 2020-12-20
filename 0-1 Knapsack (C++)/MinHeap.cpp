// Including required header files
#include <iostream>
#include <iomanip>
#include "MinHeap.h"

// Constructor
MinHeap::MinHeap(int num_items) { 
    
    size = 0; 
    n = num_items; 
    heap = new Node[num_items]; 
} 

// Function to swap 2 nodes in a heap
void swap(Node &a, Node &b) { 

    Node temp = a; 
    a = b; 
    b = temp; 
}

// Function to insert a node into the heap
void MinHeap::insertNode(Node a) {  

    size++; 
    int i = size - 1; 
    heap[i] = a; 
  	
  	// Allocate position in heap based on value of the upper_bound of the node
    while (i != 0 && heap[parent(i)].upper_bound > heap[i].upper_bound) { 
       swap(heap[i], heap[parent(i)]); 
       i = parent(i); 
    } 
} 

// Function to extract a node from the heap
Node MinHeap::extractMin() { 

    if (size == 1) { 
        size--; 
        return heap[0]; 
    } 
  	
  	// Allocate the last node to the root position and heapify
    Node root = heap[0]; 
    heap[0] = heap[size-1]; 
    size--; 

    MinHeapify(0); 
  
    return root; 
} 

// Function to make the min-heap
void MinHeap::MinHeapify(int i) { 

    int l = left(i); 
    int r = right(i); 
    int smallest = i; 

    // Allocate position in heap based on value of the upper_bound of the node
    if (l < size && heap[l].upper_bound < heap[i].upper_bound) 
        smallest = l; 

    if (r < size && heap[r].upper_bound < heap[smallest].upper_bound) 
        smallest = r; 

    if (smallest != i) { 
        swap(heap[i], heap[smallest]); 
        MinHeapify(smallest); 
    } 
} 

// Function to check if heap is empty
bool MinHeap::isEmpty() {
    return (size == 0);
}

// Function to display the current nodes in the heap
void MinHeap::display(float min_upper_bound) {
    
    std::cout<<"-----------------------------------------------------------------------------------------------------------------"<<std::endl;
    
    for (int i = 0; i < size; i++)
        std::cout<<"C = "<<std::setw(10)<<heap[i].cost<<" UB = "<<std::setw(5)<<heap[i].upper_bound<<"|";
    
    std::cout<<std::endl<<"Min UB = "<<min_upper_bound<<std::endl;
}
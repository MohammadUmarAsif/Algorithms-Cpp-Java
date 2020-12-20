// Including required header files
#include <iostream>
#include <algorithm>
#include <random>
#include <iomanip>
#include <string>
#include <chrono>
#include "MinHeap.h"

// Including required entities from std namespace
using std::cout;
using std::endl;
using std::setfill;
using std::setw;
using std::left;
using std::to_string;
using std::chrono::duration_cast;
using std::chrono::high_resolution_clock;
using std::chrono::nanoseconds;

// Defining the Item
struct Item { 
    int weight; 
    int profit; 
    int index;
};

// Function to calculate total weight of items
int weightSum(const Item* items, int n) {
    
    int sum = 0;

    for(int i = 0; i < n; i++)
        sum += items[i].weight;

    return sum;
}

// Function to find the smallest weight
int smallestWeight(const Item* items, int n) {
    
    int min = items[0].weight;

    for(int i = 1; i < n; i++) {
        if(min > items[i].weight)
            min = items[i].weight;
    }

    return min;
}

// Function to generate random items
void generateValues(int n, Item* items, int &W) {
    
    // Random integer generation within specified range
    std::default_random_engine generator;
    
    generator.seed(std::chrono::system_clock::now().time_since_epoch().count());
    
    std::uniform_int_distribution<int> weight(1,2*n);
    std::uniform_int_distribution<int> profit(1,3*n);
    std::uniform_int_distribution<int> capacity(1,5*n);
  
    int w;
    int p;

    for(int i = 0; i<n; i++) {
        w = weight(generator);
        p = profit(generator);
        items[i] = {w, p, i};
    }
    
    W = capacity(generator);
    
    int weight_sum = weightSum(items, n);
    int smallest_weight = smallestWeight(items, n);
    
    // Not all items can fit and atleast one item should fit 
    while (weight_sum <= W || smallest_weight > W)
        W = capacity(generator);
}

// Function to copy a node
Node copyNode(Node &a) {

    Node b;
    b.cost = a.cost;
    b.upper_bound = a.upper_bound;
    b.level = a.level;
    b.flag = a.flag;
    b.total_profit = a.total_profit;
    b.total_weight = a.total_weight;

    return b;
}

// Function to assign values to a node
void assignNode(Node &a, float cost, int upper_bound, int level, bool flag, float total_profit, int total_weight) {
    
    a.cost = cost;
    a.upper_bound = upper_bound;
    a.level = level;
    a.flag = flag;
    a.total_profit = total_profit;
    a.total_weight = total_weight;
}

// Function to return smaller floating point number
float smaller(float a, float b) {
    return (b<a)?b:a;
}

// Function to sort by value of profit/weight ratio
float sortByRatio(Item &a, Item &b) { 
    return a.profit/(float)a.weight > b.profit/(float)b.weight; 
} 

// Function to calculate cost of a node (includes fractions)
float calculateCost(int n, int W, float total_profit, int total_weight, int index, Item* items) { 
    
    float profit = total_profit; 
    float weight = total_weight;

    for (int i = index; i < n; i++) { 
        if (weight + items[i].weight <= W) { 
            weight += items[i].weight; 
            profit -= items[i].profit; 
        } 
        else { 
            profit -= (float)((W - weight) * items[i].profit) / (float)items[i].weight; 
            break; 
        } 
    } 
    
    return profit; 
} 

// Function to calculate the upper bound of a node (excludes fractions)
float calculateUpperBound(int n, int W, float total_profit, int total_weight, int index, Item* items) { 
    
    float profit = total_profit; 
    float weight = total_weight;
    
    for (int i = index; i < n; i++) { 
        if (weight + items[i].weight <= W) { 
            weight += items[i].weight; 
            profit -= items[i].profit; 
        } 
        else
            break; 
    } 

    return profit; 
}

// Function to implement least-cost branch & bound algorithm (With steps)
void algorithmLCBB(int n, float W, Item* items, bool* path, int &max_profit_LCBB) { 

	// Sorting items based on profit/weight ratio
    std::sort(items, items+n, sortByRatio);
    
    cout<<"Sorting items based on profit/weight ratio"<<endl;
    cout<<"-----------------------------------------------------------------------------------------------------------------"<<endl;

    cout<<setw(6)<<left<<"Item"<<"|";
    cout<<setw(6)<<"Weight"<<"|";
    cout<<setw(6)<<"Profit";
    cout<<endl;
    
    // Displaying sorted items
    for(int i = 0; i < n; i++) {
        cout<<setw(6)<<items[i].index<<"|";
        cout<<setw(6)<<items[i].weight<<"|";
        cout<<setw(6)<<items[i].profit<<endl;
    }
    
    Node node, left, right; 

    // Minimum lower bound of explored nodes
    float min_upper_bound = 0;

    // Minimum lower bound of paths reaching final level
    float final_upper_bound = INFINITY; 

    node.total_profit = node.total_weight = node.cost = node.upper_bound = 0, node.level = 0; 
    node.flag = false;

    // Storing based on upper_bound value
    MinHeap* priority_queue = new MinHeap(n);
    
    // Inserting dummy node
    priority_queue->insertNode(node); 

    // Keeping track of which item is included
    bool* current_path = new bool[n]; 
    
    for (int i = 0; i < n; i++) 
        current_path[i] = path[i] = false; 
 
    cout<<"================================================================================================================="<<endl;
    cout<<"Displaying state of priority queue at each step: (C = Cost & UB = Upper Bound)"<<endl;
    
    
    while (!priority_queue->isEmpty()) { 

        priority_queue->display(min_upper_bound);
        
        node = priority_queue->extractMin();
        
        if (node.cost > min_upper_bound || node.cost >= final_upper_bound)
        	// Not necessary to explore since cost > optimal value
            continue;

        // Update path
        if (node.level != 0) 
            current_path[node.level - 1] = node.flag; 

        // Final level
        if (node.level == n) { 
            if (node.upper_bound < final_upper_bound) {  
                
                cout<<"================================================================================================================="<<endl;
                cout<<"Final path in state space tree:"<<endl;
                cout<<"-----------------------------------------------------------------------------------------------------------------"<<endl;
                cout<<setw(8)<<std::left<<"Item"<<"|";
                cout<<setw(8)<<"Included"<<"|";
                cout<<endl;
                
                // Saving the final path
                for (int i = 0; i < n; i++)
                    path[items[i].index] = current_path[i]; 
                

                for (int i = 0; i < n; i++){
                    cout<<setw(8)<<i<<"|";
                    cout<<setw(8)<<path[i]<<"|";
                    cout<<endl;
                }
                    
            } 
            
            final_upper_bound = smaller(node.upper_bound, final_upper_bound);
                
            continue; 
        }
        
        int level = node.level; 

        // Right node excludes current item
        assignNode(right, calculateCost(n, W, node.total_profit, node.total_weight, level + 1, items), 
                    calculateUpperBound(n, W, node.total_profit, node.total_weight, level + 1, items), 
                    level + 1, false, node.total_profit, node.total_weight); 
        
        // If adding current item will not exceed capacity
        if (node.total_weight + items[node.level].weight <= W) { 

            left.cost = calculateCost(n, W,
                    node.total_profit - items[level].profit, 
                    node.total_weight + items[level].weight, 
                    level + 1, items); 
                    
            left.upper_bound = calculateUpperBound( n, W,
                    node.total_profit - items[level].profit, 
                    node.total_weight + items[level].weight, 
                    level + 1, items); 

            // Left node includes current item
            assignNode(left, left.cost, left.upper_bound, level + 1, true, 
                    node.total_profit - items[level].profit, 
                    node.total_weight + items[level].weight); 
                    
        } 
        // If capacity is exceeding, don't add to priority queue
        else
            left.cost = left.upper_bound = 1;
         
        min_upper_bound = smaller(min_upper_bound, left.upper_bound); 
        min_upper_bound = smaller(min_upper_bound, right.upper_bound); 

        // Only nodes with upper bound equal to/lesser than minimum should be explored
        if (min_upper_bound >= left.cost) 
            priority_queue->insertNode(copyNode(left));
        
        if (min_upper_bound >= right.cost) 
            priority_queue->insertNode(copyNode(right)); 
    } 
    
    // Saving maximum possible profit
    max_profit_LCBB = -final_upper_bound;
} 

// Function to implement least-cost branch & bound algorithm (Without steps)
void complexityLCBB(int n, float W, Item* items, bool* path, int &max_profit_LCBB) { 

	// Sorting items based on profit/weight ratio
    std::sort(items, items+n, sortByRatio);
    
    Node node, left, right; 

    // Minimum lower bound of explored nodes
    float min_upper_bound = 0;

    // Minimum lower bound of paths reaching final level
    float final_upper_bound = INFINITY; 

    node.total_profit = node.total_weight = node.cost = node.upper_bound = 0, node.level = 0; 
    node.flag = false;

    // Storing based on upper_bound value
    MinHeap* priority_queue = new MinHeap(n);
    
    // Inserting dummy node
    priority_queue->insertNode(node); 

    // Keeping track of which item is included
    bool* current_path = new bool[n]; 
    
    for (int i = 0; i < n; i++) 
        current_path[i] = path[i] = false; 
    
    while (!priority_queue->isEmpty()) { 

        node = priority_queue->extractMin();
        
        if (node.cost > min_upper_bound || node.cost >= final_upper_bound)
        	// Not necessary to explore since cost > optimal value
            continue;

        // Update path
        if (node.level != 0) 
            current_path[node.level - 1] = node.flag; 

        // Final level
        if (node.level == n) { 
            if (node.upper_bound < final_upper_bound) {  
                
                // Saving the final path
                for (int i = 0; i < n; i++)
                    path[items[i].index] = current_path[i]; 
            } 
            
            final_upper_bound = smaller(node.upper_bound, final_upper_bound);
                
            continue; 
        }
        
        int level = node.level; 

        // Right node excludes current item
        assignNode(right, calculateCost(n, W, node.total_profit, node.total_weight, level + 1, items), 
                    calculateUpperBound(n, W, node.total_profit, node.total_weight, level + 1, items), 
                    level + 1, false, node.total_profit, node.total_weight); 
        
        // If adding current item will not exceed capacity
        if (node.total_weight + items[node.level].weight <= W) { 

            left.cost = calculateCost(n, W,
                    node.total_profit - items[level].profit, 
                    node.total_weight + items[level].weight, 
                    level + 1, items); 
                    
            left.upper_bound = calculateUpperBound( n, W,
                    node.total_profit - items[level].profit, 
                    node.total_weight + items[level].weight, 
                    level + 1, items); 

            // Left node includes current item
            assignNode(left, left.cost, left.upper_bound, level + 1, true, 
                    node.total_profit - items[level].profit, 
                    node.total_weight + items[level].weight); 
                    
        } 
        // If capacity is exceeding, don't add to priority queue
        else
            left.cost = left.upper_bound = 1;
         
        min_upper_bound = smaller(min_upper_bound, left.upper_bound); 
        min_upper_bound = smaller(min_upper_bound, right.upper_bound); 

        // Only nodes with upper bound equal to/lesser than minimum should be explored
        if (min_upper_bound >= left.cost) 
            priority_queue->insertNode(copyNode(left));
        
        if (min_upper_bound >= right.cost) 
            priority_queue->insertNode(copyNode(right)); 
    } 
        
    // Saving maximum possible profit
    max_profit_LCBB = -final_upper_bound;
} 

// Function to display DP table
void printTable(int n, int &W, int** B, int item, bool flag = false, int* bag = NULL, int profit = -1) {
    
    if(!flag) {
        if(item == -1)
            cout<<"INITIAL TABLE"<<endl;
        else
            cout<<"Evaluating for item # "<<item<<endl;
    }
    else {
            cout<<"FINDING ITEMS"<<endl;
            cout<<setfill('=')<<setw((W+2)*7)<<""<<endl;
            cout<<"Including item # "<<item+1<<endl; 
    }
    
    cout<<setfill('=')<<setw((W+2)*7)<<""<<endl;
    cout<<setfill(' ');
    
    cout<<setw(6)<<"Weight"<<"|";
    for(int j = 0; j <= W; j++)
        cout<<setw(6)<<left<<j<<"|";
        
    if(flag)
            cout<<setw(6)<<left<<"Selected Item";
    cout<<endl;
    
    cout<<setw(6)<<left<<"Item"<<"|";
    for(int j = 0; j <= W; j++) {
        cout<<setfill('-')<<setw(6)<<""<<"|";
    }
    cout<<setfill(' ')<<endl;
    
    for(int i = 0; i <= n; i++) {
        cout<<setw(6)<<i<<"|";
        
        for(int j = 0; j <= W; j++) {
            
            if (flag && (i == item || i == item+1) && (j == profit))
                cout<<setw(6)<<left<<(to_string(B[i][j])+"**")<<"|";
            else
                cout<<setw(6)<<left<<(B[i][j]==-1?"":to_string(B[i][j]))<<"|";
        }

        if(flag && i>0)
            cout<<setw(6)<<std::right<<bag[i-1];
        cout<<left<<endl;
    }
    
    cout<<setfill('=')<<setw((W+2)*7)<<""<<endl;
    cout<<setfill(' ');
}

// Function to implement dynamic programming algorithm (With steps)
void algorithmDP(int n, Item* items, int &W, int** B, int* bag) {
    
    // Initializing table
    for(int i = 0; i <= W; i++) {
        B[0][i] = 0;
    }
    for(int i = 1; i <= n; i++) {
        B[i][0] = 0;
    }
    
    printTable(n, W, B, -1);
    
    // Filling table as per condition
    for(int i = 1; i <= n; i++) {
        for(int j = 0; j <= W; j++) {
            
            if(items[i-1].weight <= j) {
                if(items[i-1].profit + B[i-1][j-items[i-1].weight] > B[i-1][j])
                    B[i][j] = items[i-1].profit + B[i-1][j-items[i-1].weight];
                else 
                    B[i][j] = B[i-1][j];
            }
            else
                B[i][j] = B[i-1][j];
        }

        printTable(n, W, B, i);
    }
   
    int i = n;
    int j = W;
    
    // Backtracking to get the items
    while(j>0 and i>0) {
        if (B[i][j] != B[i-1][j]) {
            
            bag[i-1] = 1;
            j -= items[i-1].weight;
            i -= 1;

            printTable(n, W, B, i, true, bag, j);
        }
        else
            i -= 1;
    }
}

// Function to implement dynamic programming algorithm (Without steps)
void complexityDP(int n, Item* items, int &W, int** B, int* bag) {
    
    // Initializing table
    for(int i = 0; i <= W; i++) {
        B[0][i] = 0;
    }
    for(int i = 1; i <= n; i++) {
        B[i][0] = 0;
    }
    
    // Filling table as per condition
    for(int i = 1; i <= n; i++) {
        for(int j = 0; j <= W; j++) {
            
            if(items[i-1].weight <= j) {
                if(items[i-1].profit + B[i-1][j-items[i-1].weight] > B[i-1][j])
                    B[i][j] = items[i-1].profit + B[i-1][j-items[i-1].weight];
                else 
                    B[i][j] = B[i-1][j];
            }
            else
                B[i][j] = B[i-1][j];
        }
    }
   
    int i = n;
    int j = W;
    
    // Backtracking to get the items
    while(j>0 and i>0) {
        if (B[i][j] != B[i-1][j]) {
            
            bag[i-1] = 1;
            j -= items[i-1].weight;
            i -= 1;
        }
        else
            i -= 1;
    }
}

// Function to implement both algorithms for displaying steps
void displaySteps(int n) {
    
    Item* items = new Item[n];
    int W;
    
    // Generating random items
    generateValues(n, items, W);
    
    // Displaying items
    cout<<setw(6)<<left<<"Item"<<"|";
    cout<<setw(6)<<"Weight"<<"|";
    cout<<setw(6)<<"Profit";
    cout<<endl;
    
    for(int i = 0; i < n; i++) {
        cout<<setw(6)<<items[i].index<<"|";
        cout<<setw(6)<<items[i].weight<<"|";
        cout<<setw(6)<<items[i].profit<<endl;
    }

    cout<<"-----------------------------------------------------------------------------------------------------------------"<<endl;
    cout<<"Capacity: "<<W<<endl;

    cout<<setfill('=')<<setw((W+2)*7)<<""<<endl;
    cout<<setfill(' ');
    
    int** B = new int*[n+1];
    for(int i = 0; i < n+1; i++) {
        B[i] = new int[W+1];
    }
    
    for(int i = 0; i <= n; i++) {
        for(int j = 0; j <= W; j++) {
            B[i][j] = -1;
        }
    }
    
    int* bag = new int[n];
    for(int i = 0; i < n; i++) {
        bag[i]=0;
    }

    cout<<"DYNAMIC PROGRAMMING"<<endl;
    cout<<setfill('=')<<setw((W+2)*7)<<""<<endl;

    algorithmDP(n, items, W, B, bag);

    cout<<"Problem Solved with Dynamic Programming"<<endl;
    cout<<setfill('=')<<setw((W+2)*7)<<""<<endl;

    // Displaying final items through DP
    cout<<"Items in the knapsack: "<<endl;
    int total_weight_DP = 0;
    int max_profit_DP = 0;

    for(int i = 0; i < n; i++) {
        if(bag[i] == 1) {
            cout<<"#"<<i+1<<" ("<<items[i].weight<<","<<items[i].profit<<")"<<endl;
            total_weight_DP += items[i].weight;
            max_profit_DP += items[i].profit;
        }
    }
    
    cout<<"Total Weight = "<<total_weight_DP<<" (Capacity = "<<W<<")"<<endl;
    cout<<"Maximum Possible Profit = "<<max_profit_DP<<endl;

    cout<<"================================================================================================================="<<endl;
	cout<<"LEAST-COST BRANCH & BOUND"<<endl;
    cout<<"================================================================================================================="<<endl;

    int max_profit_LCBB;
    bool* path = new bool[n];
    
    cout<<setfill(' ');

    algorithmLCBB(n, W, items, path, max_profit_LCBB);
    
    cout<<"================================================================================================================="<<endl;
    cout<<"Problem Solved with Least-Cost Branch & Bound"<<endl;
    cout<<"================================================================================================================="<<endl;

    // Displaying final items through LCBB
    cout<<"Items in the knapsack: "<<endl;
    int total_weight_LCBB = 0;

    for(int i = 0; i < n; i++) {
        if(path[items[i].index]) {
            cout<<"#"<<items[i].index<<" ("<<items[i].weight<<","<<items[i].profit<<")"<<endl;
            total_weight_LCBB += items[i].weight;
        }
    }
    
    cout<<"Total Weight = "<<total_weight_LCBB<<" (Capacity = "<<W<<")"<<endl;
    cout<<"Maximum Possible Profit = "<<max_profit_LCBB<<endl;
}

// Function to implement both algorithms for displaying execution time
void displayExecutionTime(int n, auto &averageDP, auto &averageLCBB) {
    
    Item* items = new Item[n];
    int W;

    // Generating random items
    generateValues(n, items, W);
    
    // Displaying items
    cout<<setw(7)<<left<<"Item"<<"|";
    for(int i = 0; i < n; i++)
        cout<<setw(6)<<left<<i+1<<"|";
    cout<<endl;

    cout<<setw(7)<<"Weight"<<"|";
    for(int i = 0; i < n; i++)
        cout<<setw(6)<<items[i].weight<<"|";
    cout<<endl;

    cout<<setw(7)<<"Profit"<<"|";
    for(int i = 0; i < n; i++)
        cout<<setw(6)<<items[i].profit<<"|";
    cout<<endl;

    cout<<"Capacity: "<<W<<endl;
    cout<<"-----------------------------------------------------------------------------------------------------------------"<<endl;

    int** B = new int*[n+1];
    for(int i = 0; i < n+1; i++) {
        B[i] = new int[W+1];
    }
    
    for(int i = 0; i <= n; i++) {
        for(int j = 0; j <= W; j++) {
            B[i][j] = -1;
        }
    }
    
    int* bag = new int[n];
    for(int i = 0; i < n; i++) {
        bag[i]=0;
    }
    
    // Measuring execution time of DP
    auto startDP = high_resolution_clock::now(); 
    complexityDP(n, items, W, B, bag);
    auto stopDP = high_resolution_clock::now();
    auto durationDP = duration_cast<nanoseconds>(stopDP - startDP); 
    
    int total_weight_DP = 0;
    int max_profit_DP = 0;

    for(int i = 0; i < n; i++) {
        if(bag[i] == 1) {
            total_weight_DP += items[i].weight;
            max_profit_DP += items[i].profit;
        }
    }

    cout<<"DP Execution Time = "<<durationDP.count()<<" nanoseconds or "<<durationDP.count()/1000.0<<" microseconds"<<endl;
    cout<<"Total Weight = "<<total_weight_DP<<" (Capacity = "<<W<<")"<<endl;
    cout<<"Maximum Possible Profit = "<<max_profit_DP<<endl;

    // Displaying final items through DP
    for(int i = 0; i < n; i++) {
        if(bag[i] == 1)
            cout<<"#"<<items[i].index<<" ("<<items[i].weight<<","<<items[i].profit<<")"<<endl;
    }

    cout<<"-----------------------------------------------------------------------------------------------------------------"<<endl;

    int max_profit_LCBB;
    bool* path = new bool[n];

    // Measuring execution time of LCBB
    auto startLCBB = high_resolution_clock::now(); 
    complexityLCBB(n, W, items, path, max_profit_LCBB);
    auto stopLCBB = high_resolution_clock::now();
    auto durationLCBB = duration_cast<nanoseconds>(stopLCBB - startLCBB); 
    
    int total_weight_LCBB = 0;

    for(int i = 0; i < n; i++) {
        if(path[items[i].index]) {
            total_weight_LCBB += items[i].weight;
        }
    }

    cout<<"LCBB Execution Time = "<<durationLCBB.count()<<" nanoseconds or "<<durationLCBB.count()/1000.0<<" microseconds"<<endl;
    cout<<"Total Weight = "<<total_weight_LCBB<<" (Capacity = "<<W<<")"<<endl;
    cout<<"Maximum Possible Profit = "<<max_profit_LCBB<<endl;

    // Displaying final items through LCBB
    for(int i = 0; i < n; i++) {
        if(path[items[i].index])
            cout<<"#"<<items[i].index<<" ("<<items[i].weight<<","<<items[i].profit<<")"<<endl;
    }

    cout<<"================================================================================================================="<<endl;
    
    // Adding up for average
    averageDP += durationDP.count();
    averageLCBB += durationLCBB.count();
}

// Main Function
int main() {
    
    cout<<"This is a C++ program to implement 0/1 Knapsack Problem through Dynamic Programming and Least-Cost Branch & Bound"<<endl;
    cout<<"================================================================================================================="<<endl;
    
    int n = 4;
    
    cout<<"Following are the constraints:"<<endl;
    cout<<"-----------------------------------------------------------------------------------------------------------------"<<endl;
    cout<<"Number of Items: "<<n<<endl;
    cout<<"Weight Range: 1-"<<2*n<<endl;
    cout<<"Profit Range: 1-"<<3*n<<endl;
    cout<<"Capacity Range: 1-"<<5*n<<endl;
    cout<<"================================================================================================================="<<endl;

    cout<<"Select an option: "<<endl;
    cout<<"1. Display steps for n = 4"<<endl;
    cout<<"2. Display execution time for n = 4"<<endl;

    int option;
    cout<<"Option: ";
    std::cin>>option;
    cout<<"================================================================================================================="<<endl;

    if(option == 1) {
        displaySteps(n);
    }
    else if(option == 2) {
        auto averageDP = 0.0;
        auto averageLCBB = 0.0;
        float loops = 10.0;
        
        for (int i = 0; i < loops; i++) {
            cout<<"INSTANCE # "<<i+1<<endl;
            cout<<"-----------------------------------------------------------------------------------------------------------------"<<endl;
            displayExecutionTime(n, averageDP, averageLCBB);
        }
        
        // Calculating average execution time
        averageDP /= loops;
        averageLCBB /= loops;

        cout<<"Average Execution Time for Dynamic Programming: "<<averageDP<<" nanoseconds or "<<averageDP/1000.0<<" microseconds"<<endl;
        cout<<"Average Execution Time for Least-Cost Branch & Bound: "<<averageLCBB<<" nanoseconds or "<<averageLCBB/1000.0<<" microseconds"<<endl;
    }
    else
        cout<<"Wrong Input"<<endl;

    cout<<"================================================================================================================="<<endl;
}

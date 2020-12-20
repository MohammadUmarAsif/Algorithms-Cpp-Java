// Including libraries and files
#include <iostream>
#include <vector>
#include <math.h>
#include <cstdlib> 
#include <ctime>

// Using frequently used entities from std namespace
using std::vector;
using std::cout;
using std::endl;
using std::cin;

// Function to compute the largest number in the list
int compute_largest_number(const vector<int> &list) {
    
    int largest = list[0];
    
    for(int i=1; i<list.size(); i++) {
        if(list[i]>largest)
            largest = list[i];
    }

    return largest;
}

// Function to compute the number of digits in the largest number in the list
int compute_number_of_digits(vector<int> list) {
    
    int largest = compute_largest_number(list);
    int total_digits = 0;

    while(largest>0) {
        largest = largest/10;
        total_digits += 1;
    }

    return total_digits;
}

// Function to print a list
void print_list(const vector<int> &list) {
    
    for(int i=0; i<list.size(); i++) {
        cout<<list[i]<<" ";
    }

    cout<<endl;
}

// Function to perform counting sort on the list for LSD implementation
vector<int> perform_counting_sort(vector<int> &list, int radix, int total_digits) {
    
    vector<int> frequency (radix,0);
    vector<int> sorted_list (list.size());
    
    // Incrementing the frequency list at indexes according to number at total_digit's place
    for(int i=0; i<list.size(); i++) {
        int index = int(floor(list[i]/int(pow(radix,total_digits)))) % radix;
        frequency[index] += 1;
    }

    // Obtaining cumulative frequency
    for(int i=1; i<frequency.size(); i++) {
        frequency[i] += frequency[i-1];
    }

    // Decrementing frequency list and allocating number to suitable index in output list
    for(int i=list.size()-1; i>=0; i--) {
        int index = int(floor(list[i]/int(pow(radix,total_digits)))) % radix;
        frequency[index] -= 1;
        sorted_list[frequency[index]] = list[i];
    }
    
    return sorted_list;
}

// Function to compute radix sort (LSD) and print sorted list
void compute_radix_sort_lsd(vector<int> list, int radix = 10) {
    
    int total_digits = compute_number_of_digits(list);
    
    for(int i=0; i<total_digits; i++) { 
        list = perform_counting_sort(list, radix, i);
    }

    print_list(list);
}

// Function to join two buckets into one
void join_buckets(vector<int> &bucket_one, const vector<int> &bucket_two) {
    
    for(int i=0; i<bucket_two.size(); i++) {
        bucket_one.push_back(bucket_two[i]);
    }
}

// Function to perform recursive sorting on the list for MSD implementation
vector<int> perform_recursive_sort(vector<int> &list, int total_digits, int radix) {  
    
    // Breaking condition for recursion
    if (list.size() < 2 || total_digits == 0) {
        return list;
    }
    else {

    	// On first call of the function
        if (total_digits == -1) {
            total_digits = compute_number_of_digits(list);    
        }
        
        vector<vector<int>> buckets (radix);
        
        // Calculating suitable index and adding number to bucket of that index
        for(int i=0; i<list.size(); i++) {
            int index = int(floor(list[i]/int(pow(radix,(total_digits-1))))) % radix;
            buckets[index].push_back(list[i]);            
        }

        vector<int> sorted_list;

        // Recursively calling the function for each bucket of size>0
        for(int i=0; i<buckets.size(); i++) {
            if(buckets[i].size()>0) {
                join_buckets(sorted_list, perform_recursive_sort(buckets[i], total_digits-1, radix));
            }       
        }
       
        return sorted_list;
    }
}

// Function to compute radix sort (MSD) and print sorted list
void compute_radix_sort_msd(vector<int> list, int radix = 10) {
    
    vector<int> sorted_list;

    sorted_list = perform_recursive_sort(list, -1, radix);
 
    print_list(sorted_list);
}

// Function to display menu, take input and generate list
void display_menu(vector<int> &list) {
    
    int option;

    cout<<"This is a C++ program to implement radix sort in LSD (Least Significant Digit) and MSD (Most Significant Digit)"<<endl;
    cout<<"==============================================================================================================="<<endl;
    cout<<"The constraints for this demonstration are as follows:"<<endl<<"Range: 0-9999"<<endl<<"List size: 10"<<endl;
    cout<<"==============================================================================================================="<<endl;
    cout<<"Choose an input method: "<<endl;
    cout<<"1. User input"<<endl<<"2. Random numbers"<<endl;

    bool flag = true;

    while(flag) {
        
        cout<<"Your Input: ";
        cin>>option;
        cout<<"==============================================================================================================="<<endl;

        switch(option) {

        	// Generating list based on user input
            case 1: {
                cout<<"Enter the 10 numbers (seperated by spaces):"<<endl;
                
                for(int i=0; i<10; i++) {
                    int num;
                    cin>>num;
                    list.push_back(num);
                }
                
                flag = false;

                cout<<"Input list:"<<endl;
                print_list(list);

            }
            break;

            // Generating a random list on every program run
            case 2: {
                srand((unsigned) time(0));
        
                for(int i=0; i<10; i++) {
                    list.push_back(rand()%10000);
                }

                flag = false;

                cout<<"Input list:"<<endl;
                print_list(list);
            }
            break;
            default: {
                cout<<"Please enter either 1 or 2"<<endl;
            }
        }
    }
}

// Main function
int main() {
    
    vector<int> list;

    display_menu(list);

    cout<<"Sorted list (By LSD):"<<endl;
    compute_radix_sort_lsd(list);

    cout<<"Sorted list (By MSD):"<<endl;
    compute_radix_sort_msd(list);
    
    cout<<"==============================================================================================================="<<endl;

    return 0;
}

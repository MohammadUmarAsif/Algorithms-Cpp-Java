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

// Function to perform counting sort on the list for LSD implementation
vector<int> perform_counting_sort(vector<int> &list, int &flag_one, int &flag_two, int &flag_three, int radix, int total_digits) {
    
    vector<int> frequency (radix,0);
    vector<int> sorted_list (list.size());
    
    // Incrementing the frequency list at indexes according to number at total_digit's place
    for(int i=0; i<list.size(); i++) {
        int index = int(floor(list[i]/int(pow(radix,total_digits)))) % radix;
        frequency[index] += 1;
        // Flag to count index calculation and increment
        flag_one += 2;
    }

    // Obtaining cumulative frequency
    for(int i=1; i<frequency.size(); i++) {
        frequency[i] += frequency[i-1];
        // Flag to count adding up of frequencies
        flag_two += 1;
    }

    // Decrementing frequency list and allocating number to suitable index in output list
    for(int i=list.size()-1; i>=0; i--) {
        int index = int(floor(list[i]/int(pow(radix,total_digits)))) % radix;
        frequency[index] -= 1;
        sorted_list[frequency[index]] = list[i];
        // Flag to count index calculation, decrement and allocation of number
       	flag_three += 3;
    }
    
    return sorted_list;
}

// Function to compute radix sort (LSD) and calculate total key operations
int compute_radix_sort_lsd(vector<int> list, int radix = 10) {
    
    int total_digits = compute_number_of_digits(list);
    
    int flag_one = 0;
    int flag_two = 0;
    int flag_three = 0;

    // Flag to count perform_counting_sort calls and returns
    int flag_four = total_digits*2;
    
    for(int i=0; i<total_digits; i++) { 
        list = perform_counting_sort(list, flag_one, flag_two, flag_three, radix, i);
    }
    
    return flag_one + flag_two + flag_three + flag_four;
}

// Function to join two buckets into one
void join_buckets(vector<int> &bucket_one, const vector<int> &bucket_two, int &flag_four) {
    
    for(int i=0; i<bucket_two.size(); i++) {
        bucket_one.push_back(bucket_two[i]);

        // Flag to count allocations during joining
        flag_four += 1;
    }
}

// Function to perform recursive sorting on the list for MSD implementation
vector<int> perform_recursive_sort(vector<int> &list, int total_digits, int &flag_one, int &flag_two, int &flag_three, int &flag_four, int &flag_five, int &flag_six, int radix) {  
    
	// Flag to count breaking 'if' condition checks
    flag_six += 1;
    
    // Breaking condition for recursion
    if (list.size() < 2 || total_digits == 0) {

    	// Flag to count returns of perform_recursive_sort function
        flag_two += 1;
        
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

            // Flag to count index calculation and allocation
            flag_one += 2;   
        }
        
        vector<int> sorted_list;

        // Recursively calling the function for each bucket of size>0
        for(int i=0; i<buckets.size(); i++) {

        	// Flag to count 'if' condition checks
            flag_five += 1;

            if(buckets[i].size()>0) {

            	//Flag to count calls and returns of join_buckets function
                flag_three += 2;
                join_buckets(sorted_list, perform_recursive_sort(buckets[i], total_digits-1, flag_one, flag_two, flag_three, flag_four, flag_five, flag_six, radix), flag_four);
            }       
        }

        // Flag to count returns of perform_recursive_sort function
        flag_two += 1;

        return sorted_list;
    }
}

// Function to compute radix sort (MSD) and calculate total key operations
int compute_radix_sort_msd(vector<int> list, int radix = 10) {
    
    vector<int> sorted_list;
    
    int flag_one = 0;
    int flag_two = 0;
    int flag_three = 0;
    int flag_four = 0;
    int flag_five = 0;
    int flag_six = 0;
    
    sorted_list = perform_recursive_sort(list, -1, flag_one, flag_two, flag_three, flag_four, flag_five, flag_six, radix);
    
    return flag_one + flag_two*2 + flag_three + flag_four + flag_five + flag_six;
}

// Function to display menu
void display_menu() {
    
	cout<<"This is a C++ program to analyze the complexity of radix sort in LSD (Least Significant Digit) and MSD (Most Significant Digit)"<<endl;
    cout<<"==============================================================================================================================="<<endl;
    cout<<"The constraints for this demonstration are as follows:"<<endl<<"Range: 0-9999"<<endl<<"Number of inputs: 100 to 1000"<<endl;
    cout<<"==============================================================================================================================="<<endl;
}

// Main Function
int main() {
    
    int lsd_operations = 0;
    int msd_operations = 0;

    display_menu();

    // Performing sorting by LSD and MSD with list size starting from 100, 200, 300 ... upto 1000
    for(int i=100; i<=1000; i += 100) {
    	vector<int> list;

    	srand((unsigned) time(0));
		
		for(int j=0; j<i; j++) {
		    list.push_back(rand()%10000);
		}
		
		lsd_operations = compute_radix_sort_lsd(list);
		msd_operations = compute_radix_sort_msd(list);

		cout<<"Number of inputs: "<<i<<" | LSD: "<<lsd_operations<<" | MSD: "<<msd_operations<<endl;
    }

    cout<<"==============================================================================================================================="<<endl;

    return 0;
}

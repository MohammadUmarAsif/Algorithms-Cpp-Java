// Including required header files
#include <iostream>
#include <vector>
#include <chrono>
#include <random>
#include <regex>
#include <string>
#include <iomanip>
#include <climits>

// Including required entities from std namespace
using std::cout;
using std::cin;
using std::endl;
using std::vector;
using std::setw;

// Function to generate random reference string
void generate_random_string (vector<int> &reference_string, int &total_references, int &total_pages) {
    
    // Setting random seed and type of distribution
    std::default_random_engine generator; 
    generator.seed(std::chrono::system_clock::now().time_since_epoch().count());
    std::uniform_int_distribution<int> page (1, total_pages);

    for(int i=0; i<total_references; i++) {
        reference_string.push_back(page(generator));
        
        // Preventing contiguous references to the same page
        if (i > 0)
            while(reference_string[i] == reference_string[i-1])
                reference_string[i] = page(generator);
    }
}

// Function to generate reference string through user input
void generate_user_string (vector<int> &reference_string, int &total_references) {
    
    cout<<"Enter the reference string:"<<endl;
    cout<<"[Format: page# page# page#] (eg 2 1 4) (Page numbering starts from 1)"<<endl;
    cout<<"------------------------------------------------------------------------------------"<<endl;

    std::string input;
    cin.ignore();
    getline(cin, input);
    
    input = " " + input;
    std::string matcher = "";
    
    // Extending pattern as per total references
    for (int i=0; i<total_references; i++)
        matcher += "(\\s\\d)";
     
    std::regex pattern (matcher);
    std::smatch sm;
    
    // Adding each matched page number to the reference string
    if (std::regex_match (input, sm, pattern))
        for (int i=1; i<=total_references; i++)
            reference_string.push_back(stoi(sm.str(i)));
}

// Function to check if an element is present in a list
bool search_list (vector<int> &list, int &x) {
    for (int i=0; i<list.size(); i++)
        if (x == list[i])
            return true;
            
    return false;
}

// Function to find the index of an element in a list
int find_index (vector<int> &list, int &x) {
    for (int i=0; i<list.size(); i++)
        if (x == list[i])
            return i;
            
    return -1;
}

// Function to display the page list in ascending or descending order
void display_page_list (vector<int> &page_list, bool ascending) {
    cout<<"Page List   = |";
    
    if (ascending)
        for (int i=0; i<page_list.size(); i++)
            cout<<setw(5)<<page_list[i]<<"|";
    else
        for (int i=page_list.size()-1; i>=0; i--)
            cout<<setw(5)<<page_list[i]<<"|";
    cout<<endl;
}

// Function to display the frame contents along with their indices
void display_frame_list (vector<int> &frame_list, int &total_frames) {
    cout<<"Page Frames = |";
    
    for (int i=0; i<total_frames; i++)
        if (i < frame_list.size())
            cout<<setw(5)<<frame_list[i]<<"|";
        else
            cout<<setw(5)<<"X"<<"|";
    cout<<endl;

    cout<<setw(14)<<" "<<"|";
    for (int i=0; i<total_frames; i++)
        cout<<"#"<<setw(4)<<i+1<<"|";
    cout<<endl;
}

// Function to implement the FIFO page replacement algorithm
int fifo_algorithm (vector<int> &reference_string, int total_frames, vector<int> &frame_list, bool display, bool faults) {
    
    // Lists acts as a fifo queue
    vector<int> page_list;
    int page_faults = 0;
    
    // Iterating over the reference string
    for (int i=0; i<reference_string.size(); i++) {
        cout<<"Page Number - "<<reference_string[i]<<endl;
        
        // If page is already present in the queue, continue to next page
        if (search_list(page_list, reference_string[i])) {
            cout<<"Page is already present in frame #"<<find_index(frame_list, reference_string[i])+1<<"!"<<endl;
            cout<<"------------------------------------------------------------------------------------"<<endl;
            continue;
        }

        cout<<"Page fault has occured!"<<endl<<endl;
        page_faults++;
        
        // To handle page fault, if free frame is available, add page to the free frame and queue
        if (page_list.size() != total_frames) {
            cout<<"Pushing page onto the end of the queue and allocating it to frame#"<<frame_list.size()+1<<endl;
            frame_list.push_back(reference_string[i]);
            page_list.push_back(reference_string[i]);
            
            display_page_list(page_list, true);
            if (display)
                display_frame_list(frame_list, total_frames);
        }
        // Else, replace the longest-lasting page i.e. page at the head of the queue
        else {
            cout<<"Removing page at the head of the queue."<<endl;
            
            frame_list[find_index(frame_list,page_list[0])] = reference_string[i];
            page_list.erase(page_list.begin());
            
            display_page_list(page_list, true);
            
            cout<<"Adding new page at the tail of the queue."<<endl;
            page_list.push_back(reference_string[i]);
            
            display_page_list(page_list, true);
            if (display)
                display_frame_list(frame_list, total_frames);
        }

        cout<<"------------------------------------------------------------------------------------"<<endl;
    }
    
    if (faults)
        cout<<"Total Page Faults = "<<page_faults<<endl;
    else
        cout<<"Algorithm Complete."<<endl;
    
    return page_faults;
}

// Function to implement the LRU page replacement algorithm
int lru_algorithm (vector<int> &reference_string, int total_frames, vector<int> &frame_list, bool display, bool faults) {
    
    // Lists acts as a stack
    vector<int> page_list;
    int page_faults = 0;
    
    // Iterating over the reference string
    for (int i=0; i<reference_string.size(); i++) {
        cout<<"Page Number - "<<reference_string[i]<<endl;
        
        // If page is already present in the queue, bring it to the top of stack and continue to next page
        if (search_list(page_list, reference_string[i])) {
            cout<<"Page is already present in frame #"<<find_index(frame_list, reference_string[i])+1<<"!"<<endl;
            cout<<"Page is brought to the top of the stack."<<endl;
            
            page_list.erase(page_list.begin()+find_index(page_list, reference_string[i]));
            page_list.push_back(reference_string[i]);
            
            display_page_list(page_list, false);
            
            cout<<"------------------------------------------------------------------------------------"<<endl;
            continue;
        }

        cout<<"Page fault has occured!"<<endl<<endl;
        page_faults++;
        
        // To handle page fault, if free frame is available, add page to the free frame and insert at top of stack       
        if (page_list.size() != total_frames) {
            cout<<"Pushing page to the top of the stack and allocating it to frame#"<<frame_list.size()+1<<endl;
            frame_list.push_back(reference_string[i]);
            page_list.push_back(reference_string[i]);

            display_page_list(page_list, false);
            if (display)
                display_frame_list(frame_list, total_frames);
        }
        // Else, replace the least-recently-used page i.e. page at the bottom of the stack
        else {
            cout<<"Removing least recently used page at bottom of the stack."<<endl;
            
            frame_list[find_index(frame_list,page_list[0])] = reference_string[i];
            page_list.erase(page_list.begin());
            
            display_page_list(page_list, false);
            
            cout<<"Adding new page at the top of the stack."<<endl;
            page_list.push_back(reference_string[i]);
            
            display_page_list(page_list, false);
            if (display)
                display_frame_list(frame_list, total_frames);
        }

        cout<<"------------------------------------------------------------------------------------"<<endl;
    }
    
    if (faults)
        cout<<"Total Page Faults = "<<page_faults<<endl;
    else
        cout<<"Algorithm Complete."<<endl;

    return page_faults;
}

// Function to find the page that will not be referenced for the longest time
int longest_not_used (vector<int> &reference_string, int starting_index, vector<int> &frame_list) {
    
    // Duration = Keeps the number of counts until a page is referenced
    // Found = Sets to true when the first reference to a page is found
    vector<int> duration(frame_list.size(), 0);
    vector<bool> found(frame_list.size(), false);
    int count = 0;
    
    // Iterating over the remaining string
    for (int i=starting_index; i<reference_string.size(); i++) {
        int index = find_index(frame_list, reference_string[i]);
        count++;

        // Page should be in the frames
        if (index != -1) {

            // Page should not be found
            if (!found[index]) {
                duration[index] = count;
                found[index] = true;
            }
        }
    }
    
    // If Duration[i] = 0, page is never used again, so set count to INT_MAX
    std::replace(duration.begin(),  duration.end(), 0, INT_MAX);
    
    int longest_duration = duration[0];
    int frame = 0;
    
    // Finding the frame which contains the page that is not used for the longest time
    for (int i=1; i<duration.size(); i++) {
        if (longest_duration < duration[i]) {
            longest_duration = duration[i];
            frame = i;
        }
    }
    
    return frame;
}

// Function to implement the Optimal page replacement algorithm
int optimal_algorithm (vector<int> &reference_string, int total_frames, vector<int> &frame_list, bool display, bool faults) {
    int page_faults = 0;
    
    // Iterating over the reference string
    for (int i=0; i<reference_string.size(); i++) {
        cout<<"Page Number - "<<reference_string[i]<<endl;
        
        // If page is already present in the queue, continue to next page
        if (search_list(frame_list, reference_string[i])) {
            cout<<"Page is already present in frame #"<<find_index(frame_list, reference_string[i])+1<<"!"<<endl;
            cout<<"------------------------------------------------------------------------------------"<<endl;
            continue;
        }

        cout<<"Page fault has occured!"<<endl<<endl;
        page_faults++;
        
        // To handle page fault, if free frame is available, add page to the free frame
        if (frame_list.size() != total_frames) {
            cout<<"Allocating page to frame#"<<frame_list.size()+1<<endl;
            frame_list.push_back(reference_string[i]);
            
            if (display)
                display_frame_list(frame_list, total_frames);
        }
        // Else, replace the longest-not-used page
        else {
            cout<<"Finding the page that will not be used for the longest period of time."<<endl;
            
            int frame = longest_not_used(reference_string, i+1, frame_list);
            
            cout<<"The page to be replaced is page #"<<frame_list[frame]<<"."<<endl;
            
            frame_list[frame] = reference_string[i];
            
            if (display)
                display_frame_list(frame_list, total_frames);
        }

        cout<<"------------------------------------------------------------------------------------"<<endl;
    }

    if (faults)
        cout<<"Total Page Faults = "<<page_faults<<endl;
    else
        cout<<"Algorithm Complete."<<endl;

    return page_faults;
}

// Main function
int main() {
    
    cout<<"This is a C++ program to implement page replacement algorithms (FIFO, LRU & Optimal)"<<endl;
    cout<<"===================================================================================="<<endl;

    cout<<"Select an Input Method: "<<endl;
    cout<<"1. User Input"<<endl;
    cout<<"2. Random Input"<<endl;

    int option;
    cout<<"Your Selection: ";
    cin>>option;
    cout<<"===================================================================================="<<endl;
    
    int total_frames;
    int total_references;
    int total_pages;

    vector<int> reference_string;
    vector<int> frame_list;
    cout<<std::left;

    cout<<"Enter the number of frames: ";
    cin>>total_frames;
    cout<<"Enter the number of references: ";
    cin>>total_references;

    // Asking if page frames should be displayed or not
    char input;
    cout<<"Should page frames be shown? (y = yes | n = no)"<<endl;
    cout<<"Your Selection: ";
    cin>>input;
    bool display = (input == 'y')?true:false;

    // Asking if page faults should be shown
    cout<<"Should page faults be shown? (y = yes | n = no)"<<endl;
    cout<<"Your Selection: ";
    cin>>input;
    bool faults = (input == 'y')?true:false;

    // Taking user input or random input
    if (option == 1) {
        generate_user_string(reference_string, total_references);
    }
    else if (option == 2) {
        cout<<"Enter the number of pages: ";
        cin>>total_pages;

        generate_random_string(reference_string, total_references, total_pages);
        
        cout<<"Generated reference string: ";
        for (int i=0; i<reference_string.size(); i++)
            cout<<reference_string[i]<<" ";
        cout<<endl;
    }

    // Implementing First-In-First-Out Algorithm on the reference string
    cout<<"===================================================================================="<<endl;
    cout<<"                            First-In-First-Out Algorithm                            "<<endl;
    cout<<"===================================================================================="<<endl;
    int fifo_faults = fifo_algorithm(reference_string, total_frames, frame_list, display, faults);
    
    frame_list.clear();
    
    // Implementing Least-Recently-Used Algorithm on the reference string
    cout<<"===================================================================================="<<endl;
    cout<<"                            Least-Recently-Used Algorithm                           "<<endl;
    cout<<"===================================================================================="<<endl;
    int lru_faults = lru_algorithm(reference_string, total_frames, frame_list, display, faults);
    
    frame_list.clear();
    
    // Implementing Optimal Algorithm on the reference string
    cout<<"===================================================================================="<<endl;
    cout<<"                                  Optimal Algorithm                                 "<<endl;
    cout<<"===================================================================================="<<endl;
    int optimal_faults = optimal_algorithm(reference_string, total_frames, frame_list, display, faults);
    
    if (faults) {
        cout<<"===================================================================================="<<endl;
        cout<<"                               Page Faults Comparison                               "<<endl;
        cout<<"===================================================================================="<<endl;
        
        // Sorting the resulting page faults by value in ascending order
        vector<int> order {fifo_faults, lru_faults, optimal_faults};
        vector<bool> shown (3, false);
        std::sort(order.begin(), order.end());
        
        // Displaying page faults with corresponding algorithm
        for (int i=0; i<order.size(); i++) {
            if (order[i] == optimal_faults && !shown[2]) {
                cout<<"Total Page Faults in Optimal = "<<order[i]<<endl;
                shown[2] = true;
            }
            else if (order[i] == lru_faults && !shown[1]) {
                cout<<"Total Page Faults in LRU = "<<order[i]<<endl;
                shown[1] = true;
            }
            else if (order[i] == fifo_faults && !shown[0]) {
                cout<<"Total Page Faults in FIFO = "<<order[i]<<endl;
                shown[0] = true;
            }
        }
    }
    
    cout<<"===================================================================================="<<endl;
}

// Importing required classes
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Algorithm {
	
	// Function to generate a random resource allocation state
	static void random_allocation (int total_resources, ArrayList<String>resource_names, ArrayList<Integer> resource_instances,
			int total_processes, ArrayList<ArrayList<Integer>> max, ArrayList<ArrayList<Integer>> allocation) {
		
		System.out.println("===========================================================================================================");
		System.out.println("NOTES:");
		System.out.println("1. Total instances for a resource lie in [0,15]");
		System.out.println("2. Maximum instances of a resource for a process lie in [0,10]");
		System.out.println("3. Starting resource-allocations for a process lie in [0,3]");
		System.out.println("===========================================================================================================");
		
		Random rand = new Random();
		
		// Generating resource types + instances
		System.out.println("Resource Types & Their Instances:");
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		for (char i='A'; i<'A'+total_resources; i++) {
			resource_names.add(Character.toString(i));
			resource_instances.add(rand.nextInt(16));
			
			System.out.println(i + ": " + resource_instances.get(resource_instances.size()-1));
		}
		
		// Generating maximum demands of each resource type for all processes
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println("Maximum Demand of Each Process:");
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		for (int i=0; i<total_processes; i++) {		
			max.add(new ArrayList<Integer>(total_resources));
			
			System.out.print("Process "+ i + ": ");
			
			for (int j=0; j<total_resources; j++) {
				max.get(i).add(rand.nextInt(11));
				System.out.print(max.get(i).get(j) + " ");
			}
			
			System.out.println();
		}
		
		// Generating current allocation of each resource type for all processes
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println("Current Allocation of Each Process:");
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		for (int i=0; i<total_processes; i++) {
			allocation.add(new ArrayList<Integer>(total_resources));
				
			System.out.print("Process "+ i + ": ");
			
			for (int j=0; j<total_resources; j++) {
				int temp = rand.nextInt(3);
				
				int sum = 0;
				for (int k=0; k<allocation.size()-1; k++)
					sum += allocation.get(k).get(j);
				
				// To ensure that sum of allocations of all processes does not exceed maximum instances, for a resource type
				while (sum + temp > resource_instances.get(j) || temp > max.get(i).get(j))
					temp = rand.nextInt(3);
				
				allocation.get(i).add(temp);
				System.out.print(allocation.get(i).get(j) + " ");
			}
			
			System.out.println();
		}
	}
	
	// Function to generate resource allocation state from user input
	static void user_input_allocation (int total_resources, ArrayList<String>resource_names, ArrayList<Integer> resource_instances,
			int total_processes, ArrayList<ArrayList<Integer>> max, ArrayList<ArrayList<Integer>> allocation, Scanner keyboard) {
		
		System.out.println("===========================================================================================================");
		System.out.println("Enter the resource types & their instances:");
		System.out.println("[Format: ResourceName = NumberOfInstances (eg A = 4)]");
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		
		// Obtaining resource types + instances
		for (int i=0; i<total_resources; i++) {
			Pattern pattern = Pattern.compile("(\\w+)\\s=\\s(\\d+)");
			String input = keyboard.findInLine(pattern);
			keyboard.nextLine();
			Matcher match = pattern.matcher(input);
			
			if (match.find()) {
				resource_names.add(match.group(1));
				resource_instances.add(Integer.parseInt(match.group(2)));
			}
		}
		
		// Defining input pattern
		String input_pattern = "(\\d+)\\s".repeat(total_resources);
		input_pattern = input_pattern.substring(0, input_pattern.length()-2);
		
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println("Enter the maximum demand of each process:");
		System.out.println("[Format: MaxDemandForResource1 MaxDemandForResource2  (eg 3 4)]");
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		
		// Obtaining maximum demands of each resource type for all processes
		for (int i=0; i<total_processes; i++) {
			System.out.print("Process " + i + ": ");
			
			Pattern pattern = Pattern.compile(input_pattern);
			String input = keyboard.findInLine(pattern);
			keyboard.nextLine();
			Matcher match = pattern.matcher(input);
			
			if (match.find()) {
				max.add(new ArrayList<Integer>(total_resources));
				
				for (int j=0; j<total_resources; j++)
					max.get(i).add(Integer.parseInt(match.group(j+1)));
			}
		}
		
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println("Enter the current allocation of each process:");
		System.out.println("[Format: AllocationForResource1 AllocationForResource2 (eg 0 1)]");
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		
		// Obtaining current allocation of each resource type for all processes
		for (int i=0; i<total_processes; i++) {
			
			System.out.print("Process " + i + ": ");
			
			Pattern pattern = Pattern.compile(input_pattern);
			String input = keyboard.findInLine(pattern);
			keyboard.nextLine();
			Matcher match = pattern.matcher(input);
			
			if (match.find()) {
				allocation.add(new ArrayList<Integer>(total_resources));
				
				for (int j=0; j<total_resources; j++)
					allocation.get(i).add(Integer.parseInt(match.group(j+1)));
			}
		}	
	}
	
	// Function to implement safety algorithm (part of Banker's Algorithm)
	static boolean safety_algorithm(ArrayList<ArrayList<Integer>> allocation, ArrayList<Integer> available,
			ArrayList<ArrayList<Integer>> need, ArrayList<Integer> safe_sequence, ArrayList<String> resource_names) {
		
		ArrayList<Integer> work = new ArrayList<Integer>();
		ArrayList<Boolean> finish = new ArrayList<Boolean>();
		
		System.out.println("Initialized Work & Finish vectors:");
		System.out.println();
		
		// Initializing Work + Finish vectors
		System.out.print("Work: ");
		for (int i=0; i<available.size(); i++) {
			work.add(available.get(i));
			System.out.print(work.get(i) + " ");
		}
		System.out.println();
		System.out.print("Finish: ");
		for (int i=0; i<allocation.size(); i++) {
			finish.add(false);
			System.out.print(finish.get(i) + " ");
		}
		System.out.println();
		
		for (int i=0; i<allocation.size(); i++) {
			for (int j=0; j<allocation.size(); j++) {
			
				boolean flag = true;
				
				for (int k=0; k<available.size(); k++)
					if (need.get(j).get(k) > work.get(k))
						flag = false;
				
				// If process has not finished & process' needs are less than current work
				if (!finish.get(j) && flag) {
					System.out.println("-----------------------------------------------------------------------------------------------------------");
					System.out.println("Process " + j + " can be allocated resources (Finished == false & Needs(" + j +  ")" + " <= Work):");
					System.out.println();
					System.out.println("Work = Work + Allocation(" + j + ")");
					
					// Update work & set process as finished
					for (int k=0; k<available.size(); k++) {
						System.out.print("Work(" + resource_names.get(k) + ") = " + work.get(k) + " + " + allocation.get(j).get(k) + " = ");
						
						work.set(k, work.get(k) + allocation.get(j).get(k));
						
						System.out.print(work.get(k));
						System.out.println();
					}
					
					finish.set(j, true);
					
					// Add process to safe sequence
					safe_sequence.add(j);
					
					System.out.println("Finish(" + j + ") = true");
					System.out.println();
					System.out.print("Work: ");
					for (int k=0; k<work.size(); k++) {
						System.out.print(work.get(k) + " ");
					}
					System.out.println();
					System.out.print("Finish: ");
					for (int k=0; k<finish.size(); k++) {
						System.out.print(finish.get(k) + " ");
					}
					System.out.println();
				}
				// Check if all processes have been finished
				else if(!finish.contains(false))
					return true;
			}
		}
		
		return false;
	}

	// Function to generate request vector from user input
	static void request_input(ArrayList<Integer> request_vector, int total_resources, Scanner keyboard) {
		
		// Defining input pattern
		String input_pattern = "(\\d+)\\s".repeat(total_resources);
		input_pattern = input_pattern.substring(0, input_pattern.length()-2);
		input_pattern = "\\w(\\d+)\\s=\\s" + input_pattern;
		
		System.out.println("Enter the request vector:");
		System.out.println("[Format: Process# = #InstancesOfResource1 #InstancesOfResource2  (eg P3 = 0 2)]");

		Pattern pattern = Pattern.compile(input_pattern);
		String input = keyboard.findInLine(pattern);
		keyboard.nextLine();
		Matcher match = pattern.matcher(input);
		
		// Obtaining request vector
		if (match.find())
			for (int j=0; j<total_resources+1; j++)
				request_vector.add(Integer.parseInt(match.group(j+1)));
	}
	
	// Function to allocate resource instances & modify state if it is safe 
	static List<Object> temporary_resource_allocation(ArrayList<Integer> request_vector, ArrayList<ArrayList<Integer>> allocation,
			ArrayList<Integer> available, ArrayList<ArrayList<Integer>> need, ArrayList<String> resource_names) {
		
		ArrayList<ArrayList<Integer>> temp_allocation = new ArrayList<ArrayList<Integer>> ();
		ArrayList<Integer> temp_available = new ArrayList<Integer> ();
		ArrayList<ArrayList<Integer>> temp_need = new ArrayList<ArrayList<Integer>> ();
		
		// Defining temporary structures
		for (int j=0; j<allocation.size(); j++) {
			temp_allocation.add(new ArrayList<Integer>());
			temp_need.add(new ArrayList<Integer>());
			
			for (int k=0; k<allocation.get(j).size(); k++) {
				temp_allocation.get(j).add(allocation.get(j).get(k));
				temp_need.get(j).add(need.get(j).get(k));
			}
		}
		
		for (int j=0; j<available.size(); j++)
			temp_available.add(available.get(j));
		
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println("Temporary structures to store Available, Allocation & Need have been created.");
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		// Obtaining process number
		int i = request_vector.get(0);
		
		// Update Available = Available - Request
		System.out.println("Available = Available - Request");
		for (int j=0; j<available.size(); j++) {
			System.out.print("Available(" + resource_names.get(j) + ") = "+ temp_available.get(j) + " - " + request_vector.get(j+1) + " = ");
		
			temp_available.set(j, temp_available.get(j) - request_vector.get(j+1));
		
			System.out.print(temp_available.get(j));
			System.out.println();
		}
		System.out.println();
		
		// Update Allocation(i) = Allocation(i) + Request
		System.out.println("Allocation("+i+") = Allocation("+i+") + Request");
		for (int j=0; j<available.size(); j++) {
			System.out.print("Allocation(" +  i + ")(" +resource_names.get(j) + ") = "+ temp_allocation.get(i).get(j) + " + ");
			System.out.print(request_vector.get(j+1) + " = ");
			
			temp_allocation.get(i).set(j, temp_allocation.get(i).get(j) + request_vector.get(j+1));
			
			System.out.print(temp_allocation.get(i).get(j));
			System.out.println();
		}
		System.out.println();
		
		// Update Need(i) = Need(i) - Request
		System.out.println("Need("+i+") = Need("+i+") - Request");
		for (int j=0; j<available.size(); j++) {
			System.out.print("Need(" +  i + ")(" +resource_names.get(j) + ") = "+ temp_need.get(i).get(j) + " - ");
			System.out.print(request_vector.get(j+1) + " = ");
			
			temp_need.get(i).set(j, temp_need.get(i).get(j) - request_vector.get(j+1));
			
			System.out.print(temp_need.get(i).get(j));
			System.out.println();
		}

		System.out.println("===========================================================================================================");
		System.out.println("--------------------------CHECKING IF RESULTING RESOURCE-ALLOCATION STATE IS SAFE--------------------------");
		System.out.println("===========================================================================================================");
		
		// Checking if new state is safe
		ArrayList<Integer> safe_sequence = new ArrayList<Integer> ();
		boolean safe_state = safety_algorithm(temp_allocation, temp_available, temp_need, safe_sequence, resource_names);
			
		// If state is safe, display safe sequence
		if (safe_state) {
			System.out.println("-----------------------------------------------------------------------------------------------------------");
			System.out.println("System is in a safe state! Process has been allocated resources.");
			System.out.print("Safe Sequence: ");
			
			for (int j=0; j<safe_sequence.size(); j++) {
				if (j == safe_sequence.size()-1)
					System.out.print("P" + safe_sequence.get(j));
				else 
					System.out.print("P" + safe_sequence.get(j) + " -> ");
			}
			System.out.println();

			// Return updated structures
			return Arrays.asList(temp_allocation, temp_available, temp_need);
		}
		else {
			System.out.println("-----------------------------------------------------------------------------------------------------------");
			System.out.println("System is in an unsafe state! Process must wait for request & previous resource-allocation state is kept.");
			
			return null;
		}
	}
	
	// Function to implement request resource algorithm (part of Banker's Algorithm)
	static List<Object> request_resource_algorithm(ArrayList<Integer> request_vector, ArrayList<ArrayList<Integer>> need, 
			ArrayList<Integer> available, ArrayList<ArrayList<Integer>> allocation, ArrayList<String> resource_names) {
		
		// Obtaining process number
		int i = request_vector.get(0);
		
		System.out.println("Request made for Process " + i + ":");
		System.out.println();
		
		boolean flag_one = true;
		
		System.out.println("Checking for the condition of Request <= Need(" + i + ")");
		for (int j=0; j<available.size(); j++) {
			if (request_vector.get(j+1) > need.get(i).get(j))
				flag_one = false;
			
			System.out.print("Request(" + resource_names.get(j) + ") = " + request_vector.get(j+1));
			System.out.println(" & Need(" +  i + ")(" + resource_names.get(j) + ") = " + need.get(i).get(j));
		}
		
		// If Request <= Need(i)
		if (flag_one) {
			boolean flag_two = true;
			
			System.out.println("Condition satisfied!");
			System.out.println();
			System.out.println("Checking for the condition of Request <= Available");
			
			for (int j=0; j<available.size(); j++) {
				if (request_vector.get(j+1) > available.get(j))
					flag_two = false;
				
				System.out.print("Request(" + resource_names.get(j) + ") = " + request_vector.get(j+1));
				System.out.println(" & Available(" + resource_names.get(j) + ") = " + available.get(j));
			}
			
			// If Request <= Available
			if (flag_two) {
				System.out.println("Condition satisfied!");
				System.out.println();
				
				// Temporarily allocate resources and check if state is safe
				return temporary_resource_allocation(request_vector, allocation, available, need, resource_names);
			}
			else {
				System.out.println("Request(" + i + ") > Available (" + i + ")");
				System.out.println("Error: Process must wait as resources are not available!");
				
				return null;
			}
		}
		else {
			System.out.println("Request(" + i + ") > Need (" + i + ")");
			System.out.println("Error: Process exceeds its maximum claim!");
			
			return null;
		}
	}
	
	// Main function
	public static void main(String[] args) {
		
		System.out.println("-----------This is a Java program to implement Banker's Algorithm (Deadlock Avoidance Algorithm)-----------");
		System.out.println("===========================================================================================================");
		
		Scanner keyboard = new Scanner(System.in);
		
		// Obtaining number of resource types + processes
		System.out.print("Enter the number of resource types: ");
		int total_resources = keyboard.nextInt();
		keyboard.nextLine();
		System.out.print("Enter the number of processes: ");
		int total_processes = keyboard.nextInt();
		keyboard.nextLine();
		
		/* Declaring required structures
		 * resource_names = Name of each resource type
		 * resource_instances = # of instances of each resource type
		 * max = maximum # of instances of each resource type required by each process
		 * allocation = # of instances of each resource type allocated to each process
		 */
		ArrayList<String> resource_names = new ArrayList<String> (total_resources);
		ArrayList<Integer> resource_instances = new ArrayList<Integer> (total_resources);
		ArrayList<ArrayList<Integer>> max = new ArrayList<ArrayList<Integer>> (total_processes);
		ArrayList<ArrayList<Integer>> allocation = new ArrayList<ArrayList<Integer>> (total_processes);
		ArrayList<Integer> available = new ArrayList<Integer> (total_resources);
		ArrayList<ArrayList<Integer>> need = new ArrayList<ArrayList<Integer>> (total_processes);
		
		System.out.println("===========================================================================================================");
		System.out.println("Choose an input method:");
		System.out.println("1. User Input (Recommended)");
		System.out.println("2. Random Generation");
		System.out.print("Your Selection: ");
		int input = keyboard.nextInt();
		keyboard.nextLine();
		
		// Generating resource allocation state based on user preference
		if (input == 1)
			user_input_allocation(total_resources, resource_names, resource_instances, total_processes, max, allocation, keyboard);
		else if (input == 2)
			random_allocation(total_resources, resource_names, resource_instances, total_processes, max, allocation);
		
		// Calculating Available structure (# of instances of each resource type that are not allocated)
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println("Available Instances for each Resource Type:");
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		for (int i=0; i < total_resources; i++) {
			int sum = 0;
			
			for (int j=0; j < total_processes; j++)
				sum += allocation.get(j).get(i);
			
			available.add(resource_instances.get(i) - sum);
			System.out.println(resource_names.get(i) + " = " + available.get(i));
		}

		// Calculating Need structure (# of instances of each resource type that is additionally required by each process)
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		System.out.println("Number of Instances of Each Resource Type Needed by Each Process:");
		System.out.println("-----------------------------------------------------------------------------------------------------------");
		for (int i=0; i < total_processes; i++) {
			need.add(new ArrayList<Integer>(total_resources));
			
			System.out.print("Process " + i + ": ");
			
			for (int j=0; j < total_resources; j++) {
				need.get(i).add(max.get(i).get(j) - allocation.get(i).get(j));
				System.out.print(need.get(i).get(j) + " ");
			}
			
			System.out.println();
		}
		
		System.out.println("===========================================================================================================");
		System.out.println("                                             BANKER'S ALGORITHM                                            ");
		System.out.println("===========================================================================================================");
		System.out.println("------------------------------------CHECKING IF SYSTEM IS IN SAFE STATE------------------------------------");
		System.out.println("===========================================================================================================");
		
		// Checking if generated state is safe
		ArrayList<Integer> safe_sequence = new ArrayList<Integer>();
		boolean safe_state = safety_algorithm(allocation, available, need, safe_sequence, resource_names);
		
		if (safe_state) {
			System.out.println("-----------------------------------------------------------------------------------------------------------");
			System.out.println("System is in a safe state!");
			System.out.print("Safe Sequence: ");
			
			// Displaying safe sequence
			for (int i=0; i<safe_sequence.size(); i++) {
				if (i == safe_sequence.size()-1)
					System.out.print("P" + safe_sequence.get(i));
				else 
					System.out.print("P" + safe_sequence.get(i) + " -> ");
			}
			System.out.println();
			System.out.println("===========================================================================================================");
			
			int option;
			
			do {
				System.out.println("Would you like to enter a resource request? (1 = yes | 2 = no)");
				System.out.print("Your Answer: ");
				option = keyboard.nextInt();
				keyboard.nextLine();
						
				if (option == 1) {
					ArrayList<Integer> request_vector = new ArrayList<Integer> (total_resources);
					
					System.out.println("===========================================================================================================");
					System.out.println("----------------------------------------OBTAINING A RESOURCE REQUEST---------------------------------------");
					System.out.println("===========================================================================================================");
					
					// Obtaining resource request from user
					request_input(request_vector, total_resources, keyboard);
					
					System.out.println("===========================================================================================================");
					System.out.println("--------------------------------CHECKING IF RESOURCE REQUEST CAN BE GRANTED--------------------------------");
					System.out.println("===========================================================================================================");
					
					// Checking if request can be granted
					List<Object> new_state = request_resource_algorithm(request_vector, need, available, allocation, resource_names);	
					
					// If request can be granted update structures
					if (new_state != null) {
						allocation = (ArrayList<ArrayList<Integer>>) new_state.get(0);
						available = (ArrayList<Integer>) new_state.get(1);
						need = (ArrayList<ArrayList<Integer>>) new_state.get(2);
					}
					
					System.out.println("===========================================================================================================");
				}
				
			} while (option != 2);
		}
		else {
			System.out.println("-----------------------------------------------------------------------------------------------------------");
			System.out.println("System is in an unsafe state! Program terminated.");
		}
		
		keyboard.close();
		
		System.out.println("===========================================================================================================");
	}
}
// Importing required classes
import java.util.ArrayList;
import java.util.Stack;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Random;
import java.util.Scanner;

public class Scheduling_Algorithm {
	
	// Defining a Process class
	static class Process {
		
		// Attributes
		Integer id;
		Integer burst_time;
		Integer arrival_time;
		Integer priority;
		int execution;
		int start_time;
		int completion_time;
		int turn_around_time;
		int waiting_time;
		
		// Parameterized Constructor
		public Process (int id, Integer burst_time, Integer arrival_time, Integer priority) {
			this.id = id;
			this.burst_time = burst_time;
			this.arrival_time = arrival_time;
			this.priority = priority;
			this.execution = 0;
			this.start_time = 0;
			this.completion_time = 0;
			this.turn_around_time = 0;
			this.waiting_time = 0;
		}
		
		// Copy Constructor
		public Process (Process p) {
			this.id = p.id;
			this.burst_time = p.burst_time;
			this.arrival_time = p.arrival_time;
			this.priority = p.priority;
			this.execution = p.execution;
			this.start_time = p.start_time;
			this.completion_time = p.completion_time;
			this.turn_around_time = p.turn_around_time;
			this.waiting_time = p.waiting_time;
		}
	}
	
	// Generating a random batch of processes
	static void random_generated_batch (ArrayList<Process> batch, int total_processes) {
		
		Random rand = new Random();
		
		for (int i=0; i<total_processes; i++) {
			int id = i+1;
			Integer burst_time = rand.nextInt(10) + 1;
			Integer arrival_time = rand.nextInt(5);
			Integer priority = rand.nextInt(total_processes) + 1;

			batch.add(new Process(id, burst_time, arrival_time, priority));
		}
		
		// Displaying generated batch
		System.out.println("Generated Batch of Processes:");
		System.out.println(String.format("%-5s %-13s %-15s %-5s" , "ID", "Burst Time", "Arrival Time", "Priority"));
		for (Process process: batch)
			System.out.println(String.format("%-5s %-13s %-15s %-5s" , process.id, process.burst_time, process.arrival_time, process.priority));
	}
	
	// Taking batch of processes as user input
	static void user_generated_batch (ArrayList<Process> batch, int total_processes, Scanner keyboard) {
		
		System.out.println("Enter the processes:");
		System.out.println("[Format: ID CPU-Burst-Time Arrival-Time Priority (eg 1 6 2 3)]");

		for (int i=0; i<total_processes; i++) {
			
			Pattern pattern = Pattern.compile("(\\d+)\\s(\\d+)\\s(\\d+)\\s(\\d+)");
			String input = keyboard.findInLine(pattern);
			keyboard.nextLine();
			Matcher match = pattern.matcher(input);
			
			if (match.find()) {
				int id = Integer.parseInt(match.group(1));
				Integer burst_time = Integer.parseInt(match.group(2));
				Integer arrival_time = Integer.parseInt(match.group(3));
				Integer priority = Integer.parseInt(match.group(4));
				
				batch.add(new Process(id, burst_time, arrival_time, priority));
			}
		}
		
		System.out.println("=====================================================================================================================");
		
		// Displaying user-provided batch
		System.out.println("Provided Batch of Processes:");
		System.out.println(String.format("%-5s %-13s %-15s %-5s" , "ID", "Burst Time", "Arrival Time", "Priority"));
		for (Process process: batch)
			System.out.println(String.format("%-5s %-13s %-15s %-5s" , process.id, process.burst_time, process.arrival_time, process.priority));
	}
	
	// Displaying Gantt Chart for non-preemptive scheduling
	static void gantt_chart_non_preemptive(ArrayList<Process> execution_order) {
		
		int total_time = execution_order.get(execution_order.size()-1).completion_time;
		
		System.out.println("---------------------------------------------------------------------------------------------------------------------");
		System.out.println("Gantt Chart: ");
		
		// Displaying first row
		System.out.print("+");
		for (int i=execution_order.get(0).start_time; i<total_time; i++) {
			for (Process process: execution_order) {
				if (process.start_time == i) {
					for(int j=0; j<(process.burst_time-2)/2; j++)
						System.out.print("-");
					System.out.print("--");
					for(int j=0; j<(process.burst_time-2)/2; j++)
						System.out.print("-");
					System.out.print("+");
					break;
				}
			}
		}
		System.out.println("");
		
		// Displaying second row (of processes)
		System.out.print("|");
		for (int i=execution_order.get(0).start_time; i<total_time; i++) {
			for (Process process: execution_order) {
				if (process.start_time == i) {
					for(int j=0; j<(process.burst_time-2)/2; j++)
						System.out.print(" ");
					System.out.print("P" + process.id);
					for(int j=0; j<(process.burst_time-2)/2; j++)
						System.out.print(" ");
					System.out.print("|");
					break;
				}
			}
		}
		System.out.println("");
		
		// Displaying third row
		System.out.print("+");
		for (int i=execution_order.get(0).start_time; i<total_time; i++) {
			for (Process process: execution_order) {
				if (process.start_time == i) {
					for(int j=0; j<(process.burst_time-2)/2; j++)
						System.out.print("-");
					System.out.print("--");
					for(int j=0; j<(process.burst_time-2)/2; j++)
						System.out.print("-");
					System.out.print("+");
					break;
				}
			}
		}
		System.out.println("");
		
		String spaces;
		
		// Displaying fourth row (of time elapsed)
		System.out.print(execution_order.get(0).start_time);
		for (int i=execution_order.get(0).start_time; i<total_time; i++) {
			for (Process process: execution_order) {
				if (process.start_time == i) {
					if (process.burst_time <= 3)
						spaces = "%"+Integer.toString(3)+"s";
					else if (process.burst_time >= 10 || process.burst_time % 2 != 0)
						spaces = "%"+Integer.toString(process.burst_time)+"s";
					else 
						spaces = "%"+Integer.toString(process.burst_time+1)+"s";
					System.out.print(String.format(spaces, process.completion_time));
					break;
				}
			}
		}
		System.out.println("");
	}
	
	// Displaying Gantt Chart for preemptive scheduling
	static void gantt_chart_preemptive(ArrayList<Process> execution_order, ArrayList<ArrayList<Integer>> tracker) {
		
		System.out.println("---------------------------------------------------------------------------------------------------------------------");
		System.out.println("Gantt Chart: ");
		
		/* Since tracker has absolute time (from start),
		 * we need another type of tracker to know for how long a process executes
		 * before being put on old for a process with higher priority.
		 * This is helpful to display Gantt Chart
		 */
		ArrayList<ArrayList<Integer>> burst_tracker = new ArrayList<ArrayList<Integer>>(); 
		
		// Calculating difference between the times
		for (int i=0; i<tracker.size(); i++) {
			Integer temp_1 = tracker.get(i).get(1);
			Integer temp_2 = 0;
			
			if (i == tracker.size() - 1)
				temp_2 = execution_order.get(execution_order.size()-1).completion_time;
			else
				temp_2 = tracker.get(i+1).get(1);
			
			ArrayList<Integer> temp_3 = new ArrayList<Integer>(2);
			temp_3.add(tracker.get(i).get(0));
			temp_3.add(temp_2 - temp_1);
			burst_tracker.add(temp_3);
		}
		
		// Displaying first row
		System.out.print("+");
		for (ArrayList<Integer> process: burst_tracker) {
			for(int j=0; j<(process.get(1)-2)/2; j++)
				System.out.print("-");
			System.out.print("--");
			for(int j=0; j<(process.get(1)-2)/2; j++)
				System.out.print("-");
			System.out.print("+");
		}
		System.out.println("");
		
		// Displaying second row (of processes)
		System.out.print("|");
		for (ArrayList<Integer> process: burst_tracker) {
			for(int j=0; j<(process.get(1)-2)/2; j++)
				System.out.print(" ");
			System.out.print("P" + process.get(0));
			for(int j=0; j<(process.get(1)-2)/2; j++)
				System.out.print(" ");
			System.out.print("|");
		}
		System.out.println("");
		
		// Displaying third row
		System.out.print("+");
		for (ArrayList<Integer> process: burst_tracker) {
			for(int j=0; j<(process.get(1)-2)/2; j++)
				System.out.print("-");
			System.out.print("--");
			for(int j=0; j<(process.get(1)-2)/2; j++)
				System.out.print("-");
			System.out.print("+");
		}
		System.out.println("");
		
		String spaces;
		
		// Displaying fourth row (of time elapsed)
		for (int i=0; i<burst_tracker.size(); i++) {
			if (burst_tracker.get(i).get(1) <= 3)
				spaces = "%-"+Integer.toString(3)+"s";
			else if (burst_tracker.get(i).get(1) >= 10 || burst_tracker.get(i).get(1) % 2 != 0)
				spaces = "%-"+Integer.toString(burst_tracker.get(i).get(1))+"s";
			else 
				spaces = "%-"+Integer.toString(burst_tracker.get(i).get(1)+1)+"s";
			System.out.print(String.format(spaces, tracker.get(i).get(1)));
		}
		System.out.print(execution_order.get(execution_order.size()-1).completion_time);
		System.out.println("");
	}

	// Displaying result of scheduling algorithm
	static void display_results(ArrayList<Process> execution_order, int total_processes, ArrayList<ArrayList<Integer>> tracker, String type) {
		
		double avg_turn_around_time = 0;
		double avg_waiting_time = 0;
		int total_turn_around_time = 0;
		int total_waiting_time = 0;
		
		// To sort processes by id attribute
		SortedSet<Process> result = new TreeSet<Process>(new Comparator<Process> () {
			@Override
			public int compare (Process p1, Process p2) {
				return p1.id.compareTo(p2.id);
            }
		});
		
		for (Process process: execution_order)
			result.add(process);
		
		// Calculating average turn around time and average waiting time
		for (Process process: result) {
			process.turn_around_time = process.completion_time - process.arrival_time;
			process.waiting_time = process.turn_around_time - process.burst_time;
			total_turn_around_time += process.turn_around_time;
			total_waiting_time += process.waiting_time;
		}
		
		avg_turn_around_time = total_turn_around_time/Double.valueOf(total_processes);
		avg_waiting_time = total_waiting_time/Double.valueOf(total_processes);
		
		// Displaying execution order of processes
		System.out.println("Execution Order of Processes:");
		for (int i=0; i<execution_order.size(); i++) {
			if (i == execution_order.size() - 1)
				System.out.println("P" + execution_order.get(i).id);
			else
				System.out.print("P" + execution_order.get(i).id + " -> ");
		}
		
		// Displaying all processes with their attributes
		System.out.println("---------------------------------------------------------------------------------------------------------------------");
		System.out.println("Process Details:");
		System.out.println(String.format("%-5s %-13s %-13s %-10s %-13s %-17s %-20s %-5s" , "ID", "Burst Time", "Arrival Time", "Priority", "Start Time", "Completion Time", "Turn Around Time", "Waiting Time"));
		
		if (type == "sjf") {
			for (Process process:result)
				System.out.println(String.format("%-5s %-13s %-13s %-10s %-13s %-17s %-20s %-5s" , process.id, process.burst_time, process.arrival_time, process.burst_time, process.start_time, process.completion_time, process.turn_around_time, process.waiting_time));
		}
		else {
			for (Process process:result)
				System.out.println(String.format("%-5s %-13s %-13s %-10s %-13s %-17s %-20s %-5s" , process.id, process.burst_time, process.arrival_time, process.priority, process.start_time, process.completion_time, process.turn_around_time, process.waiting_time));
		}
		
		// Displaying algorithm parameters
		System.out.println("---------------------------------------------------------------------------------------------------------------------");
		System.out.println(String.format("Average Turn Around Time = %.2f milliseconds", avg_turn_around_time));
		System.out.println(String.format("Average Waiting Time = %.2f milliseconds", avg_waiting_time));
		
		// Selecting between type of Gantt Chart
		if (tracker == null)
			gantt_chart_non_preemptive(execution_order);
		else
			gantt_chart_preemptive(execution_order, tracker);
	}
	
	// Scheduling algorithm for Preemptive approach
	static ArrayList<Process> non_preemptive (ArrayList<Process> batch, String type) {
		
		// Set starting time
		int size = batch.size();
		int time = batch.get(0).arrival_time;
		
		// Identify the priority attribute (burst time or priority)
		Comparator<Process> priority_type;
		
		if (type == "sjf") {
			priority_type = new Comparator<Process> () {
				@Override
				public int compare (Process p1, Process p2) {
					return p1.burst_time.compareTo(p2.burst_time);
	            }
			};
		}
		else {
			priority_type = new Comparator<Process> () {
				@Override
				public int compare (Process p1, Process p2) {
					return p1.priority.compareTo(p2.priority);
	            }
			};
		}
		
		SortedSet<Process> ready_queue = new TreeSet<Process>(priority_type);
		ArrayList<Process> execution_order = new ArrayList<Process>();
				
		do {
			// If process has arrived, add to ready_queue (which sorts by priority attribute)
			for (Process process: batch) {
				if (process.arrival_time <= time)
					ready_queue.add(process);
			}
			
			// Displaying status of algorithm
			System.out.println("Time: " + time);
			
			System.out.println("\n" + "Batch (Sorted by Arrival Time):");
			System.out.println(String.format("%-5s %-5s" , "ID", "Arrival Time"));
			for (Process process: batch)
				System.out.println(String.format("%-5s %-5s" , process.id, process.arrival_time));
			
			System.out.println("\n" + "Ready Queue (Sorted by CPU Burst Time):");
			System.out.println(String.format("%-5s %-5s" , "ID", "Burst Time"));
			for (Process process: ready_queue)
				System.out.println(String.format("%-5s %-5s" , process.id, process.burst_time));
			
			// Moving process from ready_queue to execution and updating process' attributes
			if (!ready_queue.isEmpty()) {
				Process selected_process = ready_queue.first();
				ready_queue.remove(selected_process);
					
				selected_process.start_time = time;
				
				if (execution_order.size() > 0)
					execution_order.get(execution_order.size()-1).completion_time = time;
				
				execution_order.add(selected_process);
				time += selected_process.burst_time;
				batch.remove(selected_process);
				
				System.out.println("\n" + "Selected Process ID: " + selected_process.id);
			}
			else {
				System.out.println("\n" + "No Process Available for Selection");
				time += 1;
			}
			
			System.out.println("---------------------------------------------------------------------------------------------------------------------");
				
		} while (execution_order.size() != size);
		
		execution_order.get(execution_order.size()-1).completion_time = time;

		return execution_order;
	}
	
	// Displaying status of preemptive algorithm
	static void display_status(int time, ArrayList<Process> batch, SortedSet<Process> ready_queue, Stack<Process> process_stack, String type) {
		
		// Displaying time elapsed
		System.out.println("\nTime: " + time);
		
		// Displaying current batch of processes
		if(!batch.isEmpty()) {
			System.out.println("\n" + "Batch (Sorted by Arrival Time):");
			System.out.println(String.format("%-5s %-5s" , "ID", "Arrival Time"));
			for (Process process: batch)
				System.out.println(String.format("%-5s %-5s" , process.id, process.arrival_time));
		}
		
		// Selecting between type of algorithm and displaying ready queue
		if (!ready_queue.isEmpty() && type == "sjf") {
			System.out.println("\n" + "Ready Queue (Sorted by CPU Burst Time):");
			System.out.println(String.format("%-5s %-5s" , "ID", "Burst Time"));
			for (Process process: ready_queue)
				System.out.println(String.format("%-5s %-5s" , process.id, process.burst_time));
		}
		else if (!ready_queue.isEmpty()) {
			System.out.println("\n" + "Ready Queue (Sorted by Priority):");
			System.out.println(String.format("%-5s %-5s" , "ID", "Priority"));
			for (Process process: ready_queue)
				System.out.println(String.format("%-5s %-5s" , process.id, process.priority));
		}
		
		if(!process_stack.isEmpty()) {
			System.out.println("\n" + "Process Stack (Last Process is Currently Being Executed):");
			
			Enumeration<Process> enu = process_stack.elements();
			ArrayList<Process> temp_stack = new ArrayList<Process>();
			
			while (enu.hasMoreElements()) {
				temp_stack.add(enu.nextElement());
			}
			
			// Selecting between type of algorithm and displaying process stack
			if (type == "sjf") {
				System.out.println(String.format("%-5s %-5s" , "ID", "Burst Time"));
				for (int i=temp_stack.size()-1; i>=0; i--)
					System.out.println(String.format("%-5s %-5s" , temp_stack.get(i).id, temp_stack.get(i).burst_time));
			}
			else {
				System.out.println(String.format("%-5s %-5s" , "ID", "Priority"));
				for (int i=temp_stack.size()-1; i>=0; i--)
					System.out.println(String.format("%-5s %-5s" , temp_stack.get(i).id, temp_stack.get(i).priority));
			}
		}
		
		System.out.println("---------------------------------------------------------------------------------------------------------------------");
	}
	
	// Scheduling algorithm for Non-preemptive approach
	static ArrayList<Process> preemptive (ArrayList<Process> batch, ArrayList<ArrayList<Integer>> tracker, String type) {
		
		// Set starting time
		int size = batch.size();
		int time = batch.get(0).arrival_time;

		// Identify priority attribute (burst time or priority)
		Comparator<Process> priority_type;
		
		if (type == "sjf") {
			priority_type = new Comparator<Process> () {
				@Override
				public int compare (Process p1, Process p2) {
					return p1.burst_time.compareTo(p2.burst_time);
	            }
			};
		}
		else {
			priority_type = new Comparator<Process> () {
				@Override
				public int compare (Process p1, Process p2) {
					return p1.priority.compareTo(p2.priority);
	            }
			};
		}
		
		SortedSet<Process> ready_queue = new TreeSet<Process>(priority_type);
		ArrayList<Process> execution_order = new ArrayList<Process>();
		
		// Stack to keep track of which process is executing
		Stack<Process> process_stack = new Stack<Process>();
		
		do {
			// If process has arrived, add to ready_queue (which sorts by priority attribute)
			if (!batch.isEmpty()) {
				for (Process process: batch) {
					if (process.arrival_time <= time)
						ready_queue.add(process);
				}
			}
			
			// If execution of process is complete, pop it off stack, update attributes and update tracker
			if (!process_stack.isEmpty()) {
				if (process_stack.peek().execution == process_stack.peek().burst_time) {
					Process selected_process = process_stack.pop();
					
					selected_process.completion_time = time;
					execution_order.add(selected_process);
					
					// Storing the id of process with the time, useful for Gantt Chart
					ArrayList<Integer> temp_1 = new ArrayList<Integer>(2);
					temp_1.add(selected_process.id);
					temp_1.add(time);
					tracker.add(temp_1);
					
					if(!process_stack.isEmpty()) {
						ArrayList<Integer> temp_2 = new ArrayList<Integer>(2);
						temp_2.add(process_stack.peek().id);
						temp_2.add(time);
						tracker.add(temp_2);
					}
					
					System.out.println("Execution Complete for Process with ID: " + selected_process.id);
					display_status(time, batch, ready_queue, process_stack, type);
				}
			}
			
			// If new process has higher priority, make it top of stack, update attributes and update tracker
			if (!process_stack.isEmpty() && !ready_queue.isEmpty()) {
				if (type == "sjf") {
					if (ready_queue.first().burst_time < process_stack.peek().burst_time) {
				
						Process selected_process = ready_queue.first();
						ready_queue.remove(selected_process);
						
						// Storing the id of process with the time, useful for Gantt Chart
						ArrayList<Integer> temp_1 = new ArrayList<Integer>(2);
						temp_1.add(process_stack.peek().id);
						temp_1.add(time);
						tracker.add(temp_1);
						
						selected_process.start_time = time;
						process_stack.push(selected_process);
						execution_order.add(selected_process);
						batch.remove(selected_process);
						
						// Storing the id of process with the time, useful for Gantt Chart
						ArrayList<Integer> temp_2 = new ArrayList<Integer>(2);
						temp_2.add(selected_process.id);
						temp_2.add(time);
						tracker.add(temp_2);
						
						System.out.println("Pushed onto Stack (due to lower Burst Time) the Process with ID: " + selected_process.id);
						display_status(time, batch, ready_queue, process_stack, type);
					}
				}
				else {
					if (ready_queue.first().priority < process_stack.peek().priority) {
						Process selected_process = ready_queue.first();
						ready_queue.remove(selected_process);
						
						// Storing the id of process with the time, useful for Gantt Chart
						ArrayList<Integer> temp_1 = new ArrayList<Integer>(2);
						temp_1.add(process_stack.peek().id);
						temp_1.add(time);
						tracker.add(temp_1);
						
						selected_process.start_time = time;
						process_stack.push(selected_process);
						execution_order.add(selected_process);
						batch.remove(selected_process);
						
						// Storing the id of process with the time, useful for Gantt Chart
						ArrayList<Integer> temp_2 = new ArrayList<Integer>(2);
						temp_2.add(selected_process.id);
						temp_2.add(time);
						tracker.add(temp_2);
						
						System.out.println("Pushed onto Stack (due to higher Priority) the Process with ID: " + selected_process.id);
						display_status(time, batch, ready_queue, process_stack, type);
					}
				}
			}
			// If no process is executing and ready_queue is not empty, simply start process execution
			else if (process_stack.isEmpty() && !ready_queue.isEmpty()) {
				
				Process selected_process = ready_queue.first();
				ready_queue.remove(selected_process);
		
				selected_process.start_time = time;
				process_stack.push(selected_process);
				execution_order.add(selected_process);
				batch.remove(selected_process);
				
				// Storing the id of process with the time, useful for Gantt Chart
				ArrayList<Integer> temp = new ArrayList<Integer>(2);
				temp.add(selected_process.id);
				temp.add(time);
				tracker.add(temp);
				
				System.out.println("Pushed onto Stack the Process with ID: " + selected_process.id);
				display_status(time, batch, ready_queue, process_stack, type);
			}

			time += 1;
			
			// Incrementing execution time for the process at top of stack
			if (!process_stack.isEmpty())
				process_stack.peek().execution += 1;
			
		} while (execution_order.size() != size*2);
		
		ArrayList<Process> removal = new ArrayList<Process>();
		
		// Removal for same adjacent processes (Instead of P1,P1 its now only P1)
		for (int i=0; i<execution_order.size()-1; i++) {
			if (execution_order.get(i).id == execution_order.get(i+1).id)
				removal.add(execution_order.get(i+1));
		}
		
		for (Process process: removal) {
			execution_order.remove(process);
		}
		
		// Only keeping lower time process for 2 adjacent processes (Instead of P1(low time), P1(high time) its now only P1(low time))
		for (int i=0; i<tracker.size()-1; i++) {
			if (tracker.get(i).get(0) == tracker.get(i+1).get(0)) {
				tracker.remove(i+1);
				i -= 1;
			}
		}
		
		// Only keeping the process that starts at a particular time (Not the process that ends at that time)
		for (int i=0; i<tracker.size()-1; i++) {
			if (tracker.get(i).get(1) == tracker.get(i+1).get(1)) {
				tracker.remove(i);
				i -= 1;
			}
		}
		
		return execution_order;
	}
	
	// Main function
	public static void main(String[] args) {
		
		System.out.println("This is a Java program to simulate CPU Job Scheduling using SJF (Shortest Job First) & Priority Scheduling Algorithms");
		System.out.println("---------------Additionally, both algorithms are implemented for Preemptive & Non-Preemptive Scheduling--------------");
		System.out.println("=====================================================================================================================");
		
		Scanner keyboard = new Scanner(System.in);
		
		System.out.print("Enter the number of processes: ");
		int total_processes = keyboard.nextInt();
		keyboard.nextLine();
		
		System.out.println("Choose an input method for processes:");
		System.out.println("1. User Input");
		System.out.println("2. Random Generation");
		System.out.print("Your Selection: ");
		int input = keyboard.nextInt();
		keyboard.nextLine();
		
		// Declaring process batches (all have the same processes)
		ArrayList<Process> batch_1 = new ArrayList<Process>();
		ArrayList<Process> batch_2 = new ArrayList<Process>();
		ArrayList<Process> batch_3 = new ArrayList<Process>();
		ArrayList<Process> batch_4 = new ArrayList<Process>();
		
		System.out.println("=====================================================================================================================");
		System.out.println("NOTES:");
		System.out.println("1. Priority Order is Lower = Higher Priority (eg 1 is higher than 10)");
		System.out.println("2. Unit of Time is milliseconds");
		System.out.println("3. In case of SJF Scheduling, CPU-Burst-Time = Priority");
		System.out.println("=====================================================================================================================");
		
		// Generating processes
		if (input == 1)
			user_generated_batch(batch_1, total_processes, keyboard);
		else if (input == 2)
			random_generated_batch(batch_1, total_processes);
		
		// Sorting processes in batch based on arrival time
		Collections.sort(batch_1, (Process p1, Process p2) -> {
            return p1.arrival_time.compareTo(p2.arrival_time);
		});
		
		// Duplicating sorted batch
		for (Process process: batch_1) {
			batch_2.add(new Process(process));
			batch_3.add(new Process(process));
			batch_4.add(new Process(process));
		}
		
		System.out.println("=====================================================================================================================");
		System.out.println("SHORTEST JOB FIRST WITH NON-PREEMPTIVE SCHEDULING");
		System.out.println("=====================================================================================================================");
		
		// SJF (Non-preemptive) Approach
		ArrayList<Process> sjf_non_preemptive_order = non_preemptive(batch_1, "sjf");
		display_results(sjf_non_preemptive_order, total_processes, null, "sjf");
		
		System.out.println("=====================================================================================================================");
		System.out.println("SHORTEST JOB FIRST WITH PREEMPTIVE SCHEDULING");
		System.out.println("=====================================================================================================================");
		
		// SJF (Preemptive) Approach
		ArrayList<ArrayList<Integer>> tracker_1 = new ArrayList<ArrayList<Integer>>(); 
		ArrayList<Process> sjf_preemptive_order = preemptive(batch_2, tracker_1, "sjf");
		display_results(sjf_preemptive_order, total_processes, tracker_1, "sjf");
		
		System.out.println("=====================================================================================================================");
		System.out.println("PRIORITY SCHEDULING WITH NON-PREEMPTIVE APPROACH");
		System.out.println("=====================================================================================================================");
		
		// Priority (Non-preemptive) Approach
		ArrayList<Process> priority_non_preemptive_order = non_preemptive(batch_3, "priority");
		display_results(priority_non_preemptive_order, total_processes, null, "priority");
		
		System.out.println("=====================================================================================================================");
		System.out.println("PRIORITY SCHEDULING WITH PREEMPTIVE APPROACH");
		System.out.println("=====================================================================================================================");
		
		// Priority (Preemptive) Approach
		ArrayList<ArrayList<Integer>> tracker_2 = new ArrayList<ArrayList<Integer>>(); 
		ArrayList<Process> priority_preemptive_order = preemptive(batch_4, tracker_2, "priority");
		display_results(priority_preemptive_order, total_processes, tracker_2, "priority");
		
		keyboard.close();
		
		System.out.println("=====================================================================================================================");
	}
}
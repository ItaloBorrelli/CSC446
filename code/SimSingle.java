/**************************************
 * University of Victoria
 * CSC 446 Fall 2018
 * Italo Borrelli
 * V00884840
 *************************************/

/**
 * Simulates queuing in a router that doesn't pre order the packets
 * sequentially.
 */

import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Random;

public class SimSingle {
	// for easy conversion
	private static final int BYTES_TO_BITS = 8;
	private static final int MEGA_TO_UNIT = 1000000;
	private static final int UNIT_TO_MILLI = 1000;;

	// !!!! Can change for different router capacities !!!!
	private static final int QUEUE_MAX = 20 * BYTES_TO_BITS * MEGA_TO_UNIT;

	// event types
	private static final int departure = 1;
	private static final int arrival = 2;

	// destinations
	private static final int source = 1;
	private static final int router = 2;
	private static final int destination = 3;

	// priority queue with events which have a time comparator
	private static PriorityQueue<Event> event_list;
	// router
	private static LinkedList<Packet> router_queue;

	// initialize important values that always start the same
	private static double clock, last_router_event, total_busy, total_in_system, total_in_queue, total_in_router, last_arrival, total_source_delay;
	private static long number_of_customers, number_of_finished, in_order_router, in_order_destination, dropped, router_arrivals, current_seq_router, current_seq_destination;
	private static boolean in_service;

	// initialize values for random
	private static Random stream;
	private static long seed;

	// initialize all values that the user may change
	private static RandomVariate source_departure;
	private static RandomVariate source_delay;

	private static double router_transmission, router_delay;
	private static long total_customers, packet_size;

	/**
	 * Initializes all values and gives the user a place to easily come
	 * and change values for different parameters for the simulation.
	 */
	private static void initialize() {
		// poisson distributed packet departures from source node
		double mean_interarrival = 0.00088889;
		source_departure = new RandomVariate(stream, mean_interarrival);

		// normal distribution for delay between source and router
		source_delay = new RandomVariate(stream, 0.02, 0.005);

		// transmission in bps
		router_transmission = 10 * MEGA_TO_UNIT;

		// delay between router and destination in seconds
		router_delay = 50 / UNIT_TO_MILLI;

		total_customers = 5000000;		// number of customers

		packet_size = 1000*BYTES_TO_BITS;	// size of all packets
	}

	/**
	 * Main entry point for code. Initialize values and then loop until the
	 * number of customers we need finished is reached. Go to initialize()
	 * to change values. Report is generated after or user can selectively
	 * change code to get only data they want.
	 */
	public static void main(String argv[]) {
		router_queue = new LinkedList<Packet>();
		event_list = new PriorityQueue<Event>();

		// initialize rng with seed from user
		seed = Long.parseLong(argv[0]);
		stream = new Random(seed);

		clock = 0.0;
		number_of_customers = 0;
		number_of_finished = 0;
		router_arrivals = 0;
		dropped = 0;
		in_order_router = 0;
		in_order_destination = 0;

		last_router_event = 0.0;
		total_busy = 0.0;

		current_seq_router = -1;
		current_seq_destination = -1;

		total_in_system = 0.0;
		total_in_queue = 0.0;
		total_in_router = 0.0;
		total_source_delay = 0.0;

		in_service = false;

		initialize();

		// put a first event in the event list
		Packet p = new Packet(number_of_customers++, packet_size);
		event_list.add(new Event(p, 0, departure, source));
		p.begin_transmit = 0;

		// do this loop taking the soonest event and stops when the
		// total desired is reached
		while (number_of_finished < total_customers) {
			Event e = event_list.poll();
			clock = e.getTime();

			if (e.getType() == departure && e.getLocation() == source) {
				//System.out.printf("Departing from source:    %10.5fs %s\n", e.getTime(), e.getPacket().asString());
				processSourceDeparture(e);
			} else if (e.getType() == arrival && e.getLocation() == router) {
				//System.out.printf("Arriving at router:       %10.5fs %s\n", e.getTime(), e.getPacket().asString());
				processRouterArrival(e);
			} else if (e.getType() == departure && e.getLocation() == router) {
				//System.out.printf("Departing from router:    %10.5fs %s\n", e.getTime(), e.getPacket().asString());
				processRouterDeparture(e);
			} else {
				//System.out.printf("Arriving at destination:  %10.5fs %s\n", e.getTime(), e.getPacket().asString());
				processDestinationArrival(e);
			}
		}

		generateReport();
	}

	/**
         * Produce all potentially pertinent information regarding the
         * simulation.
         */
	private static void generateReport() {
		double RHO = total_busy / clock;
		double percentage_dropped = (double)dropped / router_arrivals * 100;
		double percentage_in_order_router = (double)in_order_router / router_arrivals * 100;
		double percentage_in_order_destination = (double)in_order_destination / number_of_finished * 100;
		double average_in_system = total_in_system / number_of_finished;
		double average_in_queue = total_in_queue / number_of_finished;
		double average_in_router = total_in_router / number_of_finished;
		double average_interarrival = last_arrival / (number_of_customers - 1);
		double mean_source_delay = total_source_delay / number_of_customers;

		System.out.printf("DOUBLE QUEUE ROUTER SIMULATION - PACKET REORDERING WITH SINGLE QUEUE\n");
		System.out.printf("Number of customers started:                           %11d\n", number_of_customers);
		System.out.printf("Number of customers finished:                          %11d\n", number_of_finished);
		System.out.printf("Time passed:                                           %10.2fs\n", clock);
		System.out.printf("Router utilization:                                    %11.5f\n", RHO);
		System.out.printf("Percent dropped:                                       %10.2f%%\n", percentage_dropped);
		System.out.printf("Percent in order (at router):                          %10.2f%%\n", percentage_in_order_router);
		System.out.printf("Percent out of order (at destination):                 %10.2f%%\n", percentage_in_order_destination);
		System.out.printf("Average time in system:                                %9.8fs\n", average_in_system);
		System.out.printf("Average time in queue:                                 %9.8fs\n", average_in_queue);
		System.out.printf("Average time in router:                                %9.8fs\n", average_in_router);
		System.out.printf("Average interarrival time (to system):                 %9.8fs\n", average_interarrival);
		System.out.printf("Mean source delay:                                     %9.8fs\n", mean_source_delay);
	}

        /**
         * When a departure is reached schedule a next departure with a Poisson
         * distributed difference. The source delay is calculated with a normal
         * distribution.
         *
         * @param e     the details of this event
         */
	private static void processSourceDeparture(Event e) {
		double new_time = clock + source_departure.getNext();
		Packet p = new Packet(number_of_customers++, packet_size);
		event_list.add(new Event(p, new_time, departure, source));
		p.begin_transmit = new_time;

		double delay;
		while ((delay = source_delay.getNext()) < 0);
		e.setNewVals(clock + delay, arrival, router);
		event_list.add(e);
		total_source_delay += delay;

		// this ensures that at the end when we generate the report
		// this will be the time of the last packet to arrive
		last_arrival = clock;
	}

	/**
         * When an arrival at the router occurs it becomes enqueued and waits
         * to be scheduled for departure. An event for the departure is created
         * if there is nothing being transmitted.
         *
         * @param e     the details of this event
         */
	private static void processRouterArrival(Event e) {
		router_arrivals++;
		if ((router_queue.size()+1)*packet_size > QUEUE_MAX) {
			dropped++;
			return;
		}

		Packet p = e.getPacket();
		router_queue.add(p);
		p.begin_queueing = clock;

		if (p.getSeqNumber() > current_seq_router) {
			in_order_router++;
			current_seq_router = p.getSeqNumber();
		}

		if (!in_service) {
			p = router_queue.poll();
			p.begin_service = clock;

			double new_time = clock + (p.getSize()/router_transmission);
			e.setNewVals(new_time, departure, router);
			event_list.add(e);

			in_service = true;
		} else {
			total_busy += (clock - last_router_event);
		}

		last_router_event = clock;
	}

        /**
         * When a departure occurs it checks if there is a value to schedule
         * for departure next. Then schedule it's arrival at the destination
         * with a new destination arrival event a constant time from the
         * current event.
         *
         * @param e     the details of this event
         */
	private static void processRouterDeparture(Event e) {
		double new_time = clock + router_delay;
		e.setNewVals(new_time, arrival, destination);
		event_list.add(e);

		if (router_queue.size() > 0) {
			Packet p = router_queue.poll();
			event_list.add(new Event(p, clock + (p.getSize()/router_transmission), departure, router));
			p.begin_service = clock;

			in_service = true;
		} else {
			in_service = false;
		}

		total_busy += (clock - last_router_event);
		last_router_event = clock;
	}

	/**
         * Arrival at the destination. Values are calculated and the number of
         * values finished is incremented.
         *
         * @param e     the details of this event
         */
	private static void processDestinationArrival(Event e) {
		Packet p = e.getPacket();

		total_in_system += (clock - p.begin_transmit);
		total_in_queue += (p.begin_service - p.begin_queueing);
		total_in_router += (p.begin_service - p.begin_queueing + p.getSize()/router_transmission);

		if (e.getPacket().getSeqNumber() > current_seq_destination) {
			in_order_destination++;
			current_seq_destination = e.getPacket().getSeqNumber();
		}

		number_of_finished++;
	}
};

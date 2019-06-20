/**************************************
 * University of Victoria
 * CSC 446 Fall 2018
 * Italo Borrelli
 * V00884840
 *************************************/

import java.util.LinkedList;

/**
 * Router object with two queues priority queueing based on the highest value
 * of the sequence seen so far to try to reorder packets before their arrival
 * at the destination.
 */
public class Router {
	// maximum bit counts for the routers
	private long out_max;
	private long in_max;

	// queues to hold the packets
	// in for in order values and out for out of order
	private LinkedList<Packet> out_queue;
	private LinkedList<Packet> in_queue;

	// number of bits in each queue of the router
	private long out_bits;
	private long in_bits;

	// highest sequence value so far
	private long current_seq;

	/**
	 * Constructor that assigns the min and max for the queues and
	 * initializes the queues and the number of bytes.
	 *
	 * -1 ensures that the first packet received will be in order since all
	 *  the packets should have a positive sequence value.
	 *
	 * @param out_max	maximum for the out of order queue
	 * @param in_max	maximum for the in order queue
	 * @see Packet
	 */
	public Router(long out_max, long in_max) { this.out_max = out_max;
		this.in_max = in_max;

		out_queue = new LinkedList<Packet>();
		in_queue = new LinkedList<Packet>();

		out_bits = 0;
		in_bits = 0;

		current_seq = -1;
	}

	/**
	 * Puts the next packet in router in one of the queues.
	 *
	 * @param arrival	arriving packet
	 * @return		0 if dropped
	 * 			1 if in order
	 * 			2 if out of order
	 * @see Packet
	 */
	public int packetEnqueue(Packet arrival) {
		// if seq number is less than the current put in out of order
		if (current_seq > arrival.getSeqNumber()) {
			if (out_bits+arrival.getSize() <= out_max) {
				out_queue.add(arrival);
				out_bits += arrival.getSize();
				return 2;
			} else {
				return 0;
			}
		} else {
			current_seq = arrival.getSeqNumber();
			if (in_bits+arrival.getSize() <= in_max) {
				in_queue.add(arrival);
				in_bits += arrival.getSize();
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Finds which packet is next in order to transmit and returns it.
	 *
	 * @return		the next packet this router will transmit
	 * @see	Packet
	 */
	public Packet packetTransmit() {
		Packet transmitting;

		// transmit from in order if out of order is empty
		if (out_queue.isEmpty()) {
			transmitting = in_queue.poll();
			in_bits -= transmitting.getSize();
			return transmitting;
		} else {
			transmitting = out_queue.poll();
			out_bits -= transmitting.getSize();
			return transmitting;
		}
	}

	/**
	 * Return the total number of packets currently in the router.
	 *
	 * @return		the number of packets in the router
	 */
	public long getSize() {
		return out_queue.size() + in_queue.size();
	}

	/**
	 * Prints the number of bits in each queue as well as how many items
	 * are in the queues as wellas the packets with their size and sequence
	 * number. Primarily for testing.
	 *
	 * @see Packet
	 */
	public void print() {
		Object[] out = out_queue.toArray();
		Object[] in = in_queue.toArray();

		System.out.println("High Queue Bits: " + out_bits + "/" + out_max);
		System.out.println("Low Queue Bits: " + in_bits + "/" + in_max);

		System.out.println("\nPackets in High Priority Queue (" + out.length + "):");

		for (int i = 0; i < out.length; i++) {
			System.out.println("(" + ((Packet)out[i]).getSeqNumber()
					+ "," + ((Packet)out[i]).getSize()/8 + ")");
		}

		System.out.println("\nPackets in Low Priority Queue (" + in.length + "):");
		for (int i = 0; i < in.length; i++) {
			System.out.println("(" + ((Packet)in[i]).getSeqNumber()
					+ "," + ((Packet)in[i]).getSize()/8 + ")");
		}
		System.out.println();
	}
};

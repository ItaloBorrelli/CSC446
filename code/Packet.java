/**************************************
 * University of Victoria
 * CSC 446 Fall 2018
 * Italo Borrelli
 * V00884840
 *************************************/

/**
 * A data packet with a size and sequence number for the packet.
 *
 * Also contains a place to put time information for the packet.
 */
public class Packet {
	private long seq_number;	// sequence of packet
	private long size;		// size in bits of packet
	public double begin_transmit;	// when the packet is transmitted
	public double begin_queueing;	// when the packet enters the router
	public double begin_service;	// when the packet gets serviced

	/**
	 * Constructor assigning the sequence number and size of the packet.
	 *
	 * @param seq_number	sequence of packet
	 * @param size		size of packet
	 */
	public Packet(long seq_number, long size) {
		this.seq_number = seq_number;
		this.size = size;
	}

	/**
	 * Getters for class variables.
	 */
	public long getSeqNumber() { return this.seq_number; }
	public long getSize() { return this.size; }

	/**
	 * Returns a string form of the data in the packet.
	 *
	 * @return string with seq number and number of bits
	 */
	public String asString() {
		return "(" + this.seq_number + "," + this.size + ")";
	}
};

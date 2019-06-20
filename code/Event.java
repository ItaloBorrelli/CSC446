/**************************************
 * University of Victoria
 * CSC 446 Fall 2018
 * Italo Borrelli
 * V00884840
 *************************************/

/**
 * An event object that is comparable based on time of the event.
 *
 * @see Packet
 */
public class Event implements Comparable<Event> {
	private Packet packet;	// packet associated with this event
	private double time;	// time the event occurs
	private int type;	// type of event
	private int location;	// integer corresponding to the location the event occurs

	/**
	 * Constructor assigning a packet and time to the class as well as the
	 * type event (departure or arrival) and location (in general source,
	 * router or destination) as integers.
	 *
	 * @param packet	packet in this event
	 * @param time		time of event
	 * @param type		type of event
	 * @param location	location of event
	 * @see Packet
	 */
	public Event(Packet packet, double time, int type, int location) {
		this.packet = packet;
		this.time = time;
		this.type = type;
		this.location = location;
	}

	/**
	 * Getters for class variables.
	 */
	public Packet getPacket() { return this.packet; }
	public double getTime() { return this.time; }
	public int getType() { return this.type; }
	public int getLocation() {return this.location; }

	/**
	 * Set new values for a new event with the same packet.
	 *
	 * @param new_time	new time for the event
	 * @param new_type	new type identifier for the event
	 * @param new_location	new location identifier for the event
	 */
	public void setNewVals(double new_time, int new_type, int new_location) {
		this.time = new_time;
		this.type = new_type;
		this.location = new_location;
	}

	/**
	 * Comparator for use in a priority queue based on the time of the
	 * event.
	 *
	 * @param cmpEvent	event to compare to
	 * @return		-1 if this event is sooner
	 * 			0 if this event is at the same time
	 * 			1 if this event is later
	 */
	public int compareTo(Event cmpEvent) {
		double cmpTime = cmpEvent.getTime();

		if(this.time < cmpTime) return -1;
		if(this.time == cmpTime) return 0;
		return 1;
	}
};

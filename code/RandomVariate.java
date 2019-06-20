/**************************************
 * University of Victoria
 * CSC 446 Fall 2018
 * Italo Borrelli
 * V00884840
 *************************************/

import java.util.Random;

/**
 * Class for creating a random variate from a uniform distribution on [0,1).
 */
public class RandomVariate {
	private Random rng;
	private double mean;
	private double sigma;

	private String distribution;

	/**
	 * Constructor for a poisson distribution.
	 *
	 * @param rng	random number generator
	 * @param mean	poisson mean
	 */
	public RandomVariate(Random rng, double mean) {
		this.rng = rng;
		this.mean = mean;
		this.sigma = 0;		// will not be used

		this.distribution = "poisson";
	}

	/**
	 * Constructor for a normal distribution.
	 *
	 * @param rng	random number generator
	 * @param mean	normal mean
	 * @param sigma	standard deviation
	 */
	public RandomVariate(Random rng, double mean, double sigma) {
		this.rng = rng;
		this.mean = mean;
		this.sigma = sigma;

		this.distribution = "normal";
	}

	/**
	 * Gets the next value using the type of this variate.
	 *
	 * @return	double for next value
	 */
	public double getNext() {
		if (this.distribution == "normal") return normal();
		return exponential();
	}

	/**
	 * Uses the exponential distribution to calculate the next arrival in a
	 * poisson distribution.
	 *
	 * @return	next poisson arrival
	 */
	private double exponential() {
		return -mean*Math.log(rng.nextDouble());
	}

	private static double SaveNormal;	// saved normal if one has been created
	private static int NumNormals = 0;	// keeps track of if there is a saved normal
	private static final double PI = 3.1415927;

	/**
	 * Generates two random variables with every two random variables using
	 * the Box-Muller transform.
	 *
	 * @return	next arrival with a normal distribution
	 */
	private double normal() {
		double ReturnNormal;

		//generate two normals to save time later
		if(NumNormals == 0 ) {
			double r1 = rng.nextDouble();
			double r2 = rng.nextDouble();
			ReturnNormal = Math.sqrt(-2*Math.log(r1))*Math.cos(2*PI*r2);
			SaveNormal = Math.sqrt(-2*Math.log(r1))*Math.sin(2*PI*r2);
			NumNormals = 1;
		} else {
			NumNormals = 0;
			ReturnNormal = SaveNormal;
		}

		return ReturnNormal * sigma + mean ;
	}
};

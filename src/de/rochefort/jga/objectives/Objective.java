package de.rochefort.jga.objectives;

import de.rochefort.jga.data.Individual;

public abstract class Objective<T> {

	/**
	 * Should return a value between 0 and 1, with 0 and 1 being the lowest and 
	 * highest fitness, respectively
	 * @param individual the individual to assess
	 * @return the individual's fitness
	 */
	public abstract double computeFitness(Individual<T> individual);
}
package de.rochefort.jga.alg.crossover;

import java.util.List;

import de.rochefort.jga.data.Individual;

public abstract class CrossOverAlgorithm {

	public CrossOverAlgorithm() {
	}

	public abstract List<Individual> crossover(Individual parentA, Individual parentB);
}

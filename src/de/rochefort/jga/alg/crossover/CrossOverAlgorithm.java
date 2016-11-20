package de.rochefort.jga.alg.crossover;

import de.rochefort.jga.data.Individual;

import java.util.List;

public abstract class CrossOverAlgorithm<T> {

	protected CrossOverAlgorithm() {
	}

	public abstract List<Individual<T>> crossover(Individual<T> parentA, Individual<T> parentB);

	public static <T> CrossOverAlgorithm<T> newPointCrossOverAlgorithm() {
		return new PointCrossOver<>();
	}

	public static <T> CrossOverAlgorithm<T> newPointCrossOverAlgorithm(double crossOverProbability) {
		return new PointCrossOver<>(crossOverProbability);
	}

	public static <T> CrossOverAlgorithm<T> newPointCrossOverAlgorithm(PointCrossOver.CrossOverType crossOverType) {
		return new PointCrossOver<>(crossOverType);
	}

	public static <T> CrossOverAlgorithm<T> newPointCrossOverAlgorithm(double crossOverProbability, PointCrossOver.CrossOverType crossOverType) {
		return new PointCrossOver<>(crossOverProbability, crossOverType);
	}
}

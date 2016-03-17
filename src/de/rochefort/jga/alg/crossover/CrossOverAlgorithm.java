package de.rochefort.jga.alg.crossover;

import de.rochefort.jga.data.Individual;

import java.util.List;

public abstract class CrossOverAlgorithm {

	protected CrossOverAlgorithm() {
	}

	public abstract List<Individual> crossover(Individual parentA, Individual parentB);

	public static CrossOverAlgorithm newPointCrossOverAlgorithm(){
		return new PointCrossOver();
	}

	public static CrossOverAlgorithm newPointCrossOverAlgorithm(double crossOverProbability){
		return new PointCrossOver(crossOverProbability);
	}

	public static CrossOverAlgorithm newPointCrossOverAlgorithm(PointCrossOver.CrossOverType crossOverType){
		return new PointCrossOver(crossOverType);
	}

	public static CrossOverAlgorithm newPointCrossOverAlgorithm(double crossOverProbability, PointCrossOver.CrossOverType crossOverType){
		return new PointCrossOver(crossOverProbability, crossOverType);
	}
}

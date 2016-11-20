package de.rochefort.jga.alg.crossover;

import de.rochefort.jga.alg.GeneticAlgorithm;
import de.rochefort.jga.data.Individual;
import de.rochefort.jga.data.Parameter;

import java.util.ArrayList;
import java.util.List;

public class PointCrossOver<T> extends CrossOverAlgorithm<T> {
	public static final CrossOverType DEFAULT_CROSS_OVER_TYPE = CrossOverType.DUAL;
	public static final double DEFAULT_CROSS_OVER_PROBABILITY = 0.7;

	public enum CrossOverType {
		SINGLE, DUAL
	}
	private final double crossOverProbability;
	private final CrossOverType type;

    protected PointCrossOver(double crossOverProbability, CrossOverType type) {
		super();
		this.crossOverProbability = crossOverProbability;
		this.type = type;
	}

    protected PointCrossOver(CrossOverType type) {
		this(DEFAULT_CROSS_OVER_PROBABILITY, type);
	}

    protected PointCrossOver(double crossOverProbability) {
		this(crossOverProbability, DEFAULT_CROSS_OVER_TYPE);
	}

	protected PointCrossOver() {
		this(DEFAULT_CROSS_OVER_PROBABILITY, DEFAULT_CROSS_OVER_TYPE);
	}

	@Override
	public List<Individual<T>> crossover(Individual<T> parentA, Individual<T> parentB) {
		final List<Parameter> parameters = parentA.getParameters();
		boolean[] offspringAGenes = parentA.encode();
		boolean[] offspringBGenes = parentB.encode();

		if (GeneticAlgorithm.RANDOM.nextDouble() < crossOverProbability) {
			int crossoverLocation1 = GeneticAlgorithm.RANDOM.nextInt(offspringAGenes.length) - 1;
			int crossoverLocation2 = GeneticAlgorithm.RANDOM.nextInt(offspringAGenes.length) - 1;
			

			for (int g = 0; g < offspringAGenes.length; g++) {
				if (
						g >= Math.min(crossoverLocation1, crossoverLocation2) 
						&& (type == CrossOverType.SINGLE || g < Math.max(crossoverLocation1, crossoverLocation2))
						) {
					boolean o1G = offspringAGenes[g];
					offspringAGenes[g] = offspringBGenes[g];
					offspringBGenes[g] = o1G;
				}
			}
		}
		final List<Individual<T>> offspring = new ArrayList<>(2);
		offspring.add(new Individual<>(parameters, offspringAGenes));
		offspring.add(new Individual<>(parameters, offspringBGenes));
		return offspring;
	}

}

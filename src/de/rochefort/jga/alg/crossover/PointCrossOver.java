package de.rochefort.jga.alg.crossover;

import java.util.ArrayList;
import java.util.List;

import de.rochefort.jga.alg.GeneticAlgorithm;
import de.rochefort.jga.data.Individual;
import de.rochefort.jga.data.Parameter;

public class PointCrossOver extends CrossOverAlgorithm {
	public enum CrossOverType {
		SINGLE, DUAL
	}
	private final double crossOverProbability;
	private final CrossOverType type;

	public PointCrossOver(double crossOverProbability, CrossOverType type) {
		super();
		this.crossOverProbability = crossOverProbability;
		this.type = type;
	}

	@Override
	public List<Individual> crossover(Individual parentA, Individual parentB) {
		List<Parameter> parameters = parentA.getParameters();
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
		List<Individual> offspring = new ArrayList<>(2);
		offspring.add(new Individual(parameters, offspringAGenes));
		offspring.add(new Individual(parameters, offspringBGenes));
		return offspring;
	}

}

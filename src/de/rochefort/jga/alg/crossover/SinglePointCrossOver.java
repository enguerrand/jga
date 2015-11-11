package de.rochefort.jga.alg.crossover;

import java.util.ArrayList;
import java.util.List;

import de.rochefort.jga.alg.GeneticAlgorithm;
import de.rochefort.jga.data.Individual;
import de.rochefort.jga.data.Parameter;

public class SinglePointCrossOver extends CrossOverAlgorithm {
	private final double crossOverProbability;

	public SinglePointCrossOver(double crossOverProbability) {
		super();
		this.crossOverProbability = crossOverProbability;
	}

	@Override
	public List<Individual> crossover(Individual parentA, Individual parentB) {
		List<Parameter> parameters = parentA.getParameters();
		boolean[] offspringAGenes = parentA.encode();
		boolean[] offspringBGenes = parentB.encode();

		int crossoverLocation = 0;
		if (GeneticAlgorithm.RANDOM.nextDouble() < crossOverProbability) {
			crossoverLocation = GeneticAlgorithm.RANDOM.nextInt(offspringAGenes.length) - 1;

			for (int g = 0; g < offspringAGenes.length; g++) {
				if (g >= crossoverLocation) {
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

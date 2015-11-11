package de.rochefort.jga.alg.mutation;

import java.util.List;

import de.rochefort.jga.alg.GeneticAlgorithm;
import de.rochefort.jga.data.Individual;

public class SimpleMutation extends MutationAlgorithm {
	private final double mutationProbability;
	
	public SimpleMutation(double mutationProbability) {
		super();
		this.mutationProbability = mutationProbability;
	}

	@Override
	public void mutate(List<Individual> individuals) {
		for (Individual individual : individuals) {
			boolean[] bits = individual.encode();
			for (int index = 0; index < bits.length; index++) {
				if (GeneticAlgorithm.RANDOM.nextDouble() < mutationProbability) {
					bits[index] = !bits[index];
				}
			}
			individual.recode(bits);
		}
	}

}

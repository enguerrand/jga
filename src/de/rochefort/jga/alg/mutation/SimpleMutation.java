package de.rochefort.jga.alg.mutation;

import de.rochefort.jga.alg.GeneticAlgorithm;
import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Individual;

import java.util.List;

public class SimpleMutation<T> extends MutationAlgorithm<T> {
	private final double mutationProbability;

    protected SimpleMutation(){
        this(DEFAULT_MUTATION_PROBABILITY);
    }

	SimpleMutation(double mutationProbability) {
		super();
		this.mutationProbability = mutationProbability;
	}

	@Override
	public void mutate(List<Individual<T>> individuals, Generation<T> generation) {
		for (Individual<T> individual : individuals) {
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

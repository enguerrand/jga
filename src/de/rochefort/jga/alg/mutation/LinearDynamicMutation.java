package de.rochefort.jga.alg.mutation;

import de.rochefort.jga.alg.GeneticAlgorithm;
import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Individual;

import java.util.List;

public class LinearDynamicMutation<T> extends MutationAlgorithm<T> {
	private final double initialMutationProbability;
	private final double finalMutationProbability;
	private final double mutationProbabilityGradient;
	private final long generationCount;

	protected LinearDynamicMutation(double initialMutationProbability, double finalMutationProbability, long generationCount) {
		super();
		this.initialMutationProbability = initialMutationProbability;
		this.finalMutationProbability = finalMutationProbability;
		this.mutationProbabilityGradient = (this.finalMutationProbability-this.initialMutationProbability) / ((double)generationCount);
		this.generationCount = generationCount;
	}

	protected double getCurrentMutationProbability(long generationIndex){
		if(generationIndex > this.generationCount){
			return finalMutationProbability;
		}
		return initialMutationProbability + mutationProbabilityGradient * generationIndex;
	}
	
	@Override
	public void mutate(List<Individual<T>> individuals, Generation<T> generation) {
		double mutationProbability = getCurrentMutationProbability(generation.getIndex());
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

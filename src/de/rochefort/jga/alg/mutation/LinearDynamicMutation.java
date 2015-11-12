package de.rochefort.jga.alg.mutation;

import java.util.List;

import de.rochefort.jga.alg.GeneticAlgorithm;
import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Individual;

public class LinearDynamicMutation extends MutationAlgorithm {
	private final double initialMutationProbability;
	private final double finalMutationProbability;
	private final double mutationProbabilityGradient;
	private final long generationCount;

	public LinearDynamicMutation(double initialMutationProbability, double finalMutationProbability, long generationCount) {
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
	public void mutate(List<Individual> individuals, Generation generation) {
		double mutationProbability = getCurrentMutationProbability(generation.getIndex());
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

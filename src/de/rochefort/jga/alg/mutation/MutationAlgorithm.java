package de.rochefort.jga.alg.mutation;

import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Individual;

import java.util.List;

public abstract class MutationAlgorithm {
	public final static double DEFAULT_MUTATION_PROBABILITY = 0.005;
    protected MutationAlgorithm() {
	}

	public abstract void mutate(List<Individual> individuals, Generation generation);

	public static MutationAlgorithm newSimpleMutation() {
		return new SimpleMutation();
	}

	public static MutationAlgorithm newSimpleMutation(double mutationProbability) {
		return new SimpleMutation(mutationProbability);
	}

    public static MutationAlgorithm newLinearDynamicMutation(double initialMutationProbability, double finalMutationProbability, long generationCount){
        return new LinearDynamicMutation(initialMutationProbability, finalMutationProbability, generationCount);
    }
}

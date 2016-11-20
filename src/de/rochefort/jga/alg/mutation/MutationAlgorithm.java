package de.rochefort.jga.alg.mutation;

import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Individual;

import java.util.List;

public abstract class MutationAlgorithm<T> {
	public final static double DEFAULT_MUTATION_PROBABILITY = 0.005;
    protected MutationAlgorithm() {
	}

	public abstract void mutate(List<Individual<T>> individuals, Generation<T> generation);

	public static <T> MutationAlgorithm<T> newSimpleMutation() {
		return new SimpleMutation<>();
	}

	public static <T> MutationAlgorithm<T> newSimpleMutation(double mutationProbability) {
		return new SimpleMutation<>(mutationProbability);
	}

	public static <T> MutationAlgorithm<T> newLinearDynamicMutation(double initialMutationProbability, double finalMutationProbability, long generationCount) {
		return new LinearDynamicMutation<>(initialMutationProbability, finalMutationProbability, generationCount);
	}
}

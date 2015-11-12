package de.rochefort.jga.alg.mutation;

import java.util.List;

import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Individual;

public abstract class MutationAlgorithm {

	public MutationAlgorithm() {
	}

	public abstract void mutate(List<Individual> individuals, Generation generation);
}

package de.rochefort.jga.alg.selection;

import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Individual;
import de.rochefort.jga.objectives.Objective;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class SelectionAlgorithm<T> {
	private final List<Objective<T>> objectives = new ArrayList<>();

	protected SelectionAlgorithm(Collection<Objective<T>> objectives) {
		this.objectives.addAll(objectives);
	}

	public abstract Collection<Individual<T>> selectIndividuals(Generation<T> generation, int individualCount);

	public List<Objective<T>> getObjectives() {
		return Collections.unmodifiableList(objectives);
	}

	public static <T> SelectionAlgorithm<T> newSimpleTournamentSelection(Collection<Objective<T>> objectives) {
		return new SimpleTournamentSelection<>(objectives);
	}

	public static <T> SelectionAlgorithm<T> newSimpleTournamentSelection(Collection<Objective<T>> objectives, double selectionPressure) {
		return new SimpleTournamentSelection<>(objectives, selectionPressure);
	}
}

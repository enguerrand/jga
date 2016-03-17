package de.rochefort.jga.alg.selection;

import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Individual;
import de.rochefort.jga.objectives.Objective;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class SelectionAlgorithm {
	private final List<Objective> objectives = new ArrayList<>();
    protected SelectionAlgorithm(Collection<Objective> objectives) {
		this.objectives.addAll(objectives);
	}

	public abstract Collection<Individual> selectIndividuals(Generation generation, int individualCount);
	
	public List<Objective> getObjectives() {
		return Collections.unmodifiableList(objectives);
	}

	public static SelectionAlgorithm newSimpleTournamentSelection(Collection<Objective> objectives){
		return new SimpleTournamentSelection(objectives);
	}

	public static SelectionAlgorithm newSimpleTournamentSelection(Collection<Objective> objectives, double selectionPressure){
		return new SimpleTournamentSelection(objectives, selectionPressure);
	}
}

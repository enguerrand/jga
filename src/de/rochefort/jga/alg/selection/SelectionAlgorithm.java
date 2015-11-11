package de.rochefort.jga.alg.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Individual;
import de.rochefort.jga.objectives.Objective;

public abstract class SelectionAlgorithm {
	private final List<Objective> objectives = new ArrayList<>();
	public SelectionAlgorithm(List<Objective> objectives) {
		this.objectives.addAll(objectives);
	}

	public abstract Collection<Individual> selectIndividuals(Generation generation, int individualCount);
	
	public List<Objective> getObjectives() {
		return Collections.unmodifiableList(objectives);
	}
}

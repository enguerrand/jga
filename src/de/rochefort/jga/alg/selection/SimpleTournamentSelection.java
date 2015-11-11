package de.rochefort.jga.alg.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.rochefort.jga.alg.GeneticAlgorithm;
import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Individual;
import de.rochefort.jga.objectives.Objective;

public class SimpleTournamentSelection extends SelectionAlgorithm{
	private double selectionPressure;
	/**
	 * Constructs a SimpleTournamentSelection
	 * @param objectives 		
	 * 				the objectives determining the fitness
	 * @param selectionPressure 
	 * 				A parameter between 0 and 1, for the lowest and highest selection pressure, respectively. 
	 * 				A value of 0 considers all individuals for selection. A value of e.g. 0.3 discounts the
	 * 				unfittest 30% of the pool and chooses from the remaining 70%.
	 */
	public SimpleTournamentSelection(List<Objective> objectives, double selectionPressure) {
		super(objectives);
		if(selectionPressure < 0 || selectionPressure > 1){
			throw new IllegalArgumentException("Selection pressure "+selectionPressure+" not allowed. (Value between 0 and 1 expected)");
		}
		this.selectionPressure = selectionPressure;
	}

	@Override
	public Collection<Individual> selectIndividuals(Generation generation, int selectionSize) {
		List<Individual> selectedIndividuals = new ArrayList<>();
		while(selectedIndividuals.size() < selectionSize){
			selectedIndividuals.add(selectIndividual(generation, getObjectives()));
		}
		return selectedIndividuals;
	}
	
	private Individual selectIndividual(Generation generation, List<Objective> objectives){
		double fitnessSum = generation.getFitnessSum(objectives);
		if (fitnessSum == 0) {
			int r = GeneticAlgorithm.RANDOM.nextInt(generation.getIndividualsCount() - 1);
			return generation.getIndividual(r);
		}
		double rouletteBall = GeneticAlgorithm.RANDOM.nextDouble() * fitnessSum * (1-this.selectionPressure);
		double sum = 0;
		
		for (Individual individual : generation.getIndividualsSortedByFitness(objectives)) {
			sum = sum + individual.getFitness(objectives);
			if (sum >= rouletteBall) {
				return individual;
			}
		}
		throw new RuntimeException("The rouletteBall value " + rouletteBall
				+ " was higher than the final sum " + sum
				+ " The fitnessSum was " + fitnessSum);
	}
}

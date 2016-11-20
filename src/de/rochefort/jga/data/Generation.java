package de.rochefort.jga.data;

import de.rochefort.jga.alg.GeneticAlgorithm.OutputVerbosity;
import de.rochefort.jga.alg.crossover.CrossOverAlgorithm;
import de.rochefort.jga.alg.mutation.MutationAlgorithm;
import de.rochefort.jga.alg.selection.SelectionAlgorithm;
import de.rochefort.jga.objectives.Objective;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Generation<T> {
	private final List<Individual<T>> individuals;
	private final long index;

	private Generation(long index, List<Individual<T>> individuals) {
		this.index = index;
		this.individuals = new ArrayList<>(individuals.size());
		this.individuals.addAll(individuals);
	}
	
	public Generation(int poolSize, List<Parameter> parameters){
		this(1L, createRandomPool(poolSize, parameters));
	}

	public Generation(Generation<T> parentGeneration, SelectionAlgorithm<T> selector, CrossOverAlgorithm<T> crosser, MutationAlgorithm<T> mutator) {
		this(parentGeneration.getIndex()+1, parentGeneration.createOffspring(selector, crosser, mutator));
	}
	
	public long getIndex() {
		return index;
	}

	private List<Individual<T>> createOffspring(SelectionAlgorithm<T> selector, CrossOverAlgorithm<T> crosser, MutationAlgorithm<T> mutator) {
		final List<Individual<T>> offspring = new ArrayList<>(this.individuals.size());
		while(offspring.size() < this.individuals.size()){
			Collection<Individual<T>> selection = selector.selectIndividuals(this, 2);
			if(selection.size() < 2){
				throw new RuntimeException("Too few individuals selected!");
			}
			final Iterator<Individual<T>> it = selection.iterator();
			final List<Individual<T>> children = crosser.crossover(it.next(), it.next());
			mutator.mutate(children, this);
			for (Individual<T> individual : children) {
				if(offspring.size() >= this.individuals.size()){
					break;
				}
				offspring.add(individual);
			}
		}
		return offspring;
	}

	private static <T> List<Individual<T>> createRandomPool(int size, List<Parameter> parameters) {
		List<Individual<T>> pool = new ArrayList<>(size);
		parameters.sort(Parameter.INDEX_COMPARATOR);
		while(pool.size() < size){
			Map<Parameter, Double> values = new HashMap<>();
			for(Parameter p : parameters){
				values.put(p, p.randomValue());
			}
			pool.add(new Individual<>(values));
		}
		return pool;
	}

	public void evaluate(Consumer<Generation<T>> preparer, Function<Map<Parameter, Double>, T> evaluationFunction) {
		preparer.accept(this);

		for (Individual<T> individual : this.individuals) {
			individual.evaluate(evaluationFunction);
		}
	}

	public void evaluate(Consumer<Generation<T>> preparer, Function<Map<Parameter, Double>, T> evaluationFunction, ExecutorService executor) throws InterruptedException, ExecutionException {
		preparer.accept(this);
		List<Future<?>> jobs = this.individuals.stream()
				.map(individual -> executor.submit(
						() -> individual.evaluate(evaluationFunction))).collect(Collectors.toList());
		for(Future<?> job : jobs){
			job.get();
		}
	}
	
	//TODO Implement multithreaded evaluation

	public double getFitnessSum(List<Objective<T>> objectives) {
		double fitnessSum = 0;
		for (Individual<T> individual : this.individuals) {
			fitnessSum = fitnessSum + individual.getFitness(objectives);
		}
		return fitnessSum;
	}

	public double getMeanFitness(List<Objective<T>> objectives) {
		return getFitnessSum(objectives) / individuals.size();
	}

	public double getHighestFitness(List<Objective<T>> objectives) {
		return getIndividualsSortedByFitness(objectives).get(0).getFitness(objectives);
	}

	public Generation<T> deepCopy() {
		return new Generation<>(this.index, this.individuals);
	}
	
	public int getIndividualsCount(){
		return this.individuals.size();
	}

	public List<Individual<T>> getIndividuals() {
		List<Individual<T>> list = new ArrayList<>();
		list.addAll(individuals);
		return list;
	}

	public List<Individual<T>> getIndividualsSortedByFitness(List<Objective<T>> objectives) {
		List<Individual<T>> list = getIndividuals();
		list.sort(Individual.getFitnessComparator(objectives));
		return list;
	}

	public Individual<T> getIndividual(int index) {
		return individuals.get(index);
	}

	public void print(List<Objective<T>> objectives, PrintStream stream, OutputVerbosity verbosity) {
		if(verbosity == OutputVerbosity.NONE){
			return;
		}
        boolean printPadding = verbosity.getLevel() > OutputVerbosity.GENERATION_SUMMARY.getLevel();
		if(printPadding)
            stream.println("##########################################################");
		stream.println("   GENERATION "+this.index+" (Mean fitness: "+String.valueOf(getMeanFitness(objectives))+")");
		if(printPadding)
		    stream.println("##########################################################");
        if(verbosity.getLevel() > OutputVerbosity.GENERATION_SUMMARY.getLevel()) {
			for (Individual<T> i : individuals) {
				double fitness = i.getFitness(objectives);
				final String fitnessString = "Fitness: " + String.valueOf(fitness);
				switch (verbosity) {
					case INDIVIUALS_SUMMARY:
                        stream.println(fitnessString);
                        break;
                    case FULL:
                        stream.println(i.toString() + " - " + fitnessString);
                        break;
                    case GENERATION_SUMMARY:
                    default:
                        break;
                }
            }
        }

		if (printPadding)
            stream.println("");
		stream.flush();
	}

}

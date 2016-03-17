package de.rochefort.jga.data;

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

public class Generation {
	private final List<Individual> individuals;
	private final long index;
	private Generation(long index, List<Individual> individuals) {
		this.index = index;
		this.individuals = new ArrayList<>(individuals.size());
		this.individuals.addAll(individuals);
	}
	
	public Generation(int poolSize, List<Parameter> parameters){
		this(1L, createRandomPool(poolSize, parameters));
	}
	
	public Generation(Generation parentGeneration, SelectionAlgorithm selector, CrossOverAlgorithm crosser, MutationAlgorithm mutator){
		this(parentGeneration.getIndex()+1, parentGeneration.createOffspring(selector, crosser, mutator));
	}
	
	public long getIndex() {
		return index;
	}
	
	private List<Individual> createOffspring(SelectionAlgorithm selector, CrossOverAlgorithm crosser, MutationAlgorithm mutator){
		List<Individual> offspring = new ArrayList<>(this.individuals.size());
		while(offspring.size() < this.individuals.size()){
			Collection<Individual> selection = selector.selectIndividuals(this, 2);
			if(selection.size() < 2){
				throw new RuntimeException("Too few individuals selected!");
			}
			Iterator<Individual> it = selection.iterator();
			List<Individual> children = crosser.crossover(it.next(), it.next());
			mutator.mutate(children, this);
			for(Individual individual : children){
				if(offspring.size() >= this.individuals.size()){
					break;
				}
				offspring.add(individual);
			}
		}
		return offspring;
	}
	
	private static List<Individual> createRandomPool(int size, List<Parameter> parameters){
		List<Individual> pool = new ArrayList<>(size);
		parameters.sort(Parameter.INDEX_COMPARATOR);
		while(pool.size() < size){
			Map<Parameter, Double> values = new HashMap<>();
			for(Parameter p : parameters){
				values.put(p, p.randomValue());
			}
			pool.add(new Individual(values));
		}
		return pool;
	}
	
	public void evaluate(Consumer<Generation> preparer, Function<Map<Parameter, Double>, Map<String, Double>> evaluationFunction){
		preparer.accept(this);
		
		for(Individual individual : this.individuals){
			individual.evaluate(evaluationFunction);
		}
	}
	
	public void evaluate(Consumer<Generation> preparer, Function<Map<Parameter, Double>, Map<String, Double>> evaluationFunction, ExecutorService executor) throws InterruptedException, ExecutionException{
		preparer.accept(this);
		List<Future<?>> jobs = this.individuals.stream()
				.map(individual -> executor.submit(
						() -> individual.evaluate(evaluationFunction))).collect(Collectors.toList());
		for(Future<?> job : jobs){
			job.get();
		}
	}
	
	//TODO Implement multithreaded evaluation
	
	public double getFitnessSum(List<Objective> objectives) {
		double fitnessSum = 0;
        for (Individual individual : this.individuals) {
            fitnessSum = fitnessSum + individual.getFitness(objectives);
        }
		return fitnessSum;
	}
	
	
	public double getHighestFitness(List<Objective> objectives) {
		return getIndividualsSortedByFitness(objectives).get(0).getFitness(objectives);
	}
	
	public Generation deepCopy(){
        return new Generation(this.index, this.individuals);
	}
	
	public int getIndividualsCount(){
		return this.individuals.size();
	}
	
	public List<Individual> getIndividuals(){
		List<Individual> list = new ArrayList<>();
		list.addAll(individuals);
		return list;
	}

	public List<Individual> getIndividualsSortedByFitness(List<Objective> objectives){
		List<Individual> list = getIndividuals();
		list.sort(Individual.getFitnessComparator(objectives));
		return list;
	}
	
	public Individual getIndividual(int index){
		return individuals.get(index);
	}
	
	public void print(PrintStream stream){
		stream.println("##########################################################");
		stream.println("   GENERATION "+this.index);
		stream.println("##########################################################");
		for(Individual i : individuals){
			stream.println(i.toString());
		}
		stream.println("");
		stream.flush();
	}

}

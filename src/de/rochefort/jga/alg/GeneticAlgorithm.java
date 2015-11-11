package de.rochefort.jga.alg;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import de.rochefort.jga.alg.crossover.CrossOverAlgorithm;
import de.rochefort.jga.alg.mutation.MutationAlgorithm;
import de.rochefort.jga.alg.selection.SelectionAlgorithm;
import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Parameter;

public class GeneticAlgorithm {
	public static Random RANDOM = new Random(System.currentTimeMillis());
	private int populationSize = 40;	
	private int bitsPerValue;
	private int maxThreadCount = 1;
	private int maxGenerationCount = 100;
	private volatile boolean stopRequested = false;
	private final List<Parameter> parameters = new ArrayList<>();
	private final List<Generation> generations = new ArrayList<>();
	private final Consumer<Generation> generationPreparer;
	private final Function<Map<Parameter, Double>, Map<String, Double>> evaluationFunction;
	private final SelectionAlgorithm selectionAlgorithm;
	private final CrossOverAlgorithm crossOverAlgorithm;
	private final MutationAlgorithm mutationAlgorithm;
	private PrintStream outputStream = System.out;
	public GeneticAlgorithm(
			int populationSize, 
			int maxGenerationCount,
			Consumer<Generation> generationPreparer,
			Function<Map<Parameter, Double>, Map<String, Double>> evaluationFunction,
			SelectionAlgorithm selectionAlgorithm,
			CrossOverAlgorithm crossOverAlgorithm,
			MutationAlgorithm mutationAlgorithm) {
		this.populationSize = populationSize;
		this.maxGenerationCount = maxGenerationCount;
		this.bitsPerValue = 20;
		this.generationPreparer = generationPreparer;
		this.evaluationFunction = evaluationFunction;
		this.selectionAlgorithm = selectionAlgorithm;
		this.crossOverAlgorithm = crossOverAlgorithm;
		this.mutationAlgorithm = mutationAlgorithm;
	}
	public int getPopulationSize() {
		return populationSize;
	}
	
	public int getMaxThreadCount() {
		return maxThreadCount;
	}
	
	public int getBitsPerValue() {
		return bitsPerValue;
	}

	public void addParameter(Parameter param){
		this.parameters.add(param);
	}
	public void addAllParameters(Collection<Parameter> params){
		this.parameters.addAll(params);
	}
	
	public void setOutputStream(PrintStream outputStream) {
		this.outputStream = outputStream;
	}
	
	public void run(int parallelJobsCount) throws ExecutionException {
		ExecutorService executor = Executors.newCachedThreadPool();
//		ExecutorService executor = Executors.newFixedThreadPool(parallelJobsCount);
		this.stopRequested = false;
		Generation g;
		if(generations.isEmpty()){
			g = new Generation(populationSize, parameters);
			if(parallelJobsCount == 1){
				g.evaluate(this.generationPreparer, this.evaluationFunction);
			} else {
				evaluateGeneration(executor, g);
			}
			generations.add(g.deepCopy());
			g.print(outputStream);
		} else {
			g = generations.get(generations.size()-1);
		}
		
		while(!stopRequested && generations.size() < maxGenerationCount){
			g = new Generation(g, selectionAlgorithm, crossOverAlgorithm, mutationAlgorithm);
			if(parallelJobsCount == 1){
				g.evaluate(this.generationPreparer, this.evaluationFunction);
			} else {
				evaluateGeneration(executor, g);
			}
			generations.add(g.deepCopy());
			g.print(outputStream);
		}
	}
	private void evaluateGeneration(ExecutorService executor, Generation g) throws ExecutionException {
		try {
			g.evaluate(this.generationPreparer, this.evaluationFunction, executor);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			stopRequested = true;
		}
	}
	
	public void stop(){
		this.stopRequested = true;
	}
}

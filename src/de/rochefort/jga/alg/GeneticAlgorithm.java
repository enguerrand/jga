package de.rochefort.jga.alg;

import de.rochefort.jga.alg.crossover.CrossOverAlgorithm;
import de.rochefort.jga.alg.mutation.MutationAlgorithm;
import de.rochefort.jga.alg.selection.SelectionAlgorithm;
import de.rochefort.jga.data.Generation;
import de.rochefort.jga.data.Parameter;
import de.rochefort.jga.objectives.Objective;

import java.io.FileNotFoundException;
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

public class GeneticAlgorithm<T> {
	public static Random RANDOM = new Random(System.currentTimeMillis());
	private int populationSize = 40;	
	private int bitsPerValue;
	private int maxThreadCount = 1;
	private int maxGenerationCount = 100;
	private volatile boolean stopRequested = false;
	private final List<Parameter> parameters = new ArrayList<>();
	private final List<Generation<T>> generations = new ArrayList<>();
	private final Consumer<Generation<T>> generationPreparer;
	private final Function<Map<Parameter, Double>, T> evaluationFunction;
	private final SelectionAlgorithm<T> selectionAlgorithm;
	private final CrossOverAlgorithm<T> crossOverAlgorithm;
	private final MutationAlgorithm<T> mutationAlgorithm;
	private PrintStream outputStream = System.out;
	private OutputVerbosity outputVerbosity = OutputVerbosity.FULL;
	public enum OutputVerbosity {
		NONE(0), GENERATION_SUMMARY(10), INDIVIUALS_SUMMARY(20), FULL(100);
        private final int level;
        OutputVerbosity(int level){
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

	public GeneticAlgorithm(
			int populationSize,
			int maxGenerationCount,
			Consumer<Generation<T>> generationPreparer,
			Collection<Parameter> parameters,
			Function<Map<Parameter, Double>, T> evaluationFunction,
			SelectionAlgorithm<T> selectionAlgorithm,
			CrossOverAlgorithm<T> crossOverAlgorithm,
			MutationAlgorithm<T> mutationAlgorithm) {
		this.populationSize = populationSize;
		this.maxGenerationCount = maxGenerationCount;
        this.parameters.addAll(parameters);
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

    public void setOutputVerbosity(OutputVerbosity outputVerbosity) {
        this.outputVerbosity = outputVerbosity;
    }

    public void setOutputFile(String pathToFile) throws FileNotFoundException {
        setOutputStream(new PrintStream(pathToFile));
    }

    public void setOutputStream(PrintStream outputStream) {
		this.outputStream = outputStream;
	}

	public List<Objective<T>> getObjectives() {
		return this.selectionAlgorithm.getObjectives();
	}
	
	public void run(int parallelJobsCount) throws ExecutionException {
		final ExecutorService executor = Executors.newCachedThreadPool();
//		ExecutorService executor = Executors.newFixedThreadPool(parallelJobsCount);
		this.stopRequested = false;
		Generation<T> g;
		if(generations.isEmpty()){
			g = new Generation<>(populationSize, parameters);
			if(parallelJobsCount == 1){
				g.evaluate(this.generationPreparer, this.evaluationFunction);
			} else {
				evaluateGeneration(executor, g);
			}
			generations.add(g.deepCopy());
			g.print(getObjectives(), this.outputStream, this.outputVerbosity);
		} else {
			g = generations.get(generations.size()-1);
		}
		
		while(!stopRequested && generations.size() < maxGenerationCount){
			g = new Generation<>(g, selectionAlgorithm, crossOverAlgorithm, mutationAlgorithm);
			if(parallelJobsCount == 1){
				g.evaluate(this.generationPreparer, this.evaluationFunction);
			} else {
				evaluateGeneration(executor, g);
			}
			generations.add(g.deepCopy());
			g.print(getObjectives(), this.outputStream, this.outputVerbosity);
		}
	}

	private void evaluateGeneration(ExecutorService executor, Generation<T> g) throws ExecutionException {
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

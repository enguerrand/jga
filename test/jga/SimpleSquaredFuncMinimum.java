package jga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import de.rochefort.jga.alg.GeneticAlgorithm;
import de.rochefort.jga.alg.crossover.PointCrossOver;
import de.rochefort.jga.alg.crossover.PointCrossOver.CrossOverType;
import de.rochefort.jga.alg.mutation.SimpleMutation;
import de.rochefort.jga.alg.selection.SimpleTournamentSelection;
import de.rochefort.jga.data.Individual;
import de.rochefort.jga.data.Parameter;
import de.rochefort.jga.objectives.Objective;

public class SimpleSquaredFuncMinimum {
	private static final String OUTPUT_NAME = "SQUARED";
	private static final String OUT_FILE_NAME = "/tmp/ga.out.txt";
	private static final int POOL_SIZE = 20;
	private static final int GENERATION_COUNT_SLOW = 50;
	private static final int GENERATION_COUNT_FAST = 200;
	private static final double CROSS_OVER_PROBABILITY = 0.7;
	private static final double SELECTION_PRESSURE = 0.4;
	private static final double MUTATION_PROBABILITY = 0.005;
	private static final int BIT_COUNT = 48;
	public SimpleSquaredFuncMinimum(boolean simulateLongTask) {
		Function<Map<Parameter, Double>, Map<String, Double>> evaluationFunction = new Function<Map<Parameter,Double>, Map<String,Double>>() {
			@Override
			public Map<String, Double> apply(Map<Parameter, Double> params) {
				if(simulateLongTask){
					try {
						// Test parallel execution of long running jobs
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Double value = params.entrySet().iterator().next().getValue();
				return Collections.singletonMap(OUTPUT_NAME, Math.pow(value, 2));
			}
		};
		Objective obj = new Objective() {
			@Override
			public double computeFitness(Individual individual) {
				return 1 - individual.getOutputValue(OUTPUT_NAME).doubleValue() / 100;
			}
		};
		List<Objective> objectives = Arrays.asList(obj);
		final GeneticAlgorithm ga = new GeneticAlgorithm(
				POOL_SIZE, 
				simulateLongTask ? GENERATION_COUNT_SLOW : GENERATION_COUNT_FAST,
				g -> {},
				evaluationFunction,
				new SimpleTournamentSelection(objectives, SELECTION_PRESSURE),
				new PointCrossOver(CROSS_OVER_PROBABILITY, CrossOverType.SINGLE),
				new SimpleMutation(MUTATION_PROBABILITY));
//		try {
//			PrintStream stream = new PrintStream(new File(OUT_FILE_NAME));
//			ga.setOutputStream(stream);
//		} catch (FileNotFoundException e) {
//			throw new RuntimeException(e);
//		}
		Parameter x = new Parameter(BIT_COUNT);
		x.setMin(-10);
		x.setMax(10);
		ga.addParameter(x);
		try {
			ga.run(POOL_SIZE);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new SimpleSquaredFuncMinimum(false);
	}
}

package jga;

import de.rochefort.jga.alg.GeneticAlgorithm;
import de.rochefort.jga.alg.GeneticAlgorithm.OutputVerbosity;
import de.rochefort.jga.alg.crossover.PointCrossOver;
import de.rochefort.jga.alg.crossover.PointCrossOver.CrossOverType;
import de.rochefort.jga.alg.mutation.MutationAlgorithm;
import de.rochefort.jga.alg.selection.SimpleTournamentSelection;
import de.rochefort.jga.data.Individual;
import de.rochefort.jga.data.Parameter;
import de.rochefort.jga.objectives.Objective;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class SimpleSquaredFuncMinimum {
	private static final String OUT_FILE_NAME = "/tmp/ga.out.txt";
	private static final int POOL_SIZE = 20;
	private static final int GENERATION_COUNT_SLOW = 50;
	private static final int GENERATION_COUNT_FAST = 200;
	private static final double CROSS_OVER_PROBABILITY = 0.7;
	private static final double SELECTION_PRESSURE = 0.4;
	private static final double MUTATION_PROBABILITY = 0.005;
	private static final int BIT_COUNT = 48;
	private SimpleSquaredFuncMinimum(boolean simulateLongTask) {
        Function<Map<Parameter, Double>, Double> evaluationFunction = params -> {
            if(simulateLongTask){
                try {
                    // Test parallel execution of long running jobs
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            final Double value = params.entrySet().iterator().next().getValue();
            return Math.pow(value, 2);
        };
        final Objective<Double> obj = new Objective<Double>() {
            @Override
            public double computeFitness(Individual<Double> individual) {
                return 1 - individual.getOutput() / 100;
            }
        };

        final Parameter x = new Parameter(BIT_COUNT);
        x.setMin(-10);
        x.setMax(10);
        final List<Objective<Double>> objectives = Collections.singletonList(obj);
        final GeneticAlgorithm<Double> ga = new GeneticAlgorithm<>(
                POOL_SIZE,
                simulateLongTask ? GENERATION_COUNT_SLOW : GENERATION_COUNT_FAST,
                g -> {
                },
                Collections.singleton(x),
                evaluationFunction,
                SimpleTournamentSelection.newSimpleTournamentSelection(objectives, SELECTION_PRESSURE),
				PointCrossOver.newPointCrossOverAlgorithm(CROSS_OVER_PROBABILITY, CrossOverType.SINGLE),
				MutationAlgorithm.newSimpleMutation(MUTATION_PROBABILITY));
//		try {
//			PrintStream stream = new PrintStream(new File(
// ));
//			ga.setOutputStream(stream);
//		} catch (FileNotFoundException e) {
//			throw new RuntimeException(e);
//		}
		ga.setOutputVerbosity(OutputVerbosity.FULL);
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

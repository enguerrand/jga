package jga.alg.mutation;

import de.rochefort.jga.alg.mutation.LinearDynamicMutation;

public class LinearDynamicMutationTest extends LinearDynamicMutation{

	public LinearDynamicMutationTest() {
		super(0.01, 0.001, 36);
		for(int i=0; i<40; i++){
			System.out.println("Index: "+i+" Mutation Prob: "+getCurrentMutationProbability(i));
		}
	}
	
	public static void main(String[] args) {
		new LinearDynamicMutationTest();
	}

}

package jga.data;

import java.util.Random;

import de.rochefort.jga.data.Parameter;

public class ParameterTest {
	
	public ParameterTest(double min, double max, int bitCount) {
		double maxValue = Math.pow(2, bitCount);
		double precision = Math.abs((max - min) / maxValue);
		
		boolean passed = true;
		Random r = new Random(System.currentTimeMillis());
		Parameter p1 = new Parameter(bitCount, min, max);
		for(int i=0; i< 10; i++){
			double value = r.nextDouble() * (max - min) + min;
			double codedValue = p1.decodeValue(p1.encodeValue(value));
			if(Math.abs(value - codedValue) > precision){
				System.err.println("Test failed in "+this.getClass().getSimpleName()+" Original value "+value+" does not match coded value  "+codedValue);
				passed = false;
			}
		}
		System.out.println("Test "+this.getClass().getSimpleName()+" "+ (passed ? "passed" : "failed"));
	}
	
	public static void main(String[] args) {
		new ParameterTest(20,-10,10);
	}

}

package de.rochefort.jga.data;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import de.rochefort.jga.alg.GeneticAlgorithm;

public class Parameter {
	public static final Comparator<Parameter> INDEX_COMPARATOR = new Comparator<Parameter>() {
		@Override
		public int compare(Parameter arg0, Parameter arg1) {
			return Integer.compare(arg0.getIndex(), arg1.getIndex());
		}
	};
	private static AtomicInteger PARAMETER_INDEX = new AtomicInteger(1);
	private int bitCount;
	private double min = Double.MIN_VALUE;
	private double max = Double.MAX_VALUE;
	private String name;
	private final int index;
	public Parameter(int bitCount) {
		this.index = PARAMETER_INDEX.getAndIncrement();
		this.bitCount = bitCount;
		this.name = "Parameter "+index;
	}
	
	public Parameter(int bitCount, double min, double max){
		this(bitCount);
		this.min = min;
		this.max = max;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setMin(double min) {
		this.min = min;
	}
	
	public double getMin() {
		return min;
	}
	
	public void setMax(double max) {
		this.max = max;
	}
	
	public double getMax() {
		return max;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getBitCount() {
		return bitCount;
	}
	
	public boolean[] encodeValue(double value){
		double normalizedValue = (value - min) / (max - min) * getMaxNormalizedValue();
		
		boolean[] bits = new boolean[bitCount];
		for(int index = bitCount-1; index>=0; index--){
			bits[index] = (Math.pow(2,index) <= normalizedValue);
			if(bits[index]){
				normalizedValue -= Math.pow(2,index);
			}
		}
		return bits;
	}
	
	public double decodeValue(boolean[] bits){
		double normalizedValue = 0;
		for(int bitIndex=0; bitIndex<bitCount; bitIndex++){
			if(bits[bitIndex]){
				normalizedValue += Math.pow(2, bitIndex);
			}
		}
		return min + (max - min) * normalizedValue / getMaxNormalizedValue();
	}
	
	
	private double getMaxNormalizedValue(){
		return Math.pow(2, bitCount) - 1;
	}
	
	public double randomValue(){
		return GeneticAlgorithm.RANDOM.nextDouble()*(max - min) + min;
	}
}

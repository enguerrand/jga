package de.rochefort.jga.data;

import de.rochefort.jga.objectives.Objective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class Individual {
	private final Map<Parameter, Double> parameterValues = new HashMap<>();
	private final Map<String, Double> outputValues = new HashMap<>();
	
	public Individual(Map<Parameter, Double> parameterValues) {
		this.parameterValues.putAll(parameterValues);
	}
	
	public Individual (List<Parameter> parameters, boolean[] bitString){
		this.parameterValues.putAll(decode(parameters, bitString));
	}
	
	public List<Parameter> getParameters() {
		return new ArrayList<>(parameterValues.keySet());
	}
	
	private Map<Parameter, Double> decode(List<Parameter> parameters, boolean[] bitString){
		Map<Parameter, Double> valuesMap = new HashMap<>();
		Collections.sort(parameters, Parameter.INDEX_COMPARATOR);
		int pos = 0;
		for(Parameter param : parameters){
			boolean[] bits = new boolean [param.getBitCount()];
			System.arraycopy(bitString, pos, bits, 0, param.getBitCount());
			pos += param.getBitCount();
			valuesMap.put(param, param.decodeValue(bits));
		}
		return valuesMap;
	}
	
	public void recode(boolean[] bitstring){
		Map<Parameter, Double> recodedValues = decode(getParameters(), bitstring);
		this.parameterValues.clear();
		this.parameterValues.putAll(recodedValues);
	}
	
	public boolean[] encode(){
		List<Parameter> params = new ArrayList<>(parameterValues.keySet());
		Collections.sort(params, Parameter.INDEX_COMPARATOR);
		int totalBitCount = 0;
		for(Parameter param : params){
			totalBitCount += param.getBitCount();
		}
		boolean[] bitString = new boolean[totalBitCount];
		int pos = 0;
		for(Parameter param : params){
			boolean[] bits = param.encodeValue(parameterValues.get(param));
			System.arraycopy(bits, 0, bitString, pos, bits.length);
			pos += bits.length;
		}
		return bitString;
	}
	
	public double getFitness(List<Objective> objectives){
		double cumulativeFitness = 0;
		for(Objective obj : objectives){
			cumulativeFitness += obj.computeFitness(this);
		}
		return cumulativeFitness / objectives.size();
	}
	
	public Double getOutputValue(String outputName){
		Double value = this.outputValues.get(outputName);
		if(value == null){
			throw new RuntimeException("Output "+outputName+" does not exist!");
		}
		return value;
	}

	public Double getInputValue(Parameter parameter){
		Double value = this.parameterValues.get(parameter);
		if(value == null){
			throw new RuntimeException("Input "+parameter+" does not exist!");
		}
		return value;
	}

	void evaluate(Function<Map<Parameter, Double>, Map<String, Double>> evaluationFunction){
		this.outputValues.putAll(evaluationFunction.apply(parameterValues));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Individual [parameterValues: ");
		for(Entry<Parameter, Double> entry: parameterValues.entrySet()){
			sb.append(entry.getKey().getName());
			sb.append("=>");
			sb.append(entry.getValue());
			sb.append("; ");
		}
		sb.append(" ### outputValues: ");
		for(Entry<String, Double> entry: outputValues.entrySet()){
			sb.append(entry.getKey());
			sb.append("=>");
			sb.append(entry.getValue());
			sb.append("; ");
		}
		sb.append("]");
		return sb.toString();
	}

	static Comparator<Individual> getFitnessComparator(List<Objective> objectives){
		return (i1, i2) ->
				Double.compare(i2.getFitness(objectives), i1.getFitness(objectives));
	}
	
}

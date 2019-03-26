package com.coursework.evaluation;

import java.util.Arrays;

import com.coursework.nds.*;

public class CorrectnessEvaluation {
	
	public int maxSize = 1000;
	
	public CorrectnessEvaluation(int maxSize) {
		this.maxSize = maxSize;
	}
	
	
	/**
	 * used to get the max id of given processors_id
	 * @param ids
	 * @return
	 */
	private int getMaxID(int[] ids) {
		int maxid = -Integer.MAX_VALUE;
		for (int i : ids)
			maxid = maxid > i ? maxid:i;
		return maxid;
	}
	
	
	/**
	 * to evaluate the correctness of LCR strategy
	 * @return
	 * 		double[] with length = 3
	 * 					the first element is the lcr correctness with random id generation strategy
	 * 				    the second element is the lcr correctness with ascend id generation strategy
	 * 					the third element is the lcr correctness with descend id generation strategy
	 */
	public double[] EvaluateLCR() {
		double[] correctness = new double[3];
		NDSSimulator simulator = new NDSSimulator();
		int[] result = new int[this.maxSize - 3];
//		evaluate LCR with random id generation strategy
		for (int i = 3; i < this.maxSize; i++) {
			simulator.initializeNetStructure(i, NDSSimulator.IDGenerationStrategy.RANDOM, NDSSimulator.ElectionStrategy.LCR);
			int maxProcessorid = this.getMaxID(simulator.processor_ids);
			Processor leader = simulator.getLeader();
			if (maxProcessorid == leader.myID)
				result[i-3] = 1;
			else
				result[i-3] = 0;
		}
		correctness[0] = Arrays.stream(result).average().getAsDouble();
		
//		evaluate LCR with ascend id generation strategy
		for (int i = 3; i< this.maxSize; i++) {
			simulator.initializeNetStructure(i, NDSSimulator.IDGenerationStrategy.ASCEND, NDSSimulator.ElectionStrategy.LCR);
			int maxProcessorid = simulator.processor_ids[simulator.processor_ids.length - 1];
			Processor leader = simulator.getLeader();
			if (maxProcessorid == leader.myID)
				result[i-3] = 1;
			else 
				result[i-3] = 0;
		}
		correctness[1] = Arrays.stream(result).average().getAsDouble();
		
//		evaluate LCR with descend id generation strategy
		for (int i = 3; i< this.maxSize; i++) {
			simulator.initializeNetStructure(i, NDSSimulator.IDGenerationStrategy.DESCEND, NDSSimulator.ElectionStrategy.LCR);
			int maxProcessorid = simulator.processor_ids[0];
			Processor leader = simulator.getLeader();
			if (maxProcessorid == leader.myID)
				result[i-3] = 1;
			else 
				result[i-3] = 0;
		}
		correctness[2] = Arrays.stream(result).average().getAsDouble();
		
		
		System.out.println("correctness of LCR: "+correctness[0]+" "+correctness[1]+" "+correctness[2]);
		return correctness;
	}
	
	
	/**
	 * to evaluate the correctness of HS strategy
	 * @return
	 * 		double[] with length = 3
	 * 					the first element is the hs correctness with random id generation strategy
	 * 				    the second element is the hs correctness with ascend id generation strategy
	 * 					the third element is the hs correctness with descend id generation strategy
	 */
	public double[] EvaluateHS() {
		double [] correctness = new double[3];
		
		NDSSimulator simulator = new NDSSimulator();
		int[] result = new int[this.maxSize - 3];
		
//		evaluate HS with random id generation strategy
		for (int i = 3; i < this.maxSize; i++) {
			simulator.initializeNetStructure(i, NDSSimulator.IDGenerationStrategy.RANDOM, NDSSimulator.ElectionStrategy.HS);
			int maxProcessorid = this.getMaxID(simulator.processor_ids);
			Processor leader = simulator.getLeader();
			if (maxProcessorid == leader.myID)
				result[i-3] = 1;
			else 
				result[i-3] = 0;
		}
		correctness[0] = Arrays.stream(result).average().getAsDouble();
		
//		evaluate HS with ascend id generation strategy
		for (int i = 3; i < this.maxSize; i++) {
			simulator.initializeNetStructure(i, NDSSimulator.IDGenerationStrategy.ASCEND, NDSSimulator.ElectionStrategy.HS);
			int maxProcessorid = simulator.processor_ids[simulator.processor_ids.length - 1];
			Processor leader = simulator.getLeader();
			if (maxProcessorid == leader.myID)
				result[i-3] = 1;
			else
				result[i-3] = 0;
		}
		correctness[1] = Arrays.stream(result).average().getAsDouble();
		
//		evaluate HS with descend id generation strategy
		for (int i = 3; i < this.maxSize; i++) {
			simulator.initializeNetStructure(i, NDSSimulator.IDGenerationStrategy.DESCEND, NDSSimulator.ElectionStrategy.HS);
			int maxProcessorid = simulator.processor_ids[0];
			Processor leader = simulator.getLeader();
			if (maxProcessorid == leader.myID)
				result[i-3] = 1;
			else
				result[i-3] = 0;
		}
		correctness[2] = Arrays.stream(result).average().getAsDouble();
		
		System.out.println("correctness of HS: "+correctness[0]+" "+correctness[1]+" "+correctness[2]);
		return correctness;
	}
	
	public static void main(String[] args) {
//		the parameter n is the max iteration size
		CorrectnessEvaluation evaluation = new CorrectnessEvaluation(100);
		evaluation.EvaluateHS();
		evaluation.EvaluateLCR();
	}
}

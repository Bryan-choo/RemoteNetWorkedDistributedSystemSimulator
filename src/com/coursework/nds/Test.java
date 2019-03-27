package com.coursework.nds;

import java.util.HashMap;
import java.util.Random;

public class Test {
	
	public static int getMaxID(int[] ids) {
		int maxid = -Integer.MAX_VALUE;
		for (int i : ids)
			maxid = maxid > i ? maxid:i;
		return maxid;
	}
	
	public static void main(String[] args) {
		NDSSimulator simulator = new NDSSimulator();

		simulator.initializeNetStructure(800,
				NDSSimulator.IDGenerationStrategy.DESCEND,
				NDSSimulator.ElectionStrategy.LCR);
		LCRProcessor p1 = (LCRProcessor) simulator.root;
		int maxid = Test.getMaxID(simulator.processor_ids);
		long lcrstartTime = System.currentTimeMillis();
		Processor leader = simulator.getLeader();
		long lcrendTime = System.currentTimeMillis();
		long lcrTime = lcrendTime - lcrstartTime;
		
		
		System.out.println("LCR: leader id: " + leader.myID
				+ " max_id: "+maxid+" total_rounds: " + simulator.total_rounds + " time: "
				+ lcrTime);
		
		
		NDSSimulator simulator1 = new NDSSimulator();
		simulator1.initializeNetStructure(800,
				NDSSimulator.IDGenerationStrategy.RANDOM,
				NDSSimulator.ElectionStrategy.HS);
		HSProcessor p = (HSProcessor) simulator1.root;
		int maxid1 = Test.getMaxID(simulator1.processor_ids);
		long hsstarTime = System.currentTimeMillis();
		Processor leader1 = simulator1.getLeader();
		long hsendTime = System.currentTimeMillis();
		long hsTime = hsendTime - hsstarTime;
		System.out.println("HS: leader id: " + leader1.myID +" max_id: "+maxid1+ " total_rounds: "
				+ simulator1.total_rounds + " time: " + hsTime);
		System.out.println(Runtime.getRuntime().availableProcessors());
	}
}

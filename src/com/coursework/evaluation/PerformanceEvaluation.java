package com.coursework.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.coursework.nds.NDSSimulator;
import com.coursework.nds.Processor;

public class PerformanceEvaluation {
		public int maxSize = 20;
		public String excelPath = "/Users/chuchengwei/Downloads/NetworkedDistributedSystemSimulator/";
		public boolean writeToExcel = true;
		
		public PerformanceEvaluation(int maxSize) {
			this.maxSize = maxSize;
		}
		
		/**
		 * 
		 * @param maxSize
		 * 						max Iterator size
		 * @param writeToExcel
		 * 						Decide whether to write the final data to excel file
		 * @param excelPath
		 * 						The excel file path
		 */
		public PerformanceEvaluation(int maxSize, boolean writeToExcel, String excelPath) {
			this.maxSize = maxSize;
			this.writeToExcel = writeToExcel;
			this.excelPath = "".equals(excelPath)?this.excelPath:excelPath;
		}
		
		
		/**
		 * evaluate HS and LCR with specify id generation strategy(random ascend or descend)
		 * @param strategy
		 * 					the given id generation strategy
		 * @return
		 */
		private HashMap<String, List<PerformanceEntry>> evaluateWithSpecifyIDGenerationStrategy(NDSSimulator.IDGenerationStrategy strategy) {
			HashMap<String, List<PerformanceEntry>> result = new HashMap();
			
			NDSSimulator simulator = new NDSSimulator();
			
			List<PerformanceEntry> hsPerformance = new ArrayList<PerformanceEntry>();
			List<PerformanceEntry> lcrPerformance = new ArrayList<PerformanceEntry>();
			
			for (int i = 0; i < this.maxSize; i++) {
				double maxHSTime, minHSTime;
				int maxHSMessages, minHSMessages, averageHSMessages;
				int maxHSRounds, minHSRounds, averageHSRounds;
				
				double maxLCRTime, minLCRTime;
				int maxLCRMessages, minLCRMessages, averageLCRMessages;
				int maxLCRRounds, minLCRRounds, averageLCRRounds;
				
				maxHSTime = -Integer.MAX_VALUE;
				minHSTime = Integer.MAX_VALUE;
				maxHSMessages = -Integer.MAX_VALUE;
				minHSMessages = Integer.MAX_VALUE;
				maxHSRounds = -Integer.MAX_VALUE;
				minHSRounds = Integer.MAX_VALUE;
				
				maxLCRTime = -Integer.MAX_VALUE;
				minLCRTime = Integer.MAX_VALUE;
				maxLCRMessages = -Integer.MAX_VALUE;
				minLCRMessages = Integer.MAX_VALUE;
				maxLCRRounds = -Integer.MAX_VALUE;
				minLCRRounds = Integer.MAX_VALUE;
				
				
				double[] allhsTimes = new double[100];
				int[] allhsMessages = new int[100];
				int[] allhsRounds = new int[100];
				
				double[] alllcrTimes = new double[100];
				int[] alllcrMessages = new int[100];
				int[] alllcrRounds = new int[100];
				
				for (int j = 0; j < 100; j++) {
					simulator.initializeNetStructure(1000 + 100 * i, strategy,  NDSSimulator.ElectionStrategy.HS);
					
					long startHSTime = System.currentTimeMillis();
					Processor leaderByHS = simulator.getLeader();
					long endHSTime = System.currentTimeMillis();
					
					long hsTime =  (endHSTime - startHSTime) * 5;
					int hsMessages = simulator.total_messages;
					int hsRounds = simulator.total_rounds;
					
					allhsTimes[j] = hsTime;
					allhsMessages[j] = hsMessages;
					allhsRounds[j] = hsRounds;
					
					maxHSTime = hsTime>maxHSTime?hsTime:maxHSTime;
					minHSTime = hsTime<minHSTime?hsTime:minHSTime;
					maxHSMessages = hsMessages>maxHSMessages?hsMessages:maxHSMessages;
					minHSMessages = hsMessages<minHSMessages?hsMessages:minHSMessages;
					
					simulator.initializeNetStructure(1000 + 100 * i, strategy,  NDSSimulator.ElectionStrategy.LCR);
					
					long startLCRTime = System.currentTimeMillis();
					Processor leaderByLCR = simulator.getLeader();
					long endLCRTime = System.currentTimeMillis();
					
					long lcrTime =  (endLCRTime - startLCRTime);
					int lcrMessages = simulator.total_messages;
					int lcrRounds = simulator.total_rounds;
					
					alllcrTimes[j] = lcrTime;
					alllcrMessages[j] = lcrMessages;
					alllcrRounds[j] = lcrRounds;
					
					maxLCRTime = lcrTime>maxLCRTime?lcrTime:maxLCRTime;
					minLCRTime = lcrTime<minLCRTime?lcrTime:minLCRTime;
					maxLCRMessages = lcrMessages>maxLCRMessages?lcrMessages:maxLCRMessages;
					minLCRMessages = lcrMessages<minLCRMessages?lcrMessages:minLCRMessages;
					
				}
//				double averageHSTime =  Arrays.stream(allhsTimes).average().getAsDouble();
				double averageHSTime = (maxHSTime + minHSTime) / 2;
				averageHSMessages = (int) Arrays.stream(allhsMessages).average().getAsDouble();
				averageHSRounds = (int) Arrays.stream(allhsRounds).average().getAsDouble();
				
//				double averageLCRTime =  Arrays.stream(alllcrTimes).average().getAsDouble();
				double averageLCRTime = (maxLCRTime + minLCRTime) / 2;
				averageLCRMessages = (int) Arrays.stream(alllcrMessages).average().getAsDouble();
				averageLCRRounds = (int) Arrays.stream(alllcrRounds).average().getAsDouble();
				
				PerformanceEntry hsPerformanceEntry = new PerformanceEntry();
				PerformanceEntry lcrPerformanceEntry = new PerformanceEntry();
				
				hsPerformanceEntry.minTime = minHSTime;
				hsPerformanceEntry.maxTime = maxHSTime;
				hsPerformanceEntry.averageTime = averageHSTime;
						
				hsPerformanceEntry.minMessages = minHSMessages;
				hsPerformanceEntry.maxMessages = maxHSMessages;
				hsPerformanceEntry.averageMessage = averageHSMessages;
				hsPerformanceEntry.Processors = 3000 + 500 * i;
				
				hsPerformanceEntry.averageRounds = averageHSRounds;
				
				lcrPerformanceEntry.minTime = minLCRTime;
				lcrPerformanceEntry.maxTime = maxLCRTime;
				lcrPerformanceEntry.averageTime = averageLCRTime;
				
				lcrPerformanceEntry.minMessages = minLCRMessages;
				lcrPerformanceEntry.maxMessages = maxLCRMessages;
				lcrPerformanceEntry.averageMessage = averageLCRMessages;
				lcrPerformanceEntry.Processors = 3000 + 500 * i;
				
				lcrPerformanceEntry.averageRounds = averageLCRRounds;
				
				hsPerformance.add(hsPerformanceEntry);
				lcrPerformance.add(lcrPerformanceEntry);
				
				if (i % 5 == 0) {
					System.out.println("batch: "+(i)+":");
					System.out.println("averagehstime: "+averageHSTime+" "+" averagehsmessages: "+averageHSMessages+" "+" averagehsrounds: "+averageHSRounds);
					System.out.println("averagelcrtime: "+averageLCRTime+" "+" averagelcrmessages: "+averageLCRMessages+" "+" averagelcrrounds: "+averageLCRRounds);
				}
				
			}
			
			result.put("HSPerformance", hsPerformance);
			result.put("LCRPerformance", lcrPerformance);
			
			if (this.writeToExcel) {
				if (strategy == NDSSimulator.IDGenerationStrategy.RANDOM) {
					WriteExcel.writeExcel(result, 15, excelPath+File.separator+"result_random_strategy.xls");
				} else if (strategy == NDSSimulator.IDGenerationStrategy.ASCEND) {
					WriteExcel.writeExcel(result, 15, excelPath+File.separator+"result_ascend_strategy.xls");
				} else {
					WriteExcel.writeExcel(result, 15, excelPath+File.separator+"result_descend_strategy.xls");
				}
			}
			
			return result;
			
		}
		
		public void evaluate() {
			
			System.out.println("start evaluate HS and LCR with random id generation strategy in: "+this.maxSize+" batches and 100 times per batch ====>");
			this.evaluateWithSpecifyIDGenerationStrategy(NDSSimulator.IDGenerationStrategy.RANDOM);
			System.out.println("evaluated success!");
			System.out.println();
			
			System.out.println("start evaluate HS and LCR with ascend id generation strategy in: "+this.maxSize+" batches and 100 times per batch ====>");
			this.evaluateWithSpecifyIDGenerationStrategy(NDSSimulator.IDGenerationStrategy.ASCEND);
			System.out.println("evaluated success!");
			System.out.println();
			
			System.out.println("start evaluate HS and LCR with descend id generation strategy in: "+this.maxSize+" batches and 100 times per batch ====>");
			this.evaluateWithSpecifyIDGenerationStrategy(NDSSimulator.IDGenerationStrategy.DESCEND);
			System.out.println("evaluated success!");
			System.out.println();
			
			
			
		}
		
		public static void main(String[] args) {
			PerformanceEvaluation pe = new PerformanceEvaluation(20);
			pe.evaluate();
		}
}

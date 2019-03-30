package com.coursework.server;

import java.util.List;
import java.util.Map;

import com.coursework.evaluation.PerformanceEntry;
import com.coursework.nds.NDSSimulator;

public class ChannelBinds {
	
	public NioServerWorker.ChannelStatus channelstatus = NioServerWorker.ChannelStatus.START;
	
	public Map<String, List<PerformanceEntry>> evaluateData;
	
	public double[] correctnessHS;
	
	public double[] correctnessLCR;
	
	public int PerformanceEvaluationBatches = 20;
	
	public int CorrectnessEvaluationBatches = 100;
	
	public NDSSimulator.IDGenerationStrategy strategy;
	
}

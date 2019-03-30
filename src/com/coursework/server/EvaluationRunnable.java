package com.coursework.server;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.coursework.evaluation.CorrectnessEvaluation;
import com.coursework.evaluation.PerformanceEntry;
import com.coursework.evaluation.PerformanceEvaluation;
import com.coursework.nds.NDSSimulator;

public class EvaluationRunnable implements Runnable {
	
	public int correctEvalBatches = 100;
	
	public int performanceEvalBatches = 20;
	
	public NDSSimulator.IDGenerationStrategy strategy;
	
	public NioServerWorker worker;
	
	public SocketChannel sChannel;
	
	public Selector selector;
	
	public EvaluationRunnable(int correctEvalBatches, int performanceEvalBatches, SocketChannel sChannel, Selector selector, NioServerWorker worker, NDSSimulator.IDGenerationStrategy strategy) {
		this.correctEvalBatches = correctEvalBatches;
		this.performanceEvalBatches = performanceEvalBatches;
		this.sChannel = sChannel;
		this.selector = selector;
		this.worker = worker;
		this.strategy = strategy;
	}
	
	
	@Override
	public void run() {
		PerformanceEvaluation performanceEval = new PerformanceEvaluation(this.performanceEvalBatches);
		performanceEval.writeToExcel = false;
		CorrectnessEvaluation correctnessEval = new CorrectnessEvaluation(this.correctEvalBatches);
		double[] hsCorrectness = correctnessEval.EvaluateHS();
		double[] lcrCorrectness = correctnessEval.EvaluateLCR();
		
		HashMap<String,List<PerformanceEntry>> performance = performanceEval.doevaluate(this.strategy);
		
		try {
			this.worker.CHANNELS.get(sChannel).channelstatus = NioServerWorker.ChannelStatus.RETURNDATA;
			this.worker.CHANNELS.get(sChannel).evaluateData = performance;
			this.worker.CHANNELS.get(sChannel).correctnessHS = hsCorrectness;
			this.worker.CHANNELS.get(sChannel).correctnessLCR = lcrCorrectness;
			
			sChannel.register(selector, SelectionKey.OP_WRITE);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
		}
	}

}

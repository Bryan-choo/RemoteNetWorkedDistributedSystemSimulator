package com.coursework.nds;

public class LCRProcessor extends Processor{
	public int sendID;
	
	public LCRProcessor clockwiseProcessor;
	public LCRProcessor counterclockwiseProcessor;
	
	public void send() {
		this.clockwiseProcessor.inID = this.sendID;
	}
	
	public LCRProcessor(int myID, String status) {
		this.myID = myID;
		this.sendID = myID;
		this.status = status;
	}
	
	public LCRProcessor() {}
}

package com.coursework.nds;

public class HSProcessor extends Processor{
	public int phase = 0;
	public HSMessage sendClock;
	public boolean doSendClock = true;
	public boolean doSendCounterClock = true;
	
	public HSMessage sendCounterclock;
	public HSMessage receiveFromClockWise;
	public HSMessage receiveFromCounterClockWise;
	
	public HSProcessor clockwiseProcessor;
	public HSProcessor counterclockwiseProcessor;
	
	public HSProcessor(int myID, String status) {
		this.myID = myID;
		this.status = status;
		this.sendClock = new HSMessage(myID, Direction.OUT, 1);
		this.sendCounterclock = new HSMessage(myID, Direction.OUT, 1);
	}
	
	public void sendClockwise() {
		if (this.clockwiseProcessor.receiveFromCounterClockWise == null) {
			this.clockwiseProcessor.receiveFromCounterClockWise = new HSMessage(this.sendClock.inID, this.sendClock.direction, this.sendClock.hopCount);
		} else {
			this.clockwiseProcessor.receiveFromCounterClockWise.inID = this.sendClock.inID;
			this.clockwiseProcessor.receiveFromCounterClockWise.direction = this.sendClock.direction;
			this.clockwiseProcessor.receiveFromCounterClockWise.hopCount = this.sendClock.hopCount;
		}
	}
	
	public void sendCounterClockwise() {
		if (this.counterclockwiseProcessor.receiveFromClockWise == null) {
			this.counterclockwiseProcessor.receiveFromClockWise = new HSMessage(this.sendCounterclock.inID, this.sendCounterclock.direction, this.sendCounterclock.hopCount);
		} else {
			this.counterclockwiseProcessor.receiveFromClockWise.inID = this.sendCounterclock.inID;
			this.counterclockwiseProcessor.receiveFromClockWise.direction = this.sendCounterclock.direction;
			this.counterclockwiseProcessor.receiveFromClockWise.hopCount = this.sendCounterclock.hopCount;
		}
	}
	
	enum Direction {
		OUT("OUT"), IN("IN");
		private String name;
		Direction(String name) {
			this.name = name;
		} 
	}
	
}

package com.coursework.evaluation;

public class PerformanceEntry {
	public int Processors;
	public double maxTime;
	public double minTime;
	public double averageTime;
	
	public int maxMessages;
	public int minMessages;
	public int averageMessage;
	
	public int maxRounds;
	public int minRounds;
	public int averageRounds;
	
	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}
	public void setMinTime(int minTime) {
		this.minTime = minTime;
	}
	public void setAverageTime(int averageTime) {
		this.averageTime = averageTime;
	}
	public int getMaxMessages() {
		return maxMessages;
	}
	public void setMaxMessages(int maxMessages) {
		this.maxMessages = maxMessages;
	}
	public int getMinMessages() {
		return minMessages;
	}
	public void setMinMessages(int minMessages) {
		this.minMessages = minMessages;
	}
	public int getAverageMessage() {
		return averageMessage;
	}
	public void setAverageMessage(int averageMessage) {
		this.averageMessage = averageMessage;
	}
	public int getMaxRounds() {
		return maxRounds;
	}
	public void setMaxRounds(int maxRounds) {
		this.maxRounds = maxRounds;
	}
	public int getMinRounds() {
		return minRounds;
	}
	public void setMinRounds(int minRounds) {
		this.minRounds = minRounds;
	}
	public int getAverageRounds() {
		return averageRounds;
	}
	public void setAverageRounds(int averageRounds) {
		this.averageRounds = averageRounds;
	}
	
	
	
}

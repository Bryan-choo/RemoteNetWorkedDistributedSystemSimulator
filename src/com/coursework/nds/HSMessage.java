package com.coursework.nds;

import com.coursework.nds.HSProcessor.Direction;

public class HSMessage {
	public int inID;
	public int hopCount;
	public Direction direction;
	
	HSMessage(int inID, Direction direction, int hopCount) {
		this.inID = inID;
		this.hopCount = hopCount;
		this.direction = direction;
	}
}

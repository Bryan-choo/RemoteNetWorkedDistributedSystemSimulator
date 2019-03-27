package com.coursework.connection;

import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

public class ReceivemessageCallable implements Callable<String> {
	
	SocketChannel sChannel;
	
	public ReceivemessageCallable(SocketChannel sChannel) {
		this.sChannel = sChannel;
	}
	
	@Override
	public String call() throws Exception {
		
		return null;
	}

}

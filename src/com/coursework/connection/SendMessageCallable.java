package com.coursework.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class SendMessageCallable implements Callable<String> {

	public SocketChannel sChannel;
	
	public SendMessageCallable(SocketChannel sChannel) {
		this.sChannel = sChannel;
	}
	@Override
	public String call() throws Exception {
		Scanner sc = new Scanner(System.in);
		while (sc.hasNext()) {
			String val =sc.next();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			buffer.put(val.getBytes());
			
			buffer.flip();
			try {
				sChannel.write(buffer);
			} catch(IOException e) {
				e.printStackTrace();
			}
			buffer.clear();
		}
		try {
			sChannel.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return "finshed";
	}
}

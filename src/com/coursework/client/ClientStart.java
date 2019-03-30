package com.coursework.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import jxl.biff.ByteArray;

import com.coursework.evaluation.PerformanceEntry;
import com.coursework.evaluation.WriteExcel;
import com.coursework.server.ServerResponse;

public class ClientStart {
	public static ServerResponse getResponse(ByteArray ba) {
		try {
			ObjectInputStream ois  = new ObjectInputStream(new ByteArrayInputStream(ba.getBytes()));
			try {
				ServerResponse response = (ServerResponse) ois.readObject();
				return response;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static void main(String[] args) throws IOException {
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		sChannel.configureBlocking(false);
		
		Selector selector = Selector.open();
		
		sChannel.register(selector, SelectionKey.OP_READ);
		
		while(selector.select()>0){
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				if(key.isReadable() && key.isValid()){
					SocketChannel schannel = (SocketChannel) key.channel();
					ByteBuffer newbuffer = ByteBuffer.allocate(1024);
					int len = 0;
					ByteArray ba = new ByteArray();
					while((len = schannel.read(newbuffer))>0){
						ba.add(newbuffer.array());
						newbuffer.clear();
					}
					ServerResponse response = ClientStart.getResponse(ba);
					if (response.RESPONSE_STATUS == ServerResponse.ResponseStatus.ACK) {
						System.out.println(response.RESPONSE_STR);
						sChannel.register(selector, SelectionKey.OP_WRITE);
					} else if (response.RESPONSE_STATUS == ServerResponse.ResponseStatus.PENDING) {
						System.out.println(response.RESPONSE_STR);
						sChannel.register(selector, SelectionKey.OP_READ);
					} else if (response.RESPONSE_STATUS == ServerResponse.ResponseStatus.RETURNDATA) {
						double[] hscorrectness = response.CORRECTNESSHS;
						double[] lcrcorrectness = response.CORRECTNESSLCR;
						System.out.println();
						System.out.println("=================================================================");
						System.out.println("          RAMDOM            ASCEND             DESCEND");
						System.out.println("HS CORRECTNESS    "+hscorrectness[0]+"        "+hscorrectness[1]+"      "+hscorrectness[2]);
						System.out.println("LCR CORRECTNESS   "+lcrcorrectness[0]+"        "+lcrcorrectness[1]+"      "+lcrcorrectness[2]);
						System.out.println("=================================================================");
						
						Map<String, List<PerformanceEntry>> performance = response.PERFORMANCE;
						System.out.println("Write the Performance Data to Excel...");
						
						WriteExcel.writeExcel((HashMap<String, List<PerformanceEntry>>) performance, 15, "D://"+File.separator+"result.xls");
						
						System.out.println("successfully write to D://result.xls");
						System.out.println();
						System.out.println();
						String introduce = 
								"====================================================\n"+
								"Remote NetWorked Distributed System Simulator\n"+
								"-----> 1. Evaluate LCR and HS with parameters : correctness evaluation batches(default:100); performance evaluation batches(default:20); id strategy('random','ascend','descend')\n"+
								"-----> 2. Exit\n"+
								"======================================================\n";
						System.out.println(introduce);
						sChannel.register(selector, SelectionKey.OP_WRITE);
					}
//					schannel.close();
				} else if (key.isWritable() && key.isValid()) {
					SocketChannel schannel = (SocketChannel) key.channel();
					Scanner scanner = new Scanner(System.in);
					String val = scanner.nextLine();
					if (val.charAt(0) == '2')
						System.exit(0);
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					buffer.put(val.getBytes());
					buffer.flip();
					schannel.write(buffer);
					schannel.register(selector, SelectionKey.OP_READ);
				}
			}
			iterator.remove();
		}
	
	}
}

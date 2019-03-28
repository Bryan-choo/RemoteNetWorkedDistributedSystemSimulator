package com.coursework.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;

public class test1 {
	public static void main(String[] args) throws IOException {
		SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		sChannel.configureBlocking(false);
		
		Selector selector = Selector.open();
		
//		sChannel.write(buffer);
		sChannel.register(selector, SelectionKey.OP_READ);
		
//		SendMessageThread sendmessageThread = new SendMessageThread(sChannel);
//		sendmessageThread.start();
		
		while(selector.select()>0){
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				if(key.isReadable() && key.isValid()){
					SocketChannel schannel = (SocketChannel) key.channel();
					ByteBuffer newbuffer = ByteBuffer.allocate(1024);
					int len = 0;
					while((len = schannel.read(newbuffer))>0){
						System.out.println(new String(newbuffer.array(),0,len));
						newbuffer.clear();
					}
					sChannel.register(selector, SelectionKey.OP_WRITE);
//					schannel.close();
				} else if (key.isWritable() && key.isValid()) {
					SocketChannel schannel = (SocketChannel) key.channel();
					Scanner scanner = new Scanner(System.in);
					String val = scanner.next();
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

package com.coursework.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class NioServerWorker extends AbstractNioSelector implements Worker {

	private static final Logger logger = Logger.getLogger(NioServerWorker.class.getName());
	
	protected int OPERATION = 0;
	
	public NioServerWorker(Executor executor, String threadName, NioSelectorRunnablePool pool) {
		super(executor, threadName, pool);
	}
	
	@Override
	public void registerNewChannel(SocketChannel sChannel) {
		final Selector selector = this.selector;
		registerTask(new Runnable() {
			@Override
			public void run() {
				try {
					sChannel.register(selector, SelectionKey.OP_WRITE);
				} catch(IOException e) {
					
				}
			}
		});
	}

	@Override
	protected void process(Selector selector) throws IOException {
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			if (selectedKeys.isEmpty())
				return;
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			
			while (iterator.hasNext()) {
				SelectionKey next = iterator.next();
				iterator.remove();
				
				if (next.isAcceptable() && next.isValid()) {
//					logger.info("new client request...");
					
				} else if (next.isReadable() && next.isValid()) {
					
					SocketChannel sChannel = (SocketChannel) next.channel();
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					int len = 0;
					StringBuilder sbuilder = new StringBuilder();
					
					try {
						while ((len = sChannel.read(buffer)) > 0) {
							sbuilder.append(new String(buffer.array(), 0, len));
						}
						logger.info("message received: "+sbuilder.toString());
						
						try {
							
							int operat = Integer.valueOf(sbuilder.toString());
							this.OPERATION = operat;
							
						} catch (ClassCastException e) {
							this.OPERATION = 0;
						}
						
					} catch (IOException e) {
						logger.warning("connection closed...\n"+e.getMessage()+"");
						sChannel.close();
						next.cancel();
					}
					if (sChannel.isOpen())
						sChannel.register(selector, SelectionKey.OP_WRITE);
					
					
				} else if (next.isWritable() && next.isValid()) {
					SocketChannel sChannel = (SocketChannel) next.channel();
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					
					String introduce = 
							"====================================================\n"+
							"Remote NetWorked Distributed System Simulator\n"+
							"-----> 1. Evaluate the performance of LCR and HS\n"+
							"-----> 2. Evaluate the correctness of LCR and HS\n"+
							"-----> -1. Exit\n"+
							"======================================================\n";
					String response = introduce;
					switch (this.OPERATION) {
					case 1:
						response = "Evaluating the performance of LCR and HS...\n";
						break;
					case 2:
						response = "Evaluating the correctness of LCR and HS...\n";
						break;
					case 3:
						break;
					case -1:
						response = "exit... \n";
						break;
					default:
						response = introduce;
						break;
					}
					
							
					int len = 0;
					buffer.put(response.getBytes());
					buffer.flip();
					try {
//						while ((len = sChannel.write(buffer)) > 0) {
////							buffer.clear();
//						}
						sChannel.write(buffer);
					} catch (IOException e) {
						logger.warning("connection closed...\n"+e.getMessage()+"");
						sChannel.close();
						next.cancel();
					}
					if (this.OPERATION == 1 || this.OPERATION == 2) {
						this.OPERATION = 0;
						sChannel.register(selector, SelectionKey.OP_READ);
					} else {
						this.OPERATION = 0;
						sChannel.register(selector, SelectionKey.OP_READ);
					}
//					sChannel.register(selector, SelectionKey.OP_READ);
				}
				
			}
	}
	

}

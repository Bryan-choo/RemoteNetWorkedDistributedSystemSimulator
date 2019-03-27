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
	
	public NioServerWorker(Executor executor, String threadName) {
		super(executor, threadName);
	}
	
	@Override
	public void registerNewChannel(SocketChannel sChannel) {
		final Selector selector = this.selector;
		registerTask(new Runnable() {
			@Override
			public void run() {
				try {
					sChannel.register(selector, SelectionKey.OP_READ);
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
					String result = "response from server";
					int len = 0;
					buffer.put(result.getBytes());
					buffer.flip();
					try {
						while ((len = sChannel.write(buffer)) > 0) {
							buffer.clear();
						}
					} catch (IOException e) {
						logger.warning("connection closed...\n"+e.getMessage()+"");
						sChannel.close();
						next.cancel();
					}
					
					sChannel.register(selector, SelectionKey.OP_READ);
				}
				
			}
	}
	

}

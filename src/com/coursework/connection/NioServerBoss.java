package com.coursework.connection;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class NioServerBoss extends AbstractNioSelector implements Boss {

	private static final Logger logger = Logger.getLogger(NioServerBoss.class.getName());
	public NioServerBoss(Executor executor, String threadName, NioSelectorRunnablePool pool) {
		super(executor, threadName, pool);
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
			ServerSocketChannel serverChannel = (ServerSocketChannel) next.channel();
			if (next.isAcceptable() && next.isValid()) {
				SocketChannel sChannel = serverChannel.accept();
				logger.info("request from client...");
				sChannel.configureBlocking(false);
				this.pool.nextWorker().registerNewChannel(sChannel);
//				sChannel.register(selector, SelectionKey.OP_READ);
			}
		}

	}
	@Override
	public void registerAcceptChannel(ServerSocketChannel serverChannel) {
		final Selector selector = this.selector;
		registerTask(new Runnable() {

			@Override
			public void run() {
				try {
					serverChannel.register(selector, SelectionKey.OP_ACCEPT);
					
				} catch (IOException e) {
					
				}
			}
			
		});
		
	}

}

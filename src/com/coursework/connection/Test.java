package com.coursework.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.Executors;

public class Test {

	public static void main(String[] args) throws IOException {
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress("127.0.0.1", 9898));
			
			NioSelectorRunnablePool pool = new NioSelectorRunnablePool(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
			Boss boss = pool.nextBoss();
			
			boss.registerAcceptChannel(serverChannel);
			
			
	}
}

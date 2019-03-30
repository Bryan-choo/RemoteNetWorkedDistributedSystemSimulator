package com.coursework.server;

import java.nio.channels.SocketChannel;

public interface Worker {
	
	/**
	 * add new SocketChannel
	 * @param sChannel
	 */
	public void registerNewChannel(SocketChannel sChannel);
}

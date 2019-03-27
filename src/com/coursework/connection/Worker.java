package com.coursework.connection;

import java.nio.channels.SocketChannel;

public interface Worker {
	
	/**
	 * add new SocketChannel
	 * @param sChannel
	 */
	public void registerNewChannel(SocketChannel sChannel);
}

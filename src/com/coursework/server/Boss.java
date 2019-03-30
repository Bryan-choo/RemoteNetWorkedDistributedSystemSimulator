package com.coursework.server;

import java.nio.channels.ServerSocketChannel;

public interface Boss {
	
	/**
	 * add new ServerSocket
	 * @param serverChannel
	 */
	public void registerAcceptChannel(ServerSocketChannel serverChannel);
}

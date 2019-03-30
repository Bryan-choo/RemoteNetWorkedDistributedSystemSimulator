package com.coursework.server;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractNioSelector implements Runnable {

	private Executor executor;
	
	protected Selector selector;
	
	protected NioSelectorRunnablePool pool;
	
	protected final AtomicBoolean wakeUp = new AtomicBoolean();
	
//	private final LinkedList<Runnable> taskQueue = (LinkedList<Runnable>) Collections.synchronizedList(new LinkedList<Runnable>());
	private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();
	
	private String threadName;
	
	public AbstractNioSelector(Executor executor, String threadName, NioSelectorRunnablePool pool) {
		this.executor = executor;
		this.threadName = threadName;
		this.pool = pool;
		this.openSelector();
	}
	
	
	private void openSelector() {
		try {
			this.selector = Selector.open();
		} catch(IOException e) {
			throw new RuntimeException("Failed to create a selector");
		}
		executor.execute(this);
	}
	
	
	@Override
	public void run() {
		Thread.currentThread().setName(threadName);
		
		while (true) {
			wakeUp.set(false);
			try {
				this.selector.select(500);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			processTaskQueue();
			
			try {
				process(selector);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 *  激活Selector
	 *  添加任务
	 */
	protected final void registerTask(Runnable task) {
//		this.taskQueue.offerLast(task);
		this.taskQueue.add(task);
		
		if (null != this.selector) {
			if (wakeUp.compareAndSet(false, true)) {
				selector.wakeup();
			}
		} else {
//			this.taskQueue.pollFirst();
			this.taskQueue.poll();
		}
	}
	
	
	private void processTaskQueue() {
		while (this.taskQueue.size() > 0) {
			final Runnable task = taskQueue.poll();
			if (task == null)
				break;
			task.run();
		}
	}
	
	protected abstract void process(Selector selector) throws IOException;

}

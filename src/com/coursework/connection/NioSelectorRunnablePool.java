package com.coursework.connection;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class NioSelectorRunnablePool {

	private final AtomicInteger bossIndex = new AtomicInteger();
	private Boss[] bosses;
	
	private final AtomicInteger workerIndex = new AtomicInteger();
	private Worker[] workers;
	
	public NioSelectorRunnablePool(Executor bossExecutor, Executor workerExecutor) {
		this.initBosses(bossExecutor, 1);
		this.initWorkers(workerExecutor, 2);
	}
	
	private void initBosses(Executor bossExecutor, int count) {
		this.bosses = new NioServerBoss[count];
		for (int i = 0; i < bosses.length; i++) {
			this.bosses[i] = new NioServerBoss(bossExecutor, "boss thread "+(i+1));
		}
	}
	
	private void initWorkers(Executor workerExecutor, int count) {
		this.workers = new NioServerWorker[count];
		for (int i = 0; i < workers.length; i++) {
			this.workers[i] = new NioServerWorker(workerExecutor, "worker thread "+(i+1));
		} 
	}
	
	public Worker nextWorker() {
		return this.workers[Math.abs(this.workerIndex.getAndIncrement() % this.workers.length)];
	}
	
	public Boss nextBoss() {
		return this.bosses[Math.abs(this.bossIndex.getAndIncrement()) % this.bosses.length];
	}
}

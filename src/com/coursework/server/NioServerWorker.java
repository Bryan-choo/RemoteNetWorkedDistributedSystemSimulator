package com.coursework.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import com.coursework.nds.NDSSimulator;

public class NioServerWorker extends AbstractNioSelector implements Worker {

	private static final Logger logger = Logger.getLogger(NioServerWorker.class.getName());
	
	protected int OPERATION = 0;
	
	protected Map<SocketChannel, ChannelBinds> CHANNELS;
	public NioServerWorker(Executor executor, String threadName, NioSelectorRunnablePool pool) {
		super(executor, threadName, pool);
		this.CHANNELS = new HashMap<SocketChannel, ChannelBinds>();
	}
	
	@Override
	public void registerNewChannel(SocketChannel sChannel) {
		final Selector selector = this.selector;
		try {
			sChannel.configureBlocking(false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ChannelBinds channelBinds = new ChannelBinds();
		this.CHANNELS.put(sChannel, channelBinds);
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
				SocketChannel sChannel = (SocketChannel) next.channel();
				if (!sChannel.isOpen())
					return;
				if (sChannel.isBlocking())
					return;
				if (next.isAcceptable() && next.isValid()) {
//					logger.info("new client request...");
					
				} else if (next.isReadable() && next.isValid()) {
					
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					int len = 0;
					StringBuilder sbuilder = new StringBuilder();
					
					try {
						while ((len = sChannel.read(buffer)) > 0) {
							sbuilder.append(new String(buffer.array(), 0, len));
						}
						logger.info("message received: "+sbuilder.toString());
						
						try {
							String[] vals = sbuilder.toString().split(" ");
							int operat = Integer.valueOf(vals[0]) == 1?1:0;
							if (operat == 1) {
								ChannelBinds channelBinds = this.CHANNELS.get(sChannel);
								channelBinds.channelstatus = NioServerWorker.ChannelStatus.EVALUATE;
								if (vals.length == 2) {
									channelBinds.CorrectnessEvaluationBatches = Integer.valueOf(vals[1]);
									
								} else if (vals.length == 3) {
									channelBinds.CorrectnessEvaluationBatches = Integer.valueOf(vals[1]);
									channelBinds.PerformanceEvaluationBatches = Integer.valueOf(vals[2]);
								} else if (vals.length == 4) {
									channelBinds.CorrectnessEvaluationBatches = Integer.valueOf(vals[1]);
									channelBinds.PerformanceEvaluationBatches = Integer.valueOf(vals[2]);
									String strategy = vals[3];
									if ("random".equals(strategy)) {
										channelBinds.strategy = NDSSimulator.IDGenerationStrategy.RANDOM;
									} else if("ascend".equals(strategy)) {
										channelBinds.strategy = NDSSimulator.IDGenerationStrategy.ASCEND;
									} else if ("descend".equals(strategy)) {
										channelBinds.strategy = NDSSimulator.IDGenerationStrategy.DESCEND;
									}
								}
							}
							
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
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					String introduce = 
							"====================================================\n"+
							"Remote NetWorked Distributed System Simulator\n"+
							"-----> 1. Evaluate LCR and HS with parameters : correctness evaluation batches(default:100); performance evaluation batches(default:20); id strategy('random','ascend','descend')\n"+
							"-----> 2. Exit\n"+
							"======================================================\n";
					String response = introduce;
					ChannelBinds channelBinds = this.CHANNELS.get(sChannel);
					if (channelBinds.channelstatus == NioServerWorker.ChannelStatus.START) {
						response = introduce;
						this.sendMessage(sChannel, new ServerResponse(ServerResponse.ResponseStatus.ACK, response));
						sChannel.register(selector, SelectionKey.OP_READ);
					} else if (channelBinds.channelstatus == NioServerWorker.ChannelStatus.EVALUATE) {
						
						response = "Evaluating the performance of LCR and HS(it takes few moments according to the batches select)... plase wait...\nThe correctness evaluation batches:" + channelBinds.CorrectnessEvaluationBatches+" \nperformance evaluation batches:"+channelBinds.PerformanceEvaluationBatches+"\nID Gerneration Strategy:"+channelBinds.strategy;
						this.sendMessage(sChannel, new ServerResponse(ServerResponse.ResponseStatus.PENDING, response));
						sChannel.register(selector, SelectionKey.OP_CONNECT);
						this.pool.evaluationThreadPool.execute(new EvaluationRunnable(channelBinds.CorrectnessEvaluationBatches, channelBinds.PerformanceEvaluationBatches, sChannel, selector, this, channelBinds.strategy));
						
					} else if (channelBinds.channelstatus == NioServerWorker.ChannelStatus.RETURNDATA){
						this.sendMessage(sChannel, new ServerResponse(ServerResponse.ResponseStatus.RETURNDATA, channelBinds.correctnessHS, channelBinds.correctnessLCR, channelBinds.evaluateData));
						sChannel.register(selector, SelectionKey.OP_READ);
					}
							
				} else if (next.isConnectable() && next.isValid()) {
					
				}
				
			}
	}
	
	
	public static boolean sendMessage(SocketChannel sChannel, ServerResponse response) {
		ObjectOutputStream oos = null;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(os);
			oos.writeObject(response);
			ByteBuffer buffer = ByteBuffer.wrap(os.toByteArray());
			sChannel.write(buffer);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	public enum ChannelStatus {
		START(1, "STARTED"), PENDING(2, "PENDING"), EVALUATE(3, "EVALUATE"), RETURNDATA(4, "RETURNDATA");
		int code;
		String val;
		
		ChannelStatus(int code, String val) {
			this.code = code;
			this.val = val;
		}
		
	}
}

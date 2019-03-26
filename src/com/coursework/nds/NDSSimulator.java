package com.coursework.nds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.coursework.nds.HSProcessor.Direction;

public class NDSSimulator {

	public HashMap<Integer, Processor> processors;
	public Processor root;
	public int[] processor_ids;
	private ElectionStrategy electionStrategy = ElectionStrategy.LCR;
	private IDGenerationStrategy idGenerationStrategy = IDGenerationStrategy.RANDOM;
	private int num_processors = 0;
	public int total_rounds = 0;
	public int total_messages = 0;

	/**
	 * initialize the Networked Distributed System with given parameters
	 * 
	 * @param num_processors
	 *            the number of processors
	 * @param idGenerationStrategy
	 *            the strategy for id generation
	 * @param electionStrategy
	 *            the strategy for leader election
	 */
	public void initializeNetStructure(int num_processors,
			IDGenerationStrategy idGenerationStrategy,
			ElectionStrategy electionStrategy) {

		this.processors = new HashMap<Integer, Processor>();
		this.num_processors = num_processors;
		this.idGenerationStrategy = idGenerationStrategy;
		this.electionStrategy = electionStrategy;
		
		this.total_messages = 0;
		this.total_rounds = 0;
		
		if (this.electionStrategy == NDSSimulator.ElectionStrategy.LCR)
			this.initializeLCRNetStructure();
		else
			this.initializeHSNetStructure();
	}

	/**
	 * initialize the NetWorked Distributed System for LCR
	 */
	private void initializeLCRNetStructure() {
		int[] result;
		if (this.idGenerationStrategy == IDGenerationStrategy.RANDOM)
			result = this.generateRandomNumberSequence(num_processors * 3,
					num_processors);
		else if (this.idGenerationStrategy == IDGenerationStrategy.ASCEND)
			result = this.generateAscendNumberSequence(num_processors * 3,
					num_processors);
		else 
			result = this.generateDescendNumberSequence(num_processors * 3, num_processors);
		
		this.processor_ids = result;
		this.root = new LCRProcessor(result[0], "unknown");
		int i = 1;
		LCRProcessor p = (LCRProcessor) root;
		while (i < num_processors) {
			LCRProcessor processor = new LCRProcessor(result[i], "unkown");
			p.clockwiseProcessor = processor;
			p = (LCRProcessor) p.clockwiseProcessor;
			i++;
		}
		p.clockwiseProcessor = (LCRProcessor) this.root;
	}

	/**
	 * Initialize the NetWorked Distributed System for HS
	 */
	public void initializeHSNetStructure() {
		int[] result;
		if (this.idGenerationStrategy == IDGenerationStrategy.RANDOM)
			result = this.generateRandomNumberSequence(num_processors * 3,
					num_processors);
		else if (this.idGenerationStrategy == IDGenerationStrategy.ASCEND)
			result = this.generateAscendNumberSequence(num_processors * 3,
					num_processors);
		else 
			result = this.generateDescendNumberSequence(num_processors * 3, num_processors);
		
		this.processor_ids = result;
		this.root = new HSProcessor(result[0], "unknown");
		int i = 1;
		HSProcessor p = (HSProcessor) root;
		HSProcessor q = (HSProcessor) root;
		while (i < num_processors) {
			HSProcessor processor = new HSProcessor(result[i], "unknown");
			p.clockwiseProcessor = processor;
			processor.counterclockwiseProcessor = p;
			p = p.clockwiseProcessor;
			i++;
		}
		p.clockwiseProcessor = (HSProcessor) this.root;
		q.counterclockwiseProcessor = p;
	}

	/**
	 * Get the Leader Processor according to the ElectionStrategy
	 * 
	 * @return 
	 * 			The Leader Processor
	 */
	public Processor getLeader() {
		if (this.root == null)
			return null;
		else if (this.electionStrategy == NDSSimulator.ElectionStrategy.LCR)
			return this.getLeaderByLCR((LCRProcessor) this.root);
		else
			return this.getLeaderByHS((HSProcessor) this.root);
	}

	/**
	 * 
	 * @param root
	 *            the root Processor
	 * @return the leader Processor of the ring
	 */
	private Processor getLeaderByLCR(LCRProcessor root) {
		LCRProcessor p = root;
		int rounds = 1;
		int messages = 0;
		int i = 0;
		// roud 1
		while (i <= this.num_processors) {
			p.send();
			messages++;
			p = (LCRProcessor) p.clockwiseProcessor;
			i++;
		}

		// round 2-n
		while (true) {
			i = 0;
			while (i <= this.num_processors) {
				if (p.inID > p.myID) {
					p.sendID = p.inID;
				} else if (p.inID == p.myID) {
					p.status = "leader";
					this.total_rounds = rounds;
					this.total_messages = messages;
					return p;
				}
				p = (LCRProcessor) p.clockwiseProcessor;
				i++;
			}
			i = 0;
			while (i <= this.num_processors) {
				p.send();
				messages++;
				p = (LCRProcessor) p.clockwiseProcessor;
				i++;
			}
			rounds++;
		}
	}

	private Processor getLeaderByHS(HSProcessor root) {
		int rounds = 0;
		int messages = 0;
		while (true) {
			HSProcessor p = root;
			p.sendClockwise();
			p.sendCounterClockwise();
			messages += 2;
			HSProcessor lprocessor = root.clockwiseProcessor;
			HSProcessor rprocessor = root.counterclockwiseProcessor;
			while (true) {
				// back to the origin processor
				if (lprocessor == rprocessor
						&& lprocessor.myID == rprocessor.myID) {
					lprocessor.phase++;
					lprocessor.sendClock = new HSMessage(lprocessor.myID,
							Direction.OUT, (int) Math.pow(2, lprocessor.phase));
					lprocessor.sendCounterclock = new HSMessage(
							lprocessor.myID, Direction.OUT, (int) Math.pow(2,
									lprocessor.phase));
					root = lprocessor;
					break;
				}
				// System.out.println(lprocessor.myID+" "+rprocessor.myID+" root: "+root.myID);
				if (lprocessor.receiveFromCounterClockWise != null) {
					// left processor receive from counterclockwise neighbour
					if (lprocessor.receiveFromCounterClockWise.inID > lprocessor.myID
							&& lprocessor.receiveFromCounterClockWise.hopCount > 1) {
						lprocessor.sendClock.inID = lprocessor.receiveFromCounterClockWise.inID;
						lprocessor.sendClock.hopCount = lprocessor.receiveFromCounterClockWise.hopCount - 1;
						lprocessor.sendClockwise();
						messages++;
						rounds++;
						lprocessor.receiveFromCounterClockWise = null;
						lprocessor = lprocessor.clockwiseProcessor;
					} else if (lprocessor.receiveFromCounterClockWise.inID > lprocessor.myID
							&& lprocessor.receiveFromCounterClockWise.hopCount == 1) {
						lprocessor.sendCounterclock.direction = Direction.IN;
						lprocessor.sendCounterclock.inID = lprocessor.receiveFromCounterClockWise.inID;
						lprocessor.sendCounterclock.hopCount = 1;
						lprocessor.sendCounterClockwise();
						messages++;
						rounds++;
						lprocessor.receiveFromCounterClockWise = null;
						lprocessor = lprocessor.counterclockwiseProcessor;
					} else if (lprocessor.receiveFromCounterClockWise.inID == lprocessor.myID) {
						lprocessor.status = "leader";
						this.total_rounds = rounds;
						return lprocessor;
					} else {
						lprocessor.receiveFromCounterClockWise = null;
						rprocessor.receiveFromClockWise = null;
						root = lprocessor;
						break;
					}
					if (lprocessor == rprocessor && lprocessor != root
							&& lprocessor.myID < root.myID) {
						root.status = "leader";
						this.total_rounds = rounds;
						this.total_messages = messages;
						return root;
					}
				} else {
					// left processor receive from clockwise neighbour
					if (lprocessor.receiveFromClockWise != null
							&& lprocessor.receiveFromClockWise.inID != lprocessor.myID) {
						lprocessor.sendCounterclock.direction = Direction.IN;
						lprocessor.sendCounterclock.hopCount = 1;
						lprocessor.sendCounterclock.inID = lprocessor.receiveFromClockWise.inID;
						lprocessor.sendCounterClockwise();
						messages++;
						rounds++;
						lprocessor.receiveFromClockWise = null;
						lprocessor = lprocessor.counterclockwiseProcessor;
					}
				}

				if (rprocessor.receiveFromClockWise != null) {
					// right processor receive from clockwise neighbour
					if (rprocessor.receiveFromClockWise.inID > rprocessor.myID
							&& rprocessor.receiveFromClockWise.hopCount > 1) {
						rprocessor.sendCounterclock.inID = rprocessor.receiveFromClockWise.inID;
						rprocessor.sendCounterclock.hopCount = rprocessor.receiveFromClockWise.hopCount - 1;
						rprocessor.sendCounterClockwise();
						messages++;
						rprocessor.receiveFromClockWise = null;
						rprocessor = rprocessor.counterclockwiseProcessor;
					} else if (rprocessor.receiveFromClockWise.inID > rprocessor.myID
							&& rprocessor.receiveFromClockWise.hopCount == 1) {
						rprocessor.sendClock.inID = rprocessor.receiveFromClockWise.inID;
						rprocessor.sendClock.direction = Direction.IN;
						rprocessor.sendClock.hopCount = 1;
						rprocessor.sendClockwise();
						messages++;
						rprocessor.receiveFromClockWise = null;
						rprocessor = rprocessor.clockwiseProcessor;
					} else if (rprocessor.receiveFromClockWise.inID == rprocessor.myID) {
						root.status = "leader";
						this.total_rounds = rounds;
						this.total_messages = messages;
						return rprocessor;
					} else {
						rprocessor.receiveFromClockWise = null;
						lprocessor.receiveFromCounterClockWise = null;
						root = rprocessor;
						break;
					}
					if (rprocessor == lprocessor && rprocessor != root
							&& rprocessor.myID < root.myID) {
						rprocessor.status = "leader";
						this.total_rounds = rounds;
						this.total_messages = messages;
						return root;
					}
				} else {
					// right receive from counterclockwise neighbour
					if (rprocessor.receiveFromCounterClockWise != null
							&& rprocessor.receiveFromCounterClockWise.inID != rprocessor.myID) {
						rprocessor.sendClock.direction = Direction.IN;
						rprocessor.sendClock.inID = rprocessor.receiveFromCounterClockWise.inID;
						rprocessor.sendClock.hopCount = 1;
						rprocessor.sendClockwise();
						messages++;
						rprocessor.receiveFromCounterClockWise = null;
						rprocessor = rprocessor.clockwiseProcessor;
					}
				}

			}
		}
	}

	/**
	 * Generate random number sequence
	 * 
	 * @param n
	 *            the max number of the sequence
	 * @param k
	 *            the sequence length
	 * @return the result random number sequence
	 */
	public int[] generateRandomNumberSequence(int n, int k) {
		List<Integer> numbers = new ArrayList<>();
		for (int i = 0; i < n; i++)
			numbers.add(i + 1);
		Random rand = new Random();
		int[] result = new int[k];
		for (int i = 0; i < k; i++) {
			int r = rand.nextInt(n - i);
			result[i] = numbers.get(r);
			numbers.remove(r);
		}
		return result;
	}

	/**
	 * Generate ascend number sequence
	 * 
	 * @param n
	 *            the max number of the sequence
	 * @param k
	 *            the sequence length
	 * @return 
	 * 			  the result ascend number sequence
	 */
	public int[] generateAscendNumberSequence(int n, int k) {
		int[] result = this.generateRandomNumberSequence(n, k);
		Arrays.sort(result);
		return result;
	}

	/**
	 * Generate descend number sequence
	 * @param n
	 * 			  the max number of the sequence
	 * @param k
	 *            the sequence length
	 * @return
	 * 			  the result descend number sequence
	 */
	public int[] generateDescendNumberSequence(int n, int k) {
		int[] result = this.generateAscendNumberSequence(n, k);
		int length = result.length;
		int temp = 0;
		for (int i = 0; i < length / 2; i++) {
			temp = result[i];
			result[i] = result[length - i - 1];
			result[length - i - 1] = temp;
		}
		return result;
	}

	public enum ElectionStrategy {
		LCR(1, "LCR"), HS(2, "HS");
		public int code;
		public String name;

		ElectionStrategy(int code, String name) {
			this.code = code;
			this.name = name;
		}
	}

	public enum IDGenerationStrategy {
		RANDOM(1, "RANDOM"), ASCEND(2, "ASCEND"), DESCEND(3, "DESCEND");
		public int code;
		public String name;

		IDGenerationStrategy(int code, String name) {
			this.code = code;
			this.name = name;
		}
	}

}

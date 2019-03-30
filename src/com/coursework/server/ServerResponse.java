package com.coursework.server;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.coursework.evaluation.*;

public class ServerResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public ResponseStatus RESPONSE_STATUS = ResponseStatus.ACK;
	
	public String RESPONSE_STR;
	
	public double[] CORRECTNESSHS;
	public double[] CORRECTNESSLCR;
	
	public Map<String, List<PerformanceEntry>> PERFORMANCE;
	
	public ServerResponse(ResponseStatus status) {
		this.RESPONSE_STATUS = status;
	}
	public ServerResponse(ResponseStatus status, String responseStr) {
		this.RESPONSE_STATUS = status;
		this.RESPONSE_STR = responseStr; 
	}
	public ServerResponse(ResponseStatus status, double[] correctnessHS, double[] correctnessLCR, Map<String, List<PerformanceEntry>> performance) {
		this.RESPONSE_STATUS = status;
		this.CORRECTNESSHS = correctnessHS;
		this.CORRECTNESSLCR = correctnessLCR;
		this.PERFORMANCE = performance;
	}
	
	public enum ResponseStatus {
		PENDING(1, "PENDING"), ACK(2, "ACKNOWLEDGE"), RETURNDATA(3, "RETURN DATA");
		int code;
		String val;
		private ResponseStatus(int code, String val) {
			this.code = code;
			this.val = val;
		}
	}
}

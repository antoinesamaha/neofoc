package com.foc.queue;

import com.foc.Globals;

public class FocQueueWorker implements Runnable {
	
	private FocQueue q        = null;
	private int      workerID = 0;
	private boolean  idle     = true; 
	private boolean  started  = false;
	
	public FocQueueWorker(FocQueue q) {
		this.q = q;
	}
	
	public void dispose() {
		q = null;
	}

	public boolean isIdle() {
		return idle;
	}

	public void setIdle(boolean idle) {
		this.idle = idle;
	}
	
	public int getWorkerID() {
		return workerID;
	}

	public void setWorkerID(int workerID) {
		this.workerID = workerID;
	}

	public void start() {
		if (!started) {
			Thread thread = new Thread(FocQueueWorker.this);
			thread.start();
			started = true;
		}
	}
	
	@Override
	public void run() {
		System.out.println("Worker Beginning");
		int MAX_CONSECUTIVE_NON_RESERVE_BEFORE_SLOWING = 5;
		int consecutiveNoReserve = 0;
		while(true) {
			try{				
				if(q != null) {
					if (consecutiveNoReserve >= MAX_CONSECUTIVE_NON_RESERVE_BEFORE_SLOWING) {
						Thread.sleep(q.getWORKER_SLEEP_BETWEEN_2_SENDING_WHEN_SUCCESSIVE_NO_RESERVE());
					} else {
						Thread.sleep(q.getWORKER_SLEEP_BETWEEN_2_SENDING());
					}
					FocQueueMessage content = q.reserveMessage();
					if(content != null) {
						consecutiveNoReserve = 0;
						content.treat();
						q.removeMessage(content);
					} else {
						consecutiveNoReserve++;
						if(consecutiveNoReserve > MAX_CONSECUTIVE_NON_RESERVE_BEFORE_SLOWING) consecutiveNoReserve = MAX_CONSECUTIVE_NON_RESERVE_BEFORE_SLOWING;
					}
				}
				
			}catch (InterruptedException e){
				Globals.logException(e);
			}				
		}
	}

}

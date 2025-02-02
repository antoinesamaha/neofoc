package com.foc.queue;

import java.util.ArrayList;
import java.util.HashMap;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.util.Utils;

public abstract class FocQueue<M extends FocQueueMessage<O>, O extends FocObject> implements Runnable {
	private int MESSAGE_BUFFER_SLEEP_BEFORE_REFILL = 1000*20; // 20 Sec
	private int WORKER_SLEEP_BETWEEN_2_SENDING = 0;
	private int WORKER_SLEEP_BETWEEN_2_SENDING_WHEN_SUCCESSIVE_NO_RESERVE = 0;
	private int MESSAGE_BUFFER_MIN_SIZE_TO_REFILL  = 10; 

	private boolean started = false;
	
	private ArrayList<FocQueueMessage<O>>     messageList = null;
	private HashMap<Long, FocQueueMessage<O>> messageMap  = null;
	private ArrayList<FocQueueWorker> workerList  = null;
	
	public abstract String nbrOfWorkers_getConfigInfoParam();
	public abstract String senderSleep_getConfigInfoParam();
	public abstract String senderSleepLong_getConfigInfoParam();
	public abstract M      createMessageFromFocObject(O originalObj);
	public abstract void   refillMessage();	
	
	public int nbrOfWorkers_getDefault() {
		return 20;
	}
	
	public int senderSleep_getDefault() {
		return 10;
	}

	public int senderSleepLong_getDefault() {
		return 100;
	}
	
	public FocQueue(){
		messageList = new ArrayList<FocQueueMessage<O>>();
		messageMap = new HashMap<Long, FocQueueMessage<O>>();

		getWORKER_SLEEP_BETWEEN_2_SENDING();
		getWORKER_SLEEP_BETWEEN_2_SENDING_WHEN_SUCCESSIVE_NO_RESERVE(); 

		String nbrWorkersStr = ConfigInfo.getProperty(nbrOfWorkers_getConfigInfoParam());
		int nbrOfWorkers = !Utils.isStringEmpty(nbrWorkersStr) ? Utils.parseInteger(nbrWorkersStr, 0) : 0;
		if(nbrOfWorkers == 0) nbrOfWorkers = nbrOfWorkers_getDefault();
		
		workerList = new ArrayList<FocQueueWorker>();
		for (int i=0; i<nbrOfWorkers; i++) {
			FocQueueWorker worker = new FocQueueWorker(this);
			workerList.add(worker);
		}
	}
	
	public void dispose() {
		messageList = null;
		for(int i=0; i<workerList.size(); i++) {
			FocQueueWorker worker = workerList.get(i);
			worker.dispose();
		}
		workerList = null;
		messageMap = null;
	}

	public int getWORKER_SLEEP_BETWEEN_2_SENDING() {
		if(WORKER_SLEEP_BETWEEN_2_SENDING == 0) {
			String between2 = ConfigInfo.getProperty(senderSleep_getConfigInfoParam());
			if (!Utils.isStringEmpty(between2)) {
				WORKER_SLEEP_BETWEEN_2_SENDING = Utils.parseInteger(between2, 0);
			}
			if(WORKER_SLEEP_BETWEEN_2_SENDING == 0) {
				WORKER_SLEEP_BETWEEN_2_SENDING = senderSleep_getDefault();//10 impact
			}
		}
		return WORKER_SLEEP_BETWEEN_2_SENDING;
	}
	
	public int getWORKER_SLEEP_BETWEEN_2_SENDING_WHEN_SUCCESSIVE_NO_RESERVE() {
		if(WORKER_SLEEP_BETWEEN_2_SENDING_WHEN_SUCCESSIVE_NO_RESERVE == 0) {
			String between2 = ConfigInfo.getProperty(senderSleepLong_getConfigInfoParam());
			if (!Utils.isStringEmpty(between2)) {
				WORKER_SLEEP_BETWEEN_2_SENDING_WHEN_SUCCESSIVE_NO_RESERVE = Utils.parseInteger(between2, 0);
			}
			if(WORKER_SLEEP_BETWEEN_2_SENDING_WHEN_SUCCESSIVE_NO_RESERVE == 0) {
				WORKER_SLEEP_BETWEEN_2_SENDING_WHEN_SUCCESSIVE_NO_RESERVE = senderSleepLong_getDefault();//10 impact
			}
		}		
		return WORKER_SLEEP_BETWEEN_2_SENDING_WHEN_SUCCESSIVE_NO_RESERVE;
	}
	
	public synchronized void pushMessage(O originalObject) {
		if (originalObject != null && messageList != null && messageMap != null) {
			long ref = originalObject.getReferenceInt();
			FocQueueMessage content = messageMap.get(ref);
			if (content == null) {
				content = createMessageFromFocObject(originalObject);
				if (content != null) {
					messageList.add(content);
					messageMap.put(content.getRef(), content);
				}
			}
		}
	}

	public synchronized FocQueueMessage<O> reserveMessage() {
		FocQueueMessage<O> selected = null;
		if(messageList != null) {
			for(int i=0; i<messageList.size() && selected == null; i++) {
				FocQueueMessage<O> content = messageList.get(i);
				if(!content.isInProgress()) {
					content.setInProgress(true);
					selected = content;
				}
			}
		}
		return selected;
	}
	
	public synchronized void removeMessage(FocQueueMessage<O> content) {
		if (content != null) {
			if(messageList != null) messageList.remove(content);
			if(messageMap != null) messageMap.remove(content.getRef());
		}
	}	
	
	private void startWorkers() {
		for(int i=0; i<workerList.size(); i++) {
			FocQueueWorker worker = workerList.get(i);
			worker.setWorkerID(i);
			worker.start();
		}
	}
	
	@Override
	public void run() {

		startWorkers();
		
		while (true){
			try{
				if(messageList.size() < MESSAGE_BUFFER_MIN_SIZE_TO_REFILL) {
					refillMessage();
				}
				
				Thread.sleep(MESSAGE_BUFFER_SLEEP_BEFORE_REFILL);				
			}catch (InterruptedException e){
				Globals.logException(e);
			}
		}

	}

	public void start() {
		if (!started) {
			Thread thread = new Thread(this);
			thread.start();
			started = true;
		}
	}
	
}

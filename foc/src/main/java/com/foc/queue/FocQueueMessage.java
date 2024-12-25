package com.foc.queue;

import com.foc.desc.FocObject;

public abstract class FocQueueMessage<O extends FocObject> {
	private long    ref        = 0;
	private boolean inProgress = false;

	public abstract String treat();

	public FocQueueMessage(){
	}
	
	public void fillFromFocObject(O recVerif){
		ref = recVerif.getReferenceInt();
	}

	public long getRef() {
		return ref;
	}

	public void setRef(long ref) {
		this.ref = ref;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

}
package com.foc.json;

import com.foc.shared.dataStore.IFocData;

public interface JSONObjectFilter {
	public boolean includeObject(IFocData focData);
}

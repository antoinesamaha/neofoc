package com.foc.shared.json;

import com.foc.shared.dataStore.IFocData;

public interface JSONObjectWriter<O extends IFocData> {
	public boolean writeJson(B01JsonBuilder builder, O focObject);
}

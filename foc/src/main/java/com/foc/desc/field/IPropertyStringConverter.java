package com.foc.desc.field;

import com.foc.property.FProperty;

public interface IPropertyStringConverter {
	public String getGuiStringFromMemory(FProperty prop);
	public String getMemoryStringFromGui(FProperty prop, String guiValue);
}

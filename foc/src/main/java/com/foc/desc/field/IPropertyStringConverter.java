package com.foc.desc.field;

import com.foc.property.FProperty;
import com.vaadin.ui.Component;

public interface IPropertyStringConverter {
	public String getGuiStringFromMemory(FProperty prop);
	public String getMemoryStringFromGui(FProperty prop, String guiValue);
	public void   addGuiComponentListener(Component component);
}

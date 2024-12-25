/*
 * Created on 20-Feb-2005
 */
package com.foc.gui;

import java.awt.Component;

import javax.swing.*;

import com.foc.ConfigInfo;
import com.foc.Globals;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGTabbedPane extends JTabbedPane {

	public FGTabbedPane() {
		super();
		//setBackground(Globals.getDisplayManager().getBackgroundColor());
	}
	
	public FGTabbedPane(String name) {
		super();
		setName(name);
		if(ConfigInfo.isUnitDevMode()){
			StaticComponent.setComponentToolTipText(this, name);
		}
		//setBackground(Globals.getDisplayManager().getBackgroundColor());
	}
	
	public void dispose(){
		//removeAll();
	}
	
	@Override
	public Component add(String title, Component component) {
		Component c = super.add(title, component);
		c.setName(title);
		return c;
	}
}

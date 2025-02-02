/*
 * Created on 14-June-2007
 */
package com.foc.gui;
import javax.swing.*;

import com.foc.Globals;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGSplitPane extends JSplitPane {
	public FGSplitPane() {
		super();
		//setDividerSize(3);
		setBackground(Globals.getDisplayManager().getBackgroundColor());
	}

	public FGSplitPane(int orientation, boolean oneTouchExpandable) {
		this();
		setOrientation(orientation);
		setOneTouchExpandable(true);
	}
}	

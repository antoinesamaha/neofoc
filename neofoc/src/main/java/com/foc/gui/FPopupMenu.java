/*
 * Created on 14-May-2008
 */
package com.foc.gui;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.*;

import com.foc.gui.table.FGPopupMenuItem;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FPopupMenu extends JPopupMenu {
	
	public void dispose(){
		ArrayList<FGPopupMenuItem> arrayToRemove = new ArrayList<FGPopupMenuItem>();
		
		for(int i=0; i<getComponentCount(); i++){
			Component comp = getComponent(i);
			if(comp != null && comp instanceof FGPopupMenuItem){
				FGPopupMenuItem popupMenuItem = (FGPopupMenuItem) comp;
				popupMenuItem.dispose();
				arrayToRemove.add(popupMenuItem);
				//remove(comp);
			}
		}
		
		for(int i=0; i<arrayToRemove.size(); i++){
			FGPopupMenuItem comp = arrayToRemove.get(i);
			remove(comp);
		}		
	}
	
}

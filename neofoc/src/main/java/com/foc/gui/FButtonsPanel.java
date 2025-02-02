/*
 * Created on 28-Mar-2005
 */
package com.foc.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;

import javax.swing.SwingUtilities;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FButtonsPanel extends FPanel {
  private int    x            = 0;
  private FPanel beforePanel  = null;
  private FPanel centralPanel = null;
  
  public FButtonsPanel(){
  	beforePanel  = new FPanel();
  	centralPanel = new FPanel();
  	setInsets(0, 0, 0, 0);
  	add(beforePanel, 0, 0);
  	add(centralPanel, 1, 0);
  }
  
  public void dispose(){
  	super.dispose();
  	beforePanel  = null;
 		centralPanel = null;
  }
  
  public void addButton_Before(FGButton button){
  	beforePanel.add(button, x++, 0, 1, 1, 0.1, 0.2, GridBagConstraints.EAST, GridBagConstraints.NONE);
  	readjustSize();
  }
  
  public void addComponent_Before(Component comp){
  	beforePanel.add(comp, x++, 0, 1, 1, 0.1, 0.2, GridBagConstraints.EAST, GridBagConstraints.NONE);
  	readjustSize();
  }
  
  public void addButton(FGButton button){
  	centralPanel.add(button, x++, 0, 1, 1, 0.1, 0.2, GridBagConstraints.EAST, GridBagConstraints.NONE);
  	readjustSize();
  }
  
  public void addComponent(Component comp){
  	centralPanel.add(comp, x++, 0, 1, 1, 0.1, 0.2, GridBagConstraints.EAST, GridBagConstraints.NONE);
  	readjustSize();
    //x++;
  }
  
  public void readjustSize(){
  	SwingUtilities.invokeLater(new Runnable(){
			public void run() {
		    java.awt.Dimension dim = getPreferredSize();
		    setMinimumSize(dim);
			}
  	});
  }
}

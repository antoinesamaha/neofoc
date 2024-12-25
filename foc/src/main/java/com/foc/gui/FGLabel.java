/*
 * Created on 14 fevr. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.foc.gui;

import javax.swing.Icon;
import javax.swing.JLabel;

import com.foc.Globals;

/**
 * @author Standard
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@SuppressWarnings("serial")
public class FGLabel extends JLabel {

	//private boolean showMandatorySymbol = false;
	
	public FGLabel(String label, boolean showMandatorySymbol){
		this();
		String text = label;
		if(showMandatorySymbol){
			text += "*";
		}
		setText(text);
	}
	
  /**
   * @param arg0
   * @param arg1
   * @param arg2
   */
  public FGLabel(String arg0, Icon arg1, int arg2) {
    super(arg0, arg1, arg2);
    setFont(Globals.getDisplayManager().getDefaultFont());
  }

  /**
   * @param arg0
   * @param arg1
   */
  public FGLabel(String arg0, int arg1) {
    super(arg0, arg1);
    setFont(Globals.getDisplayManager().getDefaultFont());
  }

  /**
   * @param arg0
   */
  public FGLabel(String arg0) {
    super(arg0);
    setFont(Globals.getDisplayManager().getDefaultFont());
  }

  /**
   * @param arg0
   * @param arg1
   */
  public FGLabel(Icon arg0, int arg1) {
    super(arg0, arg1);
    setFont(Globals.getDisplayManager().getDefaultFont());
  }

  /**
   * @param arg0
   */
  public FGLabel(Icon arg0) {
    super(arg0);
    setFont(Globals.getDisplayManager().getDefaultFont());
  }

  /**
   * 
   */
  public FGLabel() {
    super();
    setFont(Globals.getDisplayManager().getDefaultFont());
  }
  
  public void dispose(){
    
  }
}

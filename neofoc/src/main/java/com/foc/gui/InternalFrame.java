/*
 * Created on 22-May-2005
 */
package com.foc.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.foc.Globals;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class InternalFrame extends JInternalFrame{
  private FPanelSequence panelSequence = null;
  
  public InternalFrame(String title){
    super(title, true, false, true, true);
    
    /*
    FPanel panel = new FPanel();
    panel.setOpaque(false);
    panel.setFill(FPanel.FILL_BOTH);
    panel.add(new FGButton("Hello"), 0, 0, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
    panel.setBorder("Hi");
    panel.setVisible(true);
    setGlassPane(panel);
    panel.setVisible(true);
    getRootPane().
    */
    
    /*
    JPanel comp = (JPanel) getGlassPane();
    int debug = 3;
    ((JPanel)comp).setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    ((JPanel)comp).add(new FGButton("Hello"), c);
    debug++;
    ((JPanel)comp).repaint();
    ((JPanel)comp).setVisible(true);
    */
    
    //setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    
    /*
    addInternalFrameListener(new InternalFrameListener(){
      public void internalFrameActivated(InternalFrameEvent e) {
      }

      public void internalFrameClosed(InternalFrameEvent e) {
      }

      public void internalFrameClosing(InternalFrameEvent e) {
        b01.foc.Globals.logString("Closing event");
      }

      public void internalFrameDeactivated(InternalFrameEvent e) {
      }

      public void internalFrameDeiconified(InternalFrameEvent e) {
      }

      public void internalFrameIconified(InternalFrameEvent e) {
      }

      public void internalFrameOpened(InternalFrameEvent e) {
      }
    });
    */
    
    panelSequence = new FPanelSequence();
    
    //GridBagLayout layout = new GridBagLayout();
    //setLayout(layout);
    setContentPane(panelSequence.getCenterPanel());//AUTOSIZE
    //layout.setConstraints(contentPane, constr);
    
    addMouseListener(new MouseAdapter(){
     public void mouseClicked(MouseEvent e) {
    	 ((FDesktop)Globals.getDisplayManager().getNavigator()).setSelectedFrame(InternalFrame.this);    	 
     }

     public void mousePressed(MouseEvent e) {
    	 ((FDesktop)Globals.getDisplayManager().getNavigator()).setSelectedFrame(InternalFrame.this);
     }

     /*
			@Override
			public void mouseMoved(MouseEvent e) {
				//((FDesktop)Globals.getDisplayManager().getNavigator()).getDesktopManager().setBoundsForFrame(f, newX, newY, newWidth, newHeight)(InternalFrame.this);
				((FDesktop)Globals.getDisplayManager().getNavigator()).setSelectedFrame(InternalFrame.this);
			}
      */			
    });
  }
  
  public void dispose(){
  	if(panelSequence != null){
  		//panelSequence.dispose();
  	}
  	panelSequence = null;
  	super.dispose();
  }
  
  public FPanelSequence getPanelSequence() {
    return panelSequence;
  }


/*
	@Override
	public void paint(Graphics g) {
		g.fillOval(100, 0, 20, 20);
		super.paint(g);
		FGButton button = new FGButton("Hello");
	  g.translate(100, 100);
		button.paint(g);
		g.fillOval(100, 0, 20, 20);
	}
	
	*/
}

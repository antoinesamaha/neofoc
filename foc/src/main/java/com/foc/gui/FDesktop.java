/*
 * Created on 23-May-2005
 */
package com.foc.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.*;

import com.foc.*;
import com.foc.menu.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FDesktop extends JDesktopPane implements Navigator  {
	private JMenuBar menuBar = null;
  private MainFrame mainFrame = null;
  private Dimension viewPortDimension = null;
  private int nextX = 0;
  private int nextY = 0;
  private boolean isComputingInternalFramePanelSize = false;

  private boolean   isTheFirstPaint         = true;
  private Paint     barmajaLogoPaint        = null;
  private Paint     clientLogoPaint         = null;
  private Dimension barmajaLogoDimension    = null;
  private Dimension clientLogoDimension     = null;
  private Point     clientLogoTopLeft       = null;
    
  public FDesktop(MainFrame mainFrame){
  	
    try {
    	
      ClassLoader cl = ClassLoader.getSystemClassLoader();
      BufferedImage x = Globals.getIcons().getBackgroundImage();
    	
      if(x != null){
	      barmajaLogoDimension = new Dimension(x.getWidth(), x.getHeight());
	      barmajaLogoPaint = new TexturePaint(x, new Rectangle(0, 0, x.getWidth(), x.getHeight()));
	
	    	File clientLogoFile = new File(Globals.getIcons().getLoginPageLogo());
	    	/*if(clientLogoFile != null && clientLogoFile.getPath().compareTo("") != 0 && clientLogoFile.exists()){
	    		x = ImageIO.read(cl.getResource(clientLogoFile.getPath()));
	    	
	    		clientLogoDimension = new Dimension(x.getWidth(), x.getHeight());
	    		clientLogoTopLeft = Globals.getIcons().getTopLeftPointCenteredInBackground(clientLogoDimension);
		      
		      clientLogoPaint = new TexturePaint(x, new Rectangle(clientLogoTopLeft.x, clientLogoTopLeft.y, clientLogoDimension.width, clientLogoDimension.height));
	    	}*/
      }
	  } catch (Exception e) {
      e.printStackTrace();
	  }
  	  	
    this.mainFrame = mainFrame;
    mainFrame.setContentPane(this);
    
    mainFrame.initSize();
    /*
    mainFrame.setPreferredSize(new Dimension(ConfigInfo.getGuiNavigatorWidth(), ConfigInfo.getGuiNavigatorHeight()));
    mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
    */
    
    mainFrame.pack();
    viewPortDimension = new Dimension();
    
    /*
    setBackground(Globals.getDisplayManager().getBackgroundColor());
    panel = new FPanel();
    panel.add(new FGLabel("Hello"), 0, 0);
    panel.add(new FGLabel("Hello"), 0, 1);
    panel.add(new FGLabel("Hello"), 0, 2);
    panel.add(new FGLabel("Hello"), 0, 3);
    */
  }
  
  public void dispose(){
  	menuBar = null;
    mainFrame = null;
    viewPortDimension = null;
  }
  
/*  private void inPaintActions(Graphics g){
    if (barmajaLogoPaint != null && barmajaLogoDimension != null) {
      Graphics2D g2 = (Graphics2D) g;
      
      if(isTheFirstPaint){      	
      	isTheFirstPaint = false;
      	getViewportDimension();      	      
      }else{
        g2.setPaint(barmajaLogoPaint);      	
      	g2.fillRect( 0, 0, (int) barmajaLogoDimension.getWidth(), (int) barmajaLogoDimension.getHeight());
      	
      	if(clientLogoPaint != null && clientLogoDimension != null){
	        g2.setPaint(clientLogoPaint);
	        g2.fillRect(clientLogoTopLeft.x, clientLogoTopLeft.y, clientLogoDimension.width, clientLogoDimension.height);
	        //g2.fillRect(CLIENT_LOGO_MAX_TOP_LEFT.x, CLIENT_LOGO_MAX_TOP_LEFT.y, CLIENT_LOGO_MAX_DIMENSION.width, CLIENT_LOGO_MAX_DIMENSION.height);
      	}
      }
    }
  }*/
  
  private void inPaintActions(Graphics g){
    Graphics2D g2 = (Graphics2D) g;

    //This just colours the Desktop background  
    g2.setColor(Globals.getDisplayManager().getBackgroundColor());
    g2.fillRect(0, 0, 5000, 5000);
    
//    This used to put the 01001010100101000 background
//    BufferedImage backgroundImage = Globals.getIcons().getBackgroundImage();
//    if(backgroundImage != null){ 
//      g2.setPaint(barmajaLogoPaint);
//      g2.fillRect( 0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
//    }
    
    BufferedImage clientLogoImage = Globals.getIcons().getLoginPageImage();
//    if(clientLogoImage != null){
//      g2.setPaint(clientLogoPaint);
//      g2.fillRect( clientLogoTopLeft.x, clientLogoTopLeft.y, (int)clientLogoDimension.getWidth(), (int)clientLogoDimension.getHeight());
//    }
  }
  
  protected void paintComponent(Graphics g) {    
    super.paintComponent(g);
    inPaintActions(g);
        
//    WORKING SOLUTION BUT SLOW 
//    BufferedImage clientLogoImage = Globals.getIcons().getClientLogoImage();
//    if(clientLogoImage != null){
//    	Dimension dim = new Dimension(clientLogoImage.getWidth(), clientLogoImage.getHeight());
//    	Point pt = Globals.getIcons().getTopLeftPointCenteredInBackground(dim);
//    	g.drawImage(clientLogoImage, pt.x, pt.y, (int)dim.getWidth(), (int)dim.getHeight(), this);
//    }
  }

  public JMenuBar getMenuBar(){
  	return menuBar;
  }
  
  public Dimension getViewportDimension(){
    Dimension dim = mainFrame.getContentPane().getSize();
    if(dim != null && viewPortDimension != null){
	    viewPortDimension.width = dim.width;
	    viewPortDimension.height = dim.height;
    }
    boolean shouldGoBack = false;

    InternalFrame intFrame = (InternalFrame) getSelectedFrame();    
    if(intFrame == null){    
      if(!isComputingInternalFramePanelSize){
        isComputingInternalFramePanelSize = true;
        Globals.getDisplayManager().newInternalFrame(new FPanel());
        shouldGoBack = true;
        intFrame = (InternalFrame) getSelectedFrame();
        isComputingInternalFramePanelSize = false;
      }
    }
    if(intFrame != null){
      Dimension inFrameDim = intFrame.getSize();
      Dimension inFramePanelDim = intFrame.getContentPane().getSize();
      
      viewPortDimension.width -= inFrameDim.width - inFramePanelDim.width;  
      viewPortDimension.height -= inFrameDim.height - inFramePanelDim.height;

      if(shouldGoBack){
   			Globals.getDisplayManager().goBackDontCheckDialogs();	
      }
    }
    return viewPortDimension;
  }
  
  public synchronized JInternalFrame getSelectedFrame(){
  	JInternalFrame visibleWithFocusFrame = null;
    JInternalFrame anyVisibleIntFrame = null;
    visibleWithFocusFrame = super.getSelectedFrame();
  	if(visibleWithFocusFrame == null){
    	JInternalFrame[] intFrames = (JInternalFrame[]) getAllFrames();
    	for(int i=0; i<intFrames.length; i++){
    		JInternalFrame ifm = intFrames[i];
    		if(ifm != null && ifm.isVisible() && !ifm.isIcon()){
          if(ifm.hasFocus()){
            visibleWithFocusFrame = (InternalFrame)ifm;
            break;
          }else if(anyVisibleIntFrame == null){
            anyVisibleIntFrame = (InternalFrame)ifm;
          }
    		}
    	}
  	}
  	return visibleWithFocusFrame != null ? visibleWithFocusFrame : anyVisibleIntFrame ;
  }
  
  public FPanelSequence getActivePanelSequence(){
    InternalFrame intFrame = (InternalFrame) getSelectedFrame();
    return intFrame != null ? intFrame.getPanelSequence() : null;
  }
  
  public void packActiveFrame(){
    InternalFrame intFrame = (InternalFrame) getSelectedFrame();
    
    intFrame.pack();
    //mainFrame.pack();
  }

  private void setTitle(){
    FPanelSequence panelSeq = getActivePanelSequence();
    InternalFrame intFrame = (InternalFrame) getSelectedFrame();
    FPanel panel = (FPanel) panelSeq.getMainPanel();
    intFrame.setTitle(panel.getFrameTitle());
  }  
  
  public void changeView(FPanel panel) {
    FPanelSequence panelSeq = getActivePanelSequence();
    panelSeq.changePanel(panel);
    setTitle();        
    packActiveFrame();
  }
  
  public synchronized boolean goBack() {
    FPanelSequence panelSeq = getActivePanelSequence();
    boolean exit = panelSeq.goBack(true);
    
    if(exit){
      InternalFrame intFrame = (InternalFrame) getSelectedFrame();

      intFrame.setVisible(false);
      intFrame.dispose();
      
      remove(intFrame);
    }else{
      setTitle();
      packActiveFrame();
    }
    return false;
  }
  
  public synchronized InternalFrame newView(FPanel panel) {
    InternalFrame intFrm = new InternalFrame(panel.getFrameTitle());
    intFrm.setName(panel.getFrameTitle()+".INTFRAME");
    if(ConfigInfo.isUnitDevMode()){
    	StaticComponent.setComponentToolTipText(intFrm, panel.getFrameTitle()+".INTFRAME");
    }
    add(intFrm);

    intFrm.reshape(nextX, nextY, 10, 10);
    /*
    nextX += 20;
    nextY += 20;
    if(nextX == 5 * 20) nextX = 0;
    if(nextY == 5 * 20) nextY = 0;
    */
    
    FPanelSequence panelSeq = intFrm.getPanelSequence();
    panelSeq.setMainPanel(panel);
    intFrm.pack();
    intFrm.setVisible(true);
    return intFrm;
  }

  public MenuConstructor getMainMenuConstructor(){
    menuBar = new JMenuBar();
    //menuBar.setBorderPainted(true);
    //menuBar.add(new JMenu("dsds"));
    //mainFrame.setJMenuBar(menuBar);
    MenuBarConstructor mbc = new MenuBarConstructor(mainFrame, menuBar);
    return mbc;
  }
}

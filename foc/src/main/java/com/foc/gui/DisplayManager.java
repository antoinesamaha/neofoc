/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
// FOCUS LOCK
// CURRENT EDIT

/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;

import com.foc.*;
import com.foc.admin.*;
import com.foc.gui.lock.*;
import com.foc.list.FocList;
import com.foc.menu.*;

/**
 * @author 01Barmaja
 */
public class DisplayManager {
	
	public static final int NBR_OF_CLICKS = 1;
	
  private Color backgroundColor = null;  
  
  //Main frame est toujours la. desktop est null ou pas selon si on est en MDI ou pas
  private MainFrame mainFrame = null;
  private Navigator navigator = null;
  private int guiNavigatorType = GUI_NAVIGATOR_MDI;
  private ArrayList<FDialog> activeDialogList = null;

  private FocusLock focusLock = null;
  //private FocusLock currentEdit = null;
  private Font    defaultFont                    = null;
  private Font    defaultNotEditableFont         = null;
  @SuppressWarnings("unused")
	private Font    defaultOutputFont              = null;
  private Color   defaultEditableForeground      = new Color(228, 109, 10);
  private Color   defaultEditableBackground      = Color.WHITE;
  private Color   defaultNotEditableForeground   = new Color(180, 110, 60);//new Color(228, 109, 10);Roula
  private Color   disabledFieldBackground        = new Color(235, 235, 255);
  private Color   disabledTextColor              = Color.BLACK;
  private boolean comboBoxesWithAutoCompletion   = true;
  private Color   columnTitleBackground          = new Color(124, 165, 205);//new Color(245, 255, 179);//Yellow
  private Color   columnTitleForeground          = Color.WHITE;
  private Color   barmajaOrange                  = new Color(246, 206, 131);//new Color(165, 70, 60);
  private Color   barmajaOrangeDark              = new Color(230, 180, 120);//new Color(165, 70, 60);
  private static String FONT_NAME                = "Arial";//"SansSerif";//"Calibri";
  private int CHAR_WIDTH  = 0;
  private int CHAR_HEIGHT = 0;
  
  private int    cursorComputingCounter = 0;
  private Cursor hourglassCursor        = new Cursor(Cursor.WAIT_CURSOR);
  private Cursor normalCursor           = new Cursor(Cursor.DEFAULT_CURSOR);
  
  //Panel Background 193 200 209
 
  public static final int GUI_NAVIGATOR_NONE      = 0;
  public static final int GUI_NAVIGATOR_MDI       = 1;
  public static final int GUI_NAVIGATOR_MONOFRAME = 2;
  
  public static final Color TABLE_HEADER_BORDER_COLOR = Color.BLACK;
  
  private int lookAndFeel = LOOK_AND_FEEL_NIMBUS;
  
  public static final int LOOK_AND_FEEL_METAL  = 0;
  public static final int LOOK_AND_FEEL_NIMBUS = 1;
  
  public DisplayManager(int guiNavigatorType) {
    this.guiNavigatorType = guiNavigatorType;
    /*
    //backgroundColor = new Color(124, 165, 205);//bleu fonce
     */
    
    backgroundColor = new Color(160, 180, 240);//bleu fonce
    //backgroundColor = new Color(70, 70, 83);//New Blue of logo from Roula
    //backgroundColor = new Color(114, 124, 163);//New Blue of site from Roula 
    
    /*
    //backgroundColor = new Color(141, 179, 255);//Bleu Barmaja
    
    //UIManager.put("Button.disabledText", Color.black);
    
    //backgroundColor = new Color(192, 194, 208);//Gris
    //backgroundColor = new Color(61, 67, 132);//Blue Calm
    //backgroundColor = new Color(77, 102, 167);//Blue water
    //backgroundColor = new Color(187, 180, 131);//Sable
    //backgroundColor = new Color(236, 233, 216);//Windows - Cream
     */
  }
  
  public void dispose(){
    if( mainFrame != null ){
      mainFrame.dispose();
      mainFrame = null;
    }
    
    if(navigator != null ){
      navigator = null;
    }

    if( activeDialogList != null ){
      activeDialogList.clear();
      activeDialogList = null;  
    }
  }

  public void init() {
    try{

    	switch(lookAndFeel){
	    	case LOOK_AND_FEEL_METAL:
	    	{
	    		FONT_NAME = "SansSerif";
	    	  UIManager.put("TitledBorder.font"          , defaultFont);
	        UIManager.put("TabbedPane.font"            , defaultFont);
	
	        UIManager.put("TabbedPane.selected"        , backgroundColor);
	        UIManager.put("TabbedPane.contentAreaColor", backgroundColor);
	
	        UIManager.put("TabbedPane.focus"           , backgroundColor);//Small square arround the selected tabb title
	
	        UIManager.put("TabbedPane.darkShadow"      , backgroundColor);
	        
	        UIManager.put("CheckBox.disabledText"      , defaultNotEditableForeground);
	        UIManager.put("ComboBox.disabledBackground", disabledFieldBackground);
	        UIManager.put("ComboBox.disabledForeground", defaultNotEditableForeground);
	
	        BorderUIResource.LineBorderUIResource lineBorder = new BorderUIResource.LineBorderUIResource(barmajaOrange);
	        UIManager.put("Table.focusCellHighlightBorder", lineBorder);
	    	}
	    	break;
	    	case LOOK_AND_FEEL_NIMBUS:
	    	{
	    		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	    		
	    		//columnTitleForeground = Color.BLACK;
	    		UIManager.put("TextArea.background", columnTitleBackground);
	    		UIManager.put("TextPane.background", columnTitleBackground);
	    		UIManager.put("Menu.font", defaultFont);
	    		
	    		
	    		FONT_NAME = "Arial";
	    	  UIManager.put("TitledBorder.font", defaultFont);
	        UIManager.put("TabbedPane.font", defaultFont);
	
	        UIManager.put("TabbedPane.selected", backgroundColor);
	        UIManager.put("TabbedPane.contentAreaColor", backgroundColor);
	
	        UIManager.put("TabbedPane.focus", backgroundColor);//Small square arround the selected tabb title
	
	        UIManager.put("TabbedPane.darkShadow", backgroundColor);
	        
	        UIManager.put("CheckBox.disabledText", defaultNotEditableForeground);
	        UIManager.put("ComboBox.disabledBackground", disabledFieldBackground);
	        UIManager.put("ComboBox.disabledForeground", defaultNotEditableForeground);
	
	        BorderUIResource.LineBorderUIResource lineBorder = new BorderUIResource.LineBorderUIResource(barmajaOrange);
	        UIManager.put("Table.focusCellHighlightBorder", lineBorder);
	    	}
	    	break;
    	}
    	
    	//javax.swing.plaf.metal.MetalLookAndFeel

    	/*    	
    	GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	String envfonts[] = gEnv.getAvailableFontFamilyNames();
    	for(int i = 1; i < envfonts.length; i++){
    		Globals.logString("Font "+i+" = "+envfonts[i]);
    	}

      UIManager.LookAndFeelInfo lfi[] = UIManager.getInstalledLookAndFeels();
      for(int i =0; i<lfi.length; i++){
        Globals.logString("Look and feel: "+lfi[i].getName()+" ; "+lfi[i].getClassName());
      }
      
					Look and feel: Metal ; javax.swing.plaf.metal.MetalLookAndFeel
					Look and feel: CDE/Motif ; com.sun.java.swing.plaf.motif.MotifLookAndFeel
					Look and feel: Windows ; com.sun.java.swing.plaf.windows.WindowsLookAndFeel
					Look and feel: Windows Classic ; com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel
      */
      
      //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel());
      //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.motif.MotifLookAndFeel());
      //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
      //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel());      
    }catch (Exception e){ 
      Globals.logException(e);
    }

    mainFrame = new MainFrame();
    
    mainFrame.pack();
    mainFrame.setVisible(true);

    defaultFont            = new Font(FONT_NAME, Font.BOLD, ConfigInfo.getFontSize());
    defaultNotEditableFont = new Font(FONT_NAME, Font.BOLD, ConfigInfo.getFontSize());//new Font(FONT_NAME, Font.PLAIN, ConfigInfo.getFontSize());
    defaultOutputFont      = new Font(FONT_NAME, Font.BOLD+Font.ITALIC, ConfigInfo.getFontSize());//font.deriveFont(Font.BOLD+Font.ITALIC, ConfigInfo.getFontSize());
    defaultEditableForeground = mainFrame.getForeground();
    
    FontMetrics metrics = mainFrame.getFontMetrics(defaultFont);
    //int maxWidth = 0;
    //int[] wds = metrics.getWidths();
    //for(int i=0; i<wds.length; i++){
    //  maxWidth = Math.max(wds[i], maxWidth);
    //}
    CHAR_WIDTH = metrics.getWidths()[0];
    CHAR_HEIGHT = metrics.getAscent() ;//+ metrics.getDescent();
    //Globals.logString("Ascent = "+metrics.getAscent()+" Descent = "+metrics.getDescent()+" Leading = "+metrics.getLeading()+" Height = "+metrics.getHeight());

    /*
    UIDefaults def = UIManager.getDefaults();
    Enumeration iter = def.keys();
    Globals.logString("Begin");
    while(iter != null && iter.hasMoreElements()){
    	Object str = iter.nextElement();
    	//if(str.toString().startsWith("SplitPane.")){
    		Globals.logString(str.toString()+"="+UIManager.get(str));
    	//}
    }
    Globals.logString("End");
    */
    
    /*
    //SplitPane.ancestorInputMap=javax.swing.plaf.InputMapUIResource@16930e2
    //SplitPane.dividerFocusColor=javax.swing.plaf.ColorUIResource[r=200,g=221,b=242]
    UIManager.put("SplitPane.highlight", backgroundColor);
    //SplitPane.oneTouchButtonsOpaque=false
    UIManager.put("SplitPane.border", null);
    UIManager.put("SplitPane.shadow", backgroundColor);
    UIManager.put("SplitPane.centerOneTouchButtons", true);
    UIManager.put("SplitPane.darkShadow", backgroundColor);
    UIManager.put("SplitPane.background", backgroundColor);
    */

    /*
    UIManager.put("TitledBorder.font", defaultFont);
    UIManager.put("TabbedPane.font", defaultFont);
    //UIManager.put("TabbedPane.unselectedBackground", columnTitleBackground);
    UIManager.put("TabbedPane.selected", backgroundColor);
    UIManager.put("TabbedPane.contentAreaColor", backgroundColor);
    //UIManager.put("TabbedPane.light", backgroundColor);
    UIManager.put("TabbedPane.focus", backgroundColor);//Small square arround the selected tabb title
    //UIManager.put("TabbedPane.tabAreaBackground", backgroundColor);
    //UIManager.put("TabbedPane.shadow", backgroundColor);
    UIManager.put("TabbedPane.darkShadow", backgroundColor);
    //UIManager.put("TabbedPane.borderHightlightColor", backgroundColor);
    //UIManager.put("TabbedPane.selectHighlight", backgroundColor);
    //UIManager.put("TabbedPane.highlight", backgroundColor);
    
    UIManager.put("CheckBox.disabledText", defaultNotEditableForeground);
    UIManager.put("ComboBox.disabledBackground", disabledFieldBackground);
    UIManager.put("ComboBox.disabledForeground", defaultNotEditableForeground);

    BorderUIResource.LineBorderUIResource lineBorder = new BorderUIResource.LineBorderUIResource(barmajaOrange);
    UIManager.put("Table.focusCellHighlightBorder", lineBorder);
    */
    
    //UIManager.put("ComboBox.disabledForeground", disabledTextColor);
    //UIManager.put("InternalFrame.titlePaneHeight", Integer.valueOf(20));
    //UIManager.put("InternalFrame.titleButtonHeight", Integer.valueOf(20));
    //UIManager.put("InternalFrame.titleButtonWidth", Integer.valueOf(20));
    
    //Font tempFont = (Font) UIManager.get("InternalFrame.titleFont");
    //tempFont = tempFont.deriveFont(14);
    //UIManager.put("InternalFrame.titleFont", tempFont);
    //UIManager.put("InternalFrame.borderWidth", Integer.valueOf(0));
    
    activeDialogList = new ArrayList<FDialog>();
  }

  public void setMouseComputing(boolean computing){
  	if(getMainFrame() != null){
	  	if(computing){
	  		cursorComputingCounter++;
	  	}else{
	  		cursorComputingCounter--;
	  	}
	  	
	  	if(cursorComputingCounter == 1){
	  	  getMainFrame().setCursor(hourglassCursor);
	  	}
	  	if(cursorComputingCounter <= 0){
			  getMainFrame().setCursor(normalCursor);
	  	}
	  	if(cursorComputingCounter < 0){
	  		cursorComputingCounter = 0;
	  	}
  	}
  }
  
  public void pack(){
  	DisplayManager dispManager = Globals.getDisplayManager();
	  if(dispManager != null){
	  	Navigator navigator = Globals.getDisplayManager().getNavigator();
	  	if(navigator != null && navigator instanceof FDesktop){
	  		FDesktop desktop = (FDesktop) navigator;
	  		if(desktop.getSelectedFrame() != null){
	  			desktop.getSelectedFrame().pack();
	  		}
		  }
	  }
  }
  
  public void startMonoNavigator(){
    navigator = new MonoFrame(mainFrame);
  }
  
  public void setDefaultFontSize(int size){
    if(size != 0){
      if(defaultFont != null){
        defaultFont = defaultFont.deriveFont((float)size);
      }
      if(defaultNotEditableFont != null){
        defaultNotEditableFont = defaultNotEditableFont.deriveFont((float)size);
      }
      fontSizeHasChanged();
    }
  }

  public void setDefaultFont(String fontName, int type, int size){
    if(size != 0){
      if(defaultFont != null){
        defaultFont = new Font(fontName, type, size);
      }
      /*if(defaultNotEditableFont != null){
        defaultNotEditableFont = defaultNotEditableFont.deriveFont((float)size);
      }*/
      fontSizeHasChanged();
    }
  }
  
  public void fontSizeHasChanged(){
    FontMetrics metrics = mainFrame.getFontMetrics(defaultFont);
    //int maxWidth = 0;
    //int[] wds = metrics.getWidths();
    //for(int i=0; i<wds.length; i++){
    //  maxWidth = Math.max(wds[i], maxWidth);
    //}
    CHAR_WIDTH = metrics.getWidths()[0];
    CHAR_HEIGHT = metrics.getAscent() ;//+ metrics.getDescent();
    
    
   	switch(lookAndFeel){
	  	case LOOK_AND_FEEL_METAL:
	  	{
	      UIManager.put("TabbedPane.font", defaultFont);
	      UIManager.put("CheckBox.disabledText", Color.black);
	      UIManager.put("ComboBox.disabledBackground", disabledFieldBackground);
	      UIManager.put("ComboBox.disabledForeground", disabledTextColor);    
	      UIManager.put("Table.font", defaultFont);
	  	}
	  	break;
	  	case LOOK_AND_FEEL_NIMBUS:
	  	{
	      UIManager.put("TabbedPane.font", defaultFont);
	      UIManager.put("CheckBox.disabledText", Color.black);
	      UIManager.put("ComboBox.disabledBackground", disabledFieldBackground);
	      UIManager.put("ComboBox.disabledForeground", disabledTextColor);    
	      UIManager.put("Table.font", defaultFont);
	  	}
	  	break;
   	}
    
    getMainFrame().setFont(defaultFont);
  }
  
  public int getCharWidth(){
    return CHAR_WIDTH;
  }

  public int getCharHeight(){
    return CHAR_HEIGHT;
  }

  public FDialog getActiveDialog(){
    FDialog dialog = null;
    if(activeDialogList != null && activeDialogList.size() > 0){
      dialog = (FDialog) activeDialogList.get(activeDialogList.size()-1);
    }
    return dialog;
  }
  
  private void pushActiveDialog(FDialog dialog){
    activeDialogList.add(dialog);
  }

  public boolean closeDialog(FDialog dialog){
  	boolean closed = false;
  	if(dialog != null){
      dialog.setVisible(false);
      activeDialogList.remove(dialog);
      dialog.dispose();
      closed = true;
    }
  	return closed;
  }
  
  private boolean closeActiveDialog(){
    FDialog dialog = getActiveDialog();
    return closeDialog(dialog);
  }
  
  public void displayLogin(){
    FPanel loginPanel = FocUser.getLoginPanel();
    if (loginPanel != null) {
      navigator.changeView(loginPanel);      
    }
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * @return Returns the mainFrame.
   */
  public MainFrame getMainFrame() {
    return mainFrame;
  }
  
  public void refresh(){
    FDialog dialog = getActiveDialog();
    if(dialog == null){
      FPanelSequence panelSeq = navigator.getActivePanelSequence();
      if(panelSeq != null){
        JPanel mainPanel = panelSeq.getMainPanel();
        if(mainPanel != null) mainPanel.repaint();
      }
    }else{
      dialog.repaint();
    }
  }
  
  public void violentRefresh() {
    FDialog dialog = getActiveDialog();
    if(dialog == null){
      FPanelSequence panelSeq = navigator.getActivePanelSequence();
      if(panelSeq != null){
        panelSeq.violentRefresh();
      }
    }else{
      dialog.repaint();
    }
  }

  public FPanel getCurrentPanel() {
    FPanelSequence panelSeq = navigator.getActivePanelSequence();
    return panelSeq != null ? (FPanel)panelSeq.getMainPanel() : null;
  }

  public void changePanel(JPanel jNewPanel) {
    navigator.changeView((FPanel)jNewPanel);
  }
  
  public InternalFrame newInternalFrame(FPanel jNewPanel){
    return navigator != null ? navigator.newView((FPanel)jNewPanel) : null;
  }

  public void popupDialog(FDialog fDialog){
  	popupDialog(fDialog, true);
  }
  
  public void popupDialog(FDialog fDialog, boolean visible){
    if(fDialog.isModal()){
      pushActiveDialog(fDialog);
    }
    fDialog.pack();
    fDialog.setVisible(visible);
  }

  public FDialog popupDialog(FPanel panel, String title, boolean modal){
    return popupDialog(panel, title, modal, 0, 0);
  	/*InternalFrame intFrame = newInternalFrame(panel);
  	intFrame.setTitle(title);
  	intFrame.set*/
  }
  
  public FDialog popupDialog(FPanel panel, String title, boolean modal, int x, int y){
  	return popupDialog(panel, title, modal, x, y, true);
  }
  
  public FDialog popupDialog(FPanel panel, String title, boolean modal, int x, int y, boolean visible){
  	return popupDialog(panel, title, modal, x, y, visible, false);
  }
  
  public FDialog popupDialog(FPanel panel, String title, boolean modal, int x, int y, boolean visible, boolean undecorated){
    FDialog fDialog = new FDialog(mainFrame, title, modal, undecorated);
    fDialog.setLocation(x, y);
    
  /*FPanelSequence panelSeq = fDialog.getPanelSequence();
    panelSeq.setMainPanel(panel);
    panelSeq.violentRefresh();*/
     
    fDialog.setPanel(panel);
    
    fDialog.pack();
    
    boolean modified = false;
    Dimension realDim = fDialog.getPreferredSize();  
    if(panel.getMainPanelSising() == FPanel.MAIN_PANEL_FILL_VERTICAL){
      //realDim.height = ConfigInfo.getGuiNavigatorHeight();
      realDim.height = mainFrame.getHeight();
      modified = true;
    }
    if(panel.getMainPanelSising() == FPanel.MAIN_PANEL_FILL_HORIZONTAL){
      //realDim.width = ConfigInfo.getGuiNavigatorWidth();
      realDim.width = mainFrame.getWidth();
      modified = true;
    }
    if(panel.getMainPanelSising() == FPanel.MAIN_PANEL_FILL_BOTH){
      //realDim.height = ConfigInfo.getGuiNavigatorHeight();
      //realDim.width = ConfigInfo.getGuiNavigatorWidth();
      realDim.width = mainFrame.getWidth();
      realDim.height = mainFrame.getHeight();
      modified = true;
    }
    if(modified){
      fDialog.setPreferredSize(realDim);
      //panelSeq.getMainPanel().setPreferredSize(realDim);
    }
    
    popupDialog(fDialog, visible);
    return fDialog;
  }

  public void popupMessage(String message){
  	Globals.logString("POPUP Message : "+message);
    JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
        message,
        Globals.getApp().isShowBarmajaIconAndTitle() ? "01Barmaja" : "",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null);
  }
  
  public FDialog popupMessage_WithoutButtons(String title, String message){
  	final FPanel panel = new FPanel();
  	panel.add(new FGLabel(message), 0, 0);
  	Dimension pSize = panel.getPreferredSize();
  	pSize = new Dimension((int)pSize.getWidth(), (int)pSize.getHeight());
  	if(pSize.getWidth()  < 400) pSize.width  = 400;
  	if(pSize.getHeight() < 200) pSize.height = 200;
  	panel.setPreferredSize(pSize);
  	Dimension dim = getMainFrame().getSize();
  	
  	final FDialog dialog = popupDialog(panel, title, false, (int) (dim.getWidth() / 2 - 200), (int) (dim.getHeight() / 2 - 100));
  	/*
  	panel.repaint();
  	dialog.repaint();
		dialog.getPanelSequence().violentRefresh();
		
  	SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
		  	panel.repaint();
				dialog.repaint();
				dialog.getPanelSequence().violentRefresh();
			}
  	});
  	
  	try{
			Thread.sleep(1000);
		}catch (InterruptedException e){
			e.printStackTrace();
		}
		*/

  	return dialog;
  }
  
  public void goBack() {
    if(!closeActiveDialog()){
      goBackDontCheckDialogs();
    }
  }
  
  public void goBackDontCheckDialogs() {
    if(navigator.goBack()){
      Globals.getApp().exit();
    }
  }

  public void createNavigatorIfNeeded(){
    if(navigator == null){
    	switch(guiNavigatorType){
    	case GUI_NAVIGATOR_MDI:
        navigator = new FDesktop(mainFrame);
    		break;
    	case GUI_NAVIGATOR_MONOFRAME:
        navigator = new MonoFrame(mainFrame);
    		break;
    	}
    }
  }
  
  public void reconstructMenu(int loginStatus){
    MenuConstructor mc = null;
    
    createNavigatorIfNeeded();
    
    mc = navigator.getMainMenuConstructor();
    
    if(mc != null){
      if(loginStatus == Application.LOGIN_ADMIN){
        FMenu adminMenu = Globals.getApp().getMainAdminMenu();
        mc.addMainMenu(adminMenu);
      }else{
        FMenu appMenu = Globals.getApp().getMainAppMenu();
        if(Globals.getApp().isWithLogin()){
	        
        	FocGroup group = Globals.getApp().getGroup();
	        FocList list       = group.getMenuRightsList();
	        FocList globalList = MenuRightsDesc.getGlobalMenuRightsList(FocList.LOAD_IF_NEEDED);
	        
	        applyMenuRightsList(globalList);
	        applyMenuRightsList(list);
        }
        mc.addMainMenu(appMenu);        
      }
  
      FMenu focMenu = Globals.getApp().getMainFocMenu();
      mc.addMainMenu(focMenu);
      mc.showMenu();
    }      
    mainFrame.setPreferredSize(new Dimension(750, 580));
    mainFrame.repaint();
  }
  
  private void applyMenuRightsList(FocList list){
  	FMenu appMenu = Globals.getApp().getMainAppMenu();  	
    for(int i=0; i<list.size(); i++){
    	MenuRights menuRights       = (MenuRights) list.getFocObject(i);
    	
    	boolean enable = menuRights.getRight() != MenuRightsDesc.ALLOW_HIDE;
    	if(!enable){
    		FMenu menuFound = appMenu.findMenuForCode(menuRights.getCode());
    		if(menuFound != null){
    			menuFound.setEnabled(false);
    		}
    	}else{
    		String customTitle = menuRights.getCustomTitle();
    		if(!customTitle.isEmpty()){
      		FMenu menuFound = appMenu.findMenuForCode(menuRights.getCode());
      		if(menuFound != null){
      			menuFound.setTitle(customTitle);
      		}
      	}
    	}
    }  	
  }
  
  public void popupMenu(int loginStatus){
    navigator = null;
    createNavigatorIfNeeded();
    if(!Globals.getApp().isDisableMenus()){
      reconstructMenu(loginStatus);
    }
  }
  
  public Navigator getNavigator() {
    return navigator;
  }
  
  public boolean restoreInternalFrame(InternalFrame intFrame){
    boolean successfull = false;
    
    if(intFrame != null){
      FDesktop desk = (FDesktop)Globals.getDisplayManager().getNavigator();
      JInternalFrame ifs[] = (JInternalFrame [])desk.getAllFrames();
      for(int i=0; i<ifs.length; i++){
        InternalFrame ifrm = (InternalFrame)ifs[i];
        if(ifrm == intFrame){
          try{
            ifrm.setIcon(false);
            ifrm.setSelected(true);
            ifrm.setVisible(false);
            ifrm.setVisible(true);
            successfull = true;
          }catch(Exception e){
            Globals.logException(e);
          }
        }
      }
    }
    return successfull;
  }

  public boolean allScreensClosed(){
    boolean allScreensClosed = false;
    if(guiNavigatorType != GUI_NAVIGATOR_NONE){
	    if(guiNavigatorType == GUI_NAVIGATOR_MDI && Globals.getDisplayManager().getNavigator() instanceof FDesktop){
	      FDesktop desk = (FDesktop)Globals.getDisplayManager().getNavigator();
	      JInternalFrame ifs[] = (JInternalFrame [])desk.getAllFrames();
	      allScreensClosed = ifs.length == 0; 
	    }else{
	      MonoFrame monoFrame = (MonoFrame)Globals.getDisplayManager().getNavigator();
	      FPanelSequence panelSequence = monoFrame.getActivePanelSequence();
	      allScreensClosed = panelSequence.getPanelDeepness() == 0;
	    }
    }
    return allScreensClosed;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // FOCUS LOCK
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public void setFocusLock(FocusLock focusLock) {
    this.focusLock = focusLock;
  }
  
  private boolean shouldLockFocusInternal(boolean displayMessage){
    boolean lock = false;
    if(focusLock != null){
      lock = focusLock.shouldHoldFocus(displayMessage);
    }
    return lock ; 
  }
  
  public boolean shouldLockFocus(boolean displayMessage){
    boolean lock = false;
    //ATTENTION
    
    if(Globals.getDisplayManager().shouldLockFocusInternal(false)){
      Globals.getDisplayManager().stopEditingLockFocus();
    }
   

    lock = Globals.getDisplayManager().shouldLockFocusInternal(displayMessage);
    
    if(!lock){
    	//Globals.getDisplayManager().removeLockFocusForObject(null);
    }
    return lock ; 
  }

  public void removeLockFocusForObject(Object obj){
    if(focusLock != null){
      if(focusLock.getLockObject() == obj || obj == null){
        focusLock.dispose();
        setFocusLock(null);
      }
    }
  }
  
  public void stopEditingLockFocus(){
    if(focusLock != null){
      focusLock.stopEditing();
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // CURRENT EDIT
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  //public FocusLock getCurrentEdit() {
  //  return currentEdit;
  //}
  /*
  public void setCurrentEdit(FocusLock currentEdit) {
    this.currentEdit = currentEdit;
  }
  
  public void stopEditingCurrentEdit(){
    if(currentEdit != null){
      currentEdit.stopEditing();
    }
  }
  */
  
  public Font getDefaultFont() {
    return defaultFont;
  }

  public Font getDefaultNotEditableFont() {
    return defaultNotEditableFont;
  }

  public Font getDefaultOutputFont() {
    return defaultNotEditableFont;
  }

  public Color getNotEditableForeground() {
  	return defaultNotEditableForeground;
  }

  public Color getEditableForeground() {
  	return defaultEditableForeground;
  }

  public void setEditableForeground(Color c) {
  	defaultEditableForeground = c;
  }

  public Color getEditableBackground() {
  	return defaultEditableBackground;
  }

  public void setEditableBackground(Color c) {
  	defaultEditableBackground = c;
  }

  /**
   * @return
   */
  public Color getDisabledTextColor() {
    return disabledTextColor;
  }
  
  /**
   * @return
   */
  public Color getDisabledFieldBackground() {
    return disabledFieldBackground;
  }
  
  public void setComboBoxesWithAutoCompletion(boolean withAutoCompletion){
  	this.comboBoxesWithAutoCompletion = withAutoCompletion;
  }
  
  public boolean isComboBoxesWithAutoCompletion(){
  	return this.comboBoxesWithAutoCompletion;
  }

	public Color getColumnTitleBackground() {
		return columnTitleBackground;
	}
	
	public Color getColumnTitleForeground(){
		return columnTitleForeground;
	}
	
	public Color getBarmajaOrange(){
		return barmajaOrange;
	}

	public Color getBarmajaOrangeDark(){
		return barmajaOrangeDark;
	}

	public int getLookAndFeel(){
		return lookAndFeel;
	}
}

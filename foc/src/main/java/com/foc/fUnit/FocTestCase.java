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
// GET SET
// ADMIN
// MENU
// FIND CHILD
// GUI COMPONENT SET VALUE
// INTERNAL FRAME
// TREE
// TABLE
// Drag And Drop
// TABBED PANE
// BUTTON
// LOGIN - EXIT
// ASSERT
// LOG
// KEYBOARD
// MOUSE
// FORMULA

package com.foc.fUnit;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.foc.Globals;
import com.foc.IExitListener;
import com.foc.admin.FocUser;
import com.foc.business.status.StatusFieldPanel;
import com.foc.db.lock.LockManager;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.formula.AbstractFormulaEnvironment;
import com.foc.gui.DisplayManager;
import com.foc.gui.FDesktop;
import com.foc.gui.FDialog;
import com.foc.gui.FGButton;
import com.foc.gui.FGCheckBox;
import com.foc.gui.FGComboBox;
import com.foc.gui.FGDateChooser;
import com.foc.gui.FGDateField;
import com.foc.gui.FGNumField;
import com.foc.gui.FGObjectComboBox;
import com.foc.gui.FGTextField;
import com.foc.gui.FGTimeField;
import com.foc.gui.FListPanel;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.InternalFrame;
import com.foc.gui.Navigator;
import com.foc.gui.table.FAbstractTableModel;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableModel;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.property.FProperty;
import com.foc.tree.FNode;
import com.foc.tree.FTree;

import junit.framework.TestCase;

public class FocTestCase extends TestCase implements IExitListener {
  private String       userName              = null ;
  private boolean      endOfTest             = false;
  private Container    defaultRootContainer  = null ;
  private FocDesc      defaultFocDesc        = null ;
  private FocTestSuite focTestSuite          = null ;
  private Class        klass                 = null ;
  private boolean      failure               = true ;
  private Robot        robot                 = null ;

  private static boolean  debug_GetChildName = false;
  
  public static final int SLEEP_SCALE                = 400;
  public static final int DEFAULT_NUMBER_OF_ATTEMPTS = 20;
  
  public FocTestCase(String functionName) {
    super(functionName);
  }
  
  public FocTestCase() {
  }
  
  public FocTestCase(FocTestSuite testSuite, String functionName) {
    super(functionName);
    this.focTestSuite = testSuite;
  }
  
  public FocTestCase(FocTestSuite testSuite, String name, String userName, Class klass) {
    this(testSuite, name);
    this.userName = userName;
    this.klass = klass;
  }

  public static void setDebug_GetChildName(boolean debug){
  	debug_GetChildName = debug;
  }

  public static boolean isDebug_GetChildName(){
  	return debug_GetChildName;
  }
  
  public Robot getRobot(){
  	if(robot == null){
	    try{
	    	robot = new Robot();
	    }catch(Exception e){
	    	Globals.logException(e);
	    }
  	}
    return robot;
  }
  
  public void sleep(double val) {
    try {
      Thread.sleep((long) (val * SLEEP_SCALE));
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
  
  public FocTestSuite getFocTestSuite() {
    return focTestSuite;
  }
  
  protected void setUp() throws Exception {
    logBeginTest(getName());
    logSuiteName(getFocTestSuite().getName());
  }
  
  protected void tearDown() throws Exception {
    logStatus(!failure);
    logEndTest(getName());
  }
  
  // #######################################
  // #######################################
  // GET SET
  // #######################################
  // #######################################
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  public String getUserName() {
    return this.userName;
  }
  
  public Container getDefaultRootContainer() {
    if (defaultRootContainer == null) {
      defaultRootContainer = getMainFrame();
    }
    return defaultRootContainer;
  }
  
  public void setDefaultRootContainer(Container container) {
    this.defaultRootContainer = container;
  }
  
  public FocDesc getDefaultFocDesc() {
    return defaultFocDesc;
  }
  
  public void setDefaultFocDesc(FocDesc defaultFocDesc) {
    this.defaultFocDesc = defaultFocDesc;
  }
  
  public JFrame getMainFrame() {
    DisplayManager display = null;
    JFrame mainFrame = null;
    while (mainFrame == null) {
      display = com.foc.Globals.getDisplayManager();
      if (display != null) {
        mainFrame = display.getMainFrame();
      }
      if (mainFrame == null) {
        sleep(0.5);
      }
    }
    return mainFrame;
  }

  public FDialog getActiveDialog() {
    FDialog mainFrame = null;
    DisplayManager display = com.foc.Globals.getDisplayManager();
    if(display != null){
    	mainFrame = display.getActiveDialog();
    }
    if (mainFrame == null) {
      sleep(0.5);
    }
    return mainFrame;
  }

  // #######################################
  // #######################################
  // ADMIN
  // #######################################
  // #######################################

  public void unlockAllLockableObjects(){
    LockManager lockManager = new LockManager();
    lockManager.unlockAllObjectsForAllDescriptions();
    lockManager.dispose();
    //lockManager = null;
  }
  
  // #######################################
  // #######################################
  // MENU
  // #######################################
  // #######################################
  
  public JMenuBar getMenuBar(int nbrOfAttempts) {
    JMenuBar menuBar = null;
    for (int i = 0; i < nbrOfAttempts; i++) {
      DisplayManager display = com.foc.Globals.getDisplayManager();
      
      if (display != null) {
        Navigator navigator = display.getNavigator();
        Globals.logString(" Navigator:" + navigator.getClass().getName());
        if (navigator != null && navigator instanceof FDesktop) {
          FDesktop desktop = (FDesktop) navigator;
          menuBar = desktop.getMenuBar();
          break;
        }
      }
      
      if (menuBar == null) {
        sleep(0.5);
      }
    }
    
    focAssertNotNull("Menu Bar found ? ", menuBar);
    
    return menuBar;
  }
  
  private JMenuItem menu_FindPopupMenu_Recursive(JMenuItem parentMenu, String menuItemName){
  	JMenuItem ret = null;
  	if(parentMenu != null){
  		if(parentMenu.getName() != null && parentMenu.getName().equals(menuItemName)){
  			ret = parentMenu;
  		}else{
  			for(int i=0; i<parentMenu.getComponentCount() && ret == null; i++){
  				ret = menu_FindPopupMenu_Recursive((JMenuItem) parentMenu.getComponent(i), menuItemName);
  			}
  		}
  	}
  	return ret;
  }
  
  public JMenuItem menu_ClickPopupMenu(String menuItemName){
  	FTable     table     = table_GetDefault();
  	JPopupMenu popupMenu = table.getPopupMenu();
  	JMenuItem  menu      = null;
  	
  	for(int i=0; i<popupMenu.getComponentCount() && menu == null; i++){
  		Component comp = popupMenu.getComponent(i);
  		if(comp != null && comp instanceof JMenuItem){
	  		if(comp.getName() != null && comp.getName().equals(menuItemName)){
	  			menu = (JMenuItem) comp;
	  		}else{
	  			menu = menu_FindPopupMenu_Recursive((JMenuItem)comp, menuItemName);
	  		}
  		}
  	}
  	return menu;
  }
  
  public void menu_Click(Container menuContainer, String menuItemName) {
  	sleep(1);
    logBeginGuiAction("JMenuItem", menuItemName, "Click");
    JMenuItem menu = (JMenuItem) getChildNamed(menuContainer, "MENU", menuItemName, DEFAULT_NUMBER_OF_ATTEMPTS);
    // Since there is AssertNotNull in function getChildNamed
    // focAssertNotNull("Menu Found ?", menu);
    menu.doClick();
    logEndGuiAction();  	
  }
  
  public void menu_Click(String menuItemName) {
  	menu_Click(getMenuBar(DEFAULT_NUMBER_OF_ATTEMPTS), menuItemName);
  }
  
  public void menu_ClickByPath(String[] path) {
    boolean found = false;
    sleep(1);
    logBeginGuiAction("JMenuItem", path[path.length - 1], "Click");
    JMenuBar menuBar = getMenuBar(DEFAULT_NUMBER_OF_ATTEMPTS);
    for(int i = 0; i < menuBar.getMenuCount() && !found; i++){
      if (menuBar.getMenu(i).getText().equals(path[0])) {
        JMenuItem curMenuItem = menuBar.getMenu(i);
        int x = 1;
        
        while(!found && curMenuItem != null && x <= path.length){
          if(curMenuItem instanceof JMenu){
            JMenu curMenu = (JMenu) curMenuItem;
            JMenuItem nextMenu = null;
            
            for(int j = 0; j < curMenu.getItemCount() && nextMenu == null; j++){
              JMenuItem tempMenuItem = curMenu.getItem(j);
              if(tempMenuItem != null && tempMenuItem.getText() != null && tempMenuItem.getText().equals(path[x])){
                nextMenu = tempMenuItem;
              }
            }
            
            curMenuItem = nextMenu;
            x++;
          } else if (x == path.length) {
            curMenuItem.doClick();
            found = true;
          } else {
            curMenuItem = null;
          }
        }
      }
    }
    focAssertTrue("Menu found and clicked", found);
    logEndGuiAction();
  }
  
  /*
   * public void menu_ClickByPath(String[] path) { sleep(1);
   * logBeginGuiAction("JMenuItem", path[path.length - 1], "Click"); JMenuBar
   * menuBar = getMenuBar(DEFAULT_NUMBER_OF_ATTEMPTS); for (int i = 0; i <
   * menuBar.getMenuCount(); i++) { if
   * (menuBar.getMenu(i).getText().equals(path[0])) { for (int y = 0; y <
   * menuBar.getMenu(i).getItemCount(); y++) { if
   * (menuBar.getMenu(i).getItem(y).getText().equals(path[1])) { if
   * (menuBar.getMenu(i).getItem(y) instanceof JMenuItem && path.length == 2) {
   * JMenuItem jmi = (JMenuItem) menuBar.getMenu(i).getItem(y); jmi.doClick();
   * logEndGuiAction(); } else { JMenu jm = (JMenu)
   * menuBar.getMenu(i).getItem(y); for (int x = 2; x < path.length; x++) { for
   * (int j = 0; j < jm.getItemCount(); j++) { if
   * (jm.getItem(j).getText().equals(path[x])) {
   * System.out.println(jm.getItem(j).getText()); if (x == (path.length - 1)) {
   * jm.getItem(j).doClick(); logEndGuiAction(); break; } jm = (JMenu)
   * jm.getItem(j); } } } } } } } } }
   */
  // #######################################
  // #######################################
  // FIND CHILD
  // #######################################
  // #######################################
  private static int recursiveLevelToDelete = 1;
  private static Component getChildNamed_Recursive(Component parent, String name, boolean includingNonVisible) {
    Component ret = null;
    recursiveLevelToDelete++;
    if(includingNonVisible || parent.isVisible()){
    	if(isDebug_GetChildName()){
    		Globals.logString("Gui Comp Named:"+parent.getName());
    	}
      if (name.equals(parent.getName())) {
        ret = parent;
      } else {
        Component[] children = (parent instanceof JMenu) ? ((JMenu) parent).getMenuComponents() : ((Container) parent).getComponents();
        for (int i = 0; i < children.length && ret == null; i++) {
          String debugString = ""; for(int xx=0; xx<recursiveLevelToDelete;xx++) debugString+=" ";
          //Globals.logString(debugString+"comp:"+children[i].getName());

          Component child = getChildNamed_Recursive(children[i], name, includingNonVisible);
          if (child != null) {
            ret = child;
          }
        }
      }
    }
    recursiveLevelToDelete--;
    return ret;
  }

  private Component getChildNamed_ForExplicitParent(Component parent, String name, int nbrAttempts, boolean includeNotVisible){
    Component ret = null;
    
    if(parent != null){
	    for (int a = 0; a < nbrAttempts; a++) {
	      ret = getChildNamed_Recursive(parent, name, includeNotVisible);
	      
	      if(ret != null){
	        break;
	      }
	      try{
	      	Thread.sleep(2000);
	      }catch(Exception e){
	      	Globals.logException(e);
	      }
	    }
    }	    
    
    return ret;
  }
  
  public Component getChildNamed(Component parent, String name, int nbrAttempts, boolean includeNotVisible) {
    Component ret = null;
    //If the parent is null we try first with the Main Frame then with the Main Dialog
    if(parent != null){
    	ret = getChildNamed_ForExplicitParent(parent, name, nbrAttempts, includeNotVisible);
    }else{
	    Component tempActiveDialog = getActiveDialog();
	    Component tempMainFrame    = getDefaultRootContainer();
	    if(tempActiveDialog != null){
	    	ret = getChildNamed_ForExplicitParent(tempActiveDialog, name, nbrAttempts, includeNotVisible);
	    }
	    if(ret == null && tempMainFrame != null){
	    	ret = getChildNamed_ForExplicitParent(tempMainFrame, name, nbrAttempts, includeNotVisible);
	    }
    }
    
    focAssertNotNull("Gui Component '" + name+ "' Found", ret);
    
    return ret;
  }
  
  private String composeName(String table, String field){
  	String str = null;
  	if(table != null && table.length() > 0){
  		str = table + "." + field;
  	}else{
  		str = field;
  	}
  	return str;
  }
  
  public Component getChildNamed(Component parent, String name, int nbrAttempts) {
    return getChildNamed(parent, name, nbrAttempts, false);
  }
  
  public Component getChildNamed(Component parent, String name) {
    return getChildNamed(parent, name, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public Component getChildNamed(Container parent, String table, String field, int nbrAttempts) {
    return getChildNamed(parent, composeName(table, field), nbrAttempts);
  }
  
  public Component getChildNamed(Container parent, String table, String field) {
    return getChildNamed(parent, composeName(table, field), DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public Component getChildNamed(String table, String field, int nbrAttempts) {
    return getChildNamed((Container) null, composeName(table, field), nbrAttempts);
  }
  
  public Component getChildNamed(String table, String field) {
    return getChildNamed((Container) null, composeName(table, field), DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public Component getChildNamed(String name, int nbrAttempts, boolean includeNotVisible) {
    return getChildNamed((Container) null, name, nbrAttempts, includeNotVisible);
  }
  
  public Component getChildNamed(String name, int nbrAttempts) {
    return getChildNamed((Container) null, name, nbrAttempts);
  }
  
  public Component getChildNamed(String name) {
    return getChildNamed((Container) null, name, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public Component getChildNamed(String name, boolean includeNotVisible) {
    return getChildNamed((Container) null, name, DEFAULT_NUMBER_OF_ATTEMPTS, includeNotVisible);
  }
  
  // #######################################
  // #######################################
  // GET CHILD INDEXED - for modal dialog box
  // #######################################
  // #######################################
  
  static int counter;
  
  public static Component getChildIndexed(Component parent, String klass, int index) {
    counter = 0;
    if (parent instanceof Window) {
      Component[] children = ((Window) parent).getOwnedWindows();
      for (int i = 0; i < children.length; ++i) {
        if (children[i] instanceof Window && !((Window) children[i]).isActive()) {
          continue;
        }
        Component child = getChildIndexedInternal(children[i], klass, index);
        if (child != null) {
          return child;
        }
      }
    }
    return null;
  }
  
  private static Component getChildIndexedInternal(Component parent, String klass, int index) {
    if (parent.getClass().toString().endsWith(klass)) {
      if (counter == index) {
        return parent;
      }
      ++counter;
    }
    if (parent instanceof Container) {
      Component[] children = (parent instanceof JMenu) ? ((JMenu) parent).getMenuComponents() : ((Container) parent).getComponents();
      for (int i = 0; i < children.length; i++) {
        Component child = getChildIndexedInternal(children[i], klass, index);
        if (child != null) {
          return child;
        }
      }
    }
    return null;
  }
  
  // #######################################
  // #######################################
  // GUI COMPONENT SET VALUE
  // #######################################
  // #######################################
  
  public void preventAlteringIfDisabled(Component comp) {
    if(!comp.isEnabled()){
      comp.setFocusable(false);
    }
  }
  
  protected boolean requestFocus(Component comp){
  	boolean focused = comp.hasFocus();
  	int attempts = 0;
  	//focused=true;
  	while(!focused && attempts < DEFAULT_NUMBER_OF_ATTEMPTS){
  		focused = comp.requestFocusInWindow();
  		Globals.logString("focused = "+focused);
  		if(!focused){
  			sleep(1);
  		}
  		attempts++;
  	}
  	
  	focAssertTrue("RequestFocuse for "+comp.getName(), focused);
  	return !focused;
  }
  
  public void guiComponent_SetValue(Container rootContainer, String table, String field, Object value, int maxAttempts) {
    Component comp = (Component) getChildNamed(rootContainer, table, field, maxAttempts);
    preventAlteringIfDisabled(comp);
    
    // focAssertNotNull("Gui component found ? ", comp);
    if (comp instanceof FGTextField || comp instanceof FGDateField || comp instanceof FGNumField || comp instanceof FGTimeField) {
      //logBeginGuiAction(comp.getClass().getSimpleName(), comp.getName(), "setText '" + value + "'");
      logBeginGuiAction(comp.getClass().getSimpleName(), comp.getName(), "setText" + value);
      JTextField textField = (JTextField) comp;
      requestFocus(comp);
      textField.setText("" + value);
      textField.postActionEvent();
      logEndGuiAction();
    } else if (comp instanceof FGObjectComboBox) {
      //logBeginGuiAction("FGObjectComboBox", comp.getName(), "Select '" + value + "'");
      logBeginGuiAction("FGObjectComboBox", comp.getName(), "Select " + value);
      FGObjectComboBox combo = (FGObjectComboBox) comp;
      requestFocus(comp);
      focAssertTrue("Value "+value.toString()+" available in ComboBox '"+comp.getName()+"'", combo.isValueInItemList(value)); 
      combo.setSelectedItem(value);
      logEndGuiAction();
    } else if (comp instanceof FGComboBox) {
      //logBeginGuiAction("FGComboBox", comp.getName(), "Select '" + value + "'");
      logBeginGuiAction("FGComboBox", comp.getName(), "Select " + value);
      FGComboBox combo = (FGComboBox) comp;
      requestFocus(comp);
      focAssertTrue("Value "+value.toString()+" available in ComboBox '"+comp.getName()+"'", combo.isValueInItemList(value)); 
      combo.isValueInItemList(value);
      combo.setSelectedItem(value);
      logEndGuiAction();
    } else if (comp instanceof FGCheckBox) {
      logBeginGuiAction("FGCheckBox", comp.getName(), "Selected");
      FGCheckBox chkbox = (FGCheckBox) comp;
      requestFocus(comp);
      chkbox.setSelected((Boolean) value);
      logEndGuiAction();
    } else if (comp instanceof FGDateChooser) {
      logBeginGuiAction("FGDateChoose", comp.getName(), "Selected");
      FGDateChooser dateChooser = (FGDateChooser) comp;
      requestFocus(comp);
      try{
      	dateChooser.setDate((String) value);
      }catch(Exception e){
      	Globals.logException(e);
      }
      logEndGuiAction();
    }
  }
  
  public Component getComponent(String compName) {
    return getChildNamed(compName, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  /*
   * public void guiComponent_SetValue(String compName, Object val) { Component
   * comp = getComponent(compName);
   * 
   * if (comp != null) { if (comp instanceof FGTextField) { FGTextField
   * textField = (FGTextField) comp; textField.setText(""+val);
   * textField.postActionEvent(); } else if (comp instanceof FGObjectComboBox) {
   * FGObjectComboBox combo = (FGObjectComboBox) comp;
   * combo.setSelectedItem(val); } else if (comp instanceof FGComboBox) {
   * FGComboBox combo = (FGComboBox) comp; combo.setSelectedItem(val); }else if
   * (comp instanceof FGCheckBox){ FGCheckBox chkbox = (FGCheckBox) comp;
   * chkbox.setSelected((Boolean) val); } sleep(1); } }
   * 
   * /*public void guiComponent_SetValue(String compName, Object val) {
   * 
   * Component comp = getComponent(compName); if (comp instanceof JCheckBox) {
   * JCheckBox chkbox = (JCheckBox) comp; chkbox.setSelected((Boolean) val); }
   * else if (comp instanceof JComboBox) { JComboBox cb = (JComboBox) comp;
   * cb.setSelectedItem(val); } }
   */

  public void guiComponent_SetValue(String table, String field, Object value, int maxAttempts) {
    guiComponent_SetValue(null, table, field, value, maxAttempts);
  }
  
  public void guiComponent_SetValue(Container rootContainer, String table, String field, Object value) {
    guiComponent_SetValue(rootContainer, table, field, value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public void guiComponent_SetValue(String table, String field, Object value) {
    guiComponent_SetValue(null, table, field, value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public void guiComponent_SetValue(int fieldId, Object value) {
    assertNotNull("Default FocDesc not NULL ?", getDefaultFocDesc());
    guiComponent_SetValue(getDefaultFocDesc().getStorageName(), getDefaultFocDesc().getFieldByID(fieldId).getName(), value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  protected String convertDateInGuiTypingFormat(Date date){
  	String stringDate = "";
  	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
  	stringDate = dateFormat.format(date);
  	return stringDate;
  }

  protected String getCurrentDateInStringFormat(){
  	return convertDateInGuiTypingFormat(Globals.getApp().getSystemDate());
  }

  // #######################################
  // #######################################
  // INTERNAL FRAME
  // #######################################
  // #######################################
  
  public InternalFrame internalFrame_Activate(String name) throws Exception{
    InternalFrame internalFrame = (InternalFrame) getChildNamed(name + ".INTFRAME", DEFAULT_NUMBER_OF_ATTEMPTS);
    internalFrame.setIcon(false);
    internalFrame.setSelected(true);
    internalFrame.setVisible(false);
    internalFrame.setVisible(true);
    Globals.getApp().getDisplayManager().restoreInternalFrame(internalFrame);
    return internalFrame ;
  }

  public void internalFrame_Iconify(String name, boolean iconify) throws Exception{
    InternalFrame internalFrame = (InternalFrame) getChildNamed(name + ".INTFRAME", DEFAULT_NUMBER_OF_ATTEMPTS);
    internalFrame.setIcon(iconify);
  }

  // #######################################
  // #######################################
  // TREE
  // #######################################
  // #######################################

  private void insertUnderCurrentNode(String child){
  	sleep(3);
  	table_ClickInsertButton();
  	sleep(2);
  	keyboard_Type(child);
  	keyboard_TypeEnter();
  	sleep(2);
  }
  
  protected void tree_insertNode(String father, String child){
  	table_FindAndSelectRowByNodeTitle(getTable(), father, true);
  	insertUnderCurrentNode(child);
  }

  protected void tree_insertNode(ArrayList<String> father, String child){
  	sleep(1);
  	table_FindAndSelectRowByNodeTitlePath(getTable(), father, true);
  	insertUnderCurrentNode(child);
  }
  
  // #######################################
  // #######################################
  // TABLE
  // #######################################
  // #######################################

  public int table_GetRouwCount() {
    FTable fTable = (FTable) getChildNamed(getDefaultFocDesc().getStorageName() + ".TABLE", DEFAULT_NUMBER_OF_ATTEMPTS);
    FAbstractTableModel model = (FAbstractTableModel) fTable.getModel();
    return model.getRowCount();
  }
  
  public FTableModel table_GetTableModel() {
    FTable fTable = (FTable) getChildNamed(getDefaultFocDesc().getStorageName() + ".TABLE", DEFAULT_NUMBER_OF_ATTEMPTS);
    FTableModel model = (FTableModel) fTable.getModel();
    return model;
  }
  
  public void table_SetValue(int row, int columnId, Object value) {
    FTable fTable = (FTable) getChildNamed(getDefaultFocDesc().getStorageName() + ".TABLE", DEFAULT_NUMBER_OF_ATTEMPTS);
    FAbstractTableModel model = (FAbstractTableModel) fTable.getModel();
    int col = model.getTableView().getColumnIndexForId(columnId);
    logBeginGuiAction("Table cell", "("+row +" , "+col+")","writing : "+ value.toString());
    model.setValueAt(value, row, col);
    logEndGuiAction();
  }

  public void table_SetValue(int row, String columnName, Object value) {
    FTable fTable = (FTable) getChildNamed(getDefaultFocDesc().getStorageName() + ".TABLE", DEFAULT_NUMBER_OF_ATTEMPTS);
    
    boolean found = false;
    int     colID = 0;
    for(int i=0; i<fTable.getTableView().getColumnCount() && !found; i++){
    	FTableColumn col = fTable.getTableView().getColumnAt(i);
    	if(col.getTitle().equals(columnName)){
    		colID = col.getID();
    		found = true;
    	}
    }
    
    focAssertTrue("Finding Column title:"+columnName, found);
    if(found){
    	table_SetValue(row, colID, value);  	
    }
  }

  public FTable table_GetDefault(){
    FTable fTable = (FTable) getChildNamed(getDefaultFocDesc().getStorageName() + ".TABLE", DEFAULT_NUMBER_OF_ATTEMPTS);
  	return fTable;
  }
  
  public String table_GetValue(int row, int columnId){
    logBeginGuiAction("JTable", "Table", "GetValue");
    FTable fTable = table_GetDefault();
    FTableModel model = (FTableModel) fTable.getModel();
    int col = model.getTableView().getColumnIndexForId(columnId);
    String value = (String)model.getValueAt(row, col);
    logEndGuiAction();
    return value;
  }

  public int table_GetRowCount(){
  	int count = 0;
    logBeginGuiAction("JTable", "Table", "GetRowCount");
    FTable fTable = table_GetDefault();
    FTableModel model = (FTableModel) fTable.getModel();
    count = model.getRowCount();
    logEndGuiAction();
    return count;
  }

  public int table_ClickInsertButtonAndWaitForNewLine() {
    FTableModel model = table_GetTableModel();
    int rowCount0 = model.getRowCount();
    button_Click(FListPanel.BUTTON_INSERT);
    int rowCount1 = model.getRowCount();
    for (int att = 0; att < 5 && rowCount0 == rowCount1; att++) {
      sleep(1);
      rowCount1 = model.getRowCount();
    }
    focAssertEquals("Insertion on new line", rowCount0 + 1, rowCount1);
    return rowCount1 - 1;
  }

  public void table_ClickInsertButton() {
    button_Click(FListPanel.BUTTON_INSERT);
  }

  public void table_ClickEditButton() {
    button_Click(FListPanel.BUTTON_EDIT);
  }
  
  public void table_ClickExpandAllButton() {
  	button_Click(FTreeTablePanel.BUTTON_EXPAND_ALL);
  }

  private int table_FindAndSelectRowByColumn(FTable fTable, int searchColumn, Object cellValue, boolean doAssert){
    return table_FindAndSelectRowByColumn(fTable, searchColumn, searchColumn, cellValue, doAssert);
  }

  private int table_FindAndSelectRowByColumn(FTable fTable, int searchColumnIndex, int selectionColumnIndex, Object cellValue, boolean doAssert){
    int index = -1;
    boolean found = false;
    for (int i = 0; i < fTable.getRowCount() && !found; i++) {
      Object realCellValue = (Object) fTable.getValueAt(i, searchColumnIndex);
      if(realCellValue != null && realCellValue.equals(cellValue)){
        fTable.changeSelection(i, selectionColumnIndex, false, false);
        index = i;
        found = true;
      }
    }
    if(doAssert){
      focAssertTrue("Finding Cell with value '" + cellValue + "' at row: " + index, found);
    }
    return index;
  }
  
  public int table_FindAndSelectRowByNodeTitle(FTable fTable, String title, boolean doAssert) {
    int index = -1;
    FTreeTableModel treeTableModel = (FTreeTableModel) fTable.getModel();
    FTree tree = treeTableModel.getTree();
    FNode node = tree != null ? tree.getNodeByDisplayTitle(title) : null;
    index = treeTableModel != null ? treeTableModel.getRowForNode(node) : null;
    if(index >= 0){
    	FTableColumn column = treeTableModel.getTableView().getColumnById(FField.TREE_FIELD_ID);
    	if(column != null){
    		int colId = column.getID();
    		int col = treeTableModel.getTableView().getColumnIndexForId(colId);
    		requestFocus(fTable);
        fTable.changeSelection(index, col, false, false);
    	}
    }
    if(doAssert){
      focAssertTrue("Finding Row For Node Title '" + title + "' at row: " + index, index >= 0);
    }
    return index;
  }

  public int table_FindAndSelectRowByNodeTitlePath(FTable fTable, ArrayList<String> titlePath, boolean doAssert){
  	return table_FindAndSelectRowByNodeTitlePath(fTable, titlePath, FField.TREE_FIELD_ID, doAssert);
  }
  
  public int table_FindAndSelectRowByNodeTitlePath(FTable fTable, ArrayList<String> titlePath, int selectionColumnID, boolean doAssert){
    int index = -1;
    FTreeTableModel treeTableModel = (FTreeTableModel) fTable.getModel();
    FTree tree = treeTableModel.getTree();
    
    FNode node = tree.getRoot();
    for(int i=0; i<titlePath.size(); i++){
    	String title = titlePath.get(i);
    	FNode  child = node.findChild(title);
    	if(child != null){
    		node = child;
    	}else{
    		node = null;
    	}
    }
    index = (node != null && treeTableModel != null) ? treeTableModel.getRowForNode(node) : -1;
    if(index >= 0){
    	FTableColumn column = treeTableModel.getTableView().getColumnById(selectionColumnID);
    	if(column != null){
    		int          colId    = column.getID();
    		FTableColumn tableCol = treeTableModel.getTableView().getColumnById(colId);
    		requestFocus(fTable);//This line should be before the changinf of tables
    		//If the column is invisibvle in this table then it should be visible in the other. So The selection is done on the other.
    		FTable selectionTable = fTable;
    		if(!tableCol.isShow()){
    			if(fTable.getScrollPane().getScrollTable() == selectionTable){
    				selectionTable = fTable.getScrollPane().getFixedTable();
    			}else{
    				selectionTable = fTable.getScrollPane().getScrollTable();
    			}
    		}
    		int colIndex = ((FAbstractTableModel)selectionTable.getModel()).getTableView().getColumnIndexForId(colId);
    		if(!selectionTable.isFixed() && fTable.isFixed()) colIndex -= fTable.getTableView().getColumnsToFreeze();
    	  selectionTable.changeSelection(index, colIndex, false, false);
    	}
    }
    if(doAssert){
    	String fieldPath = "";
    	for(int i=0; i<titlePath.size(); i++){
    		if(i > 0) fieldPath += "-";
    		fieldPath += titlePath.get(i);
    	}
      focAssertTrue("Finding Row For Node Title '" + fieldPath + "' at row: " + index, index >= 0);
    }
    return index;
  }
  
  public int table_FindAndSelectRow(int columnId, Object cellValue) {
    FTable fTable = getTable();
    int col = fTable.getTableView().getColumnIndexForId(columnId);
    return table_FindAndSelectRowByColumn(fTable, col, cellValue, true);
  }

  public int table_FindAndSelectRow(Object cellValue) {
    return table_FindAndSelectRow(cellValue, true);
  }
  
  public int table_FindAndSelectRow(Object cellValue, boolean doAssert) {
    int     index  = -1;
    boolean found  = false;
    FTable  fTable = getTable();
    for (int j = 0; j < fTable.getColumnCount() && !found; j++) {
      index = table_FindAndSelectRowByColumn(fTable, j, cellValue, false);
      found = index >= 0;
    }
    if(doAssert){
      focAssertTrue("Finding Cell with value '" + cellValue + "' at row:" + index, found);
    }
    return index;
  }

  public int table_FindAndSelectRow_ByKey(String tableName, Object[] cell_ColID_Value_Array, boolean doAssert) {
    int     index   = -1;
    String  message = "Finding Row with value '";
    FTable  fTable  = getTable(tableName);
    if(fTable != null){
    	FTableModel tModel = (FTableModel) fTable.getTableModel();
    	if(tModel != null){
    		//First we need to get the column indexes that correspond to the col ids
    		int[]    columns = new int[(int)(cell_ColID_Value_Array.length/2)];
    		Object[] values  = new Object[(int)(cell_ColID_Value_Array.length/2)];
    		for(int i=0; i<cell_ColID_Value_Array.length; i=i+2){
    			columns[i] = Integer.valueOf((Integer)cell_ColID_Value_Array[i]);
    			values[i]  = cell_ColID_Value_Array[i+1];
    			columns[i] = tModel.getTableView().getColumnIndexForId(columns[i]);
    		}
    		
    		boolean equal = false;
		    for(int r = 0; r < tModel.getRowCount() && !equal; r++){
		    	equal = true;
		    	for(int c=0; c<columns.length && equal; c++){
		    		Object value = tModel.getValueAt(r, columns[c]);
		    		equal = value.equals(values[c]);
		    		if(r==0){
		    			if(c==0){
		    				message += cell_ColID_Value_Array[c*2+1];
		    			}else{
		    				message += ","+cell_ColID_Value_Array[c*2+1];
		    			}
		    		}
		    	}
		    	if(equal){
		    		index = r;
		    	}
		    }
    	}
    }
    if(doAssert){
      focAssertTrue("Finding Row with value '" + message + "' at row:" + index, index>=0);
    }
    return index;
  }

  public FTable getTable() {
    return getTable(getDefaultFocDesc().getStorageName());
  }

  public FTable getTable(String storageName) {
    FTable table = (FTable) getChildNamed(storageName + ".TABLE", DEFAULT_NUMBER_OF_ATTEMPTS);
    return table;
  }

  // #######################################
  // #######################################
  // Drag And Drop
  // #######################################
  // #######################################
  
  public Point getLocation_InTable(String storageName, int columnID, Object value){
  	FTable table = getTable(storageName);
  	int colIndex = table.getTableView().getColumnIndexForId(columnID);
  	int    row   = table_FindAndSelectRowByColumn(table, colIndex, value, true);

  	return table.getRowLocation(row);
  }

  public Point getLocation_InTree(String storageName, String title){
  	FTable table = getTable(storageName);
  	int    row   = table_FindAndSelectRowByNodeTitle(table, title, true);

  	return table.getRowLocation(row);
  }

  public Point getLocation_InTree(String storageName, ArrayList<String> nodePath){
  	FTable table = getTable(storageName);
  	int    row   = table_FindAndSelectRowByNodeTitlePath(table, nodePath, true);

  	return table.getRowLocation(row);
  }
  
  public void dnd_Table2Table(String srcStorageName, int srcColumnId, Object srcCellValue, String tarStorageName, int tarColumnId, Object tarCellValue){
  	FTable srcTable = getTable(srcStorageName);
  	int    srcRow   = table_FindAndSelectRowByColumn(srcTable, srcColumnId, srcCellValue, false);
  	FTable tarTable = getTable(tarStorageName);
  	int    tarRow   = table_FindAndSelectRowByColumn(tarTable, tarColumnId, tarCellValue, false);

  	dnd_Table2Table(srcTable, srcRow, tarTable, tarRow);
  }

  public void dnd_Tree2Tree(String srcStorageName, String srcTitle, String tarStorageName, String tarTitle){
  	FTable srcTable = getTable(srcStorageName);
  	int    srcRow   = table_FindAndSelectRowByNodeTitle(srcTable, srcTitle, true);
  	FTable tarTable = getTable(tarStorageName);
  	int    tarRow   = table_FindAndSelectRowByNodeTitle(tarTable, tarTitle, true);

  	dnd_Table2Table(srcTable, srcRow, tarTable, tarRow);
  }

  public void dnd_Tree2Tree(String srcStorageName, String srcTitle, String tarStorageName, ArrayList<String> tarTitlePath){
  	FTable srcTable = getTable(srcStorageName);
  	int    srcRow   = table_FindAndSelectRowByNodeTitle(srcTable, srcTitle, true);
  	FTable tarTable = getTable(tarStorageName);
  	int    tarRow   = table_FindAndSelectRowByNodeTitlePath(tarTable, tarTitlePath, true);

  	dnd_Table2Table(srcTable, srcRow, tarTable, tarRow);
  }
  
  public void dnd_Table2Table(FTable srcTable, int srcRow, FTable tarTable, int tarRow){
  	Point  srcPt    = srcTable.getRowLocation(srcRow);
  	Point  tarPt    = tarTable.getRowLocation(tarRow);

  	dnd_Point2Point(srcPt, tarPt);
  }
  
  public void dnd_Table2Tree(	String 						srcStorageName, 
  														int 							srcColumnId,
  														Object 						srcCellValue,
  														String 						tarStorageName,
  														ArrayList<String> tarTitlePath){
  	Point srcPt = getLocation_InTable(srcStorageName, srcColumnId, srcCellValue);
  	Point tarPt = getLocation_InTree(tarStorageName, tarTitlePath);

  	dnd_Point2Point(srcPt, tarPt);
  }

  public void dnd_Tree2Table(	String srcStorageName, 
															String srcTitle,
															String tarStorageName){
  	FTable srcTable = getTable(srcStorageName);
  	int    srcRow   = table_FindAndSelectRowByNodeTitle(srcTable, srcTitle, true);
  	FTable tarTable = getTable(tarStorageName);
  	
  	Point  srcPt    = srcTable.getRowLocation(srcRow);
  	Point  tarPt    = new Point(tarTable.getLocationOnScreen());
  	tarPt.x += 10;
  	tarPt.y += 10;

  	dnd_Point2Point(srcPt, tarPt);
	}

  public void dnd_Table2Table(	String   srcStorageName, 
																Object[] src_ColIDs_Values_Array,
																String   tarStorageName){
		FTable srcTable = getTable(srcStorageName);
		int    srcRow   = table_FindAndSelectRow_ByKey(srcStorageName, src_ColIDs_Values_Array, true);
		FTable tarTable = getTable(tarStorageName);
		
		Point  srcPt    = srcTable.getRowLocation(srcRow);
		Point  tarPt    = new Point(tarTable.getLocationOnScreen());
		tarPt.x += 10;
		tarPt.y += 10;
		
		dnd_Point2Point(srcPt, tarPt);
	}
  
  public void dnd_Point2Point(Point srcPt, Point tarPt){
  	mouse_Move((int)srcPt.getX(), (int)srcPt.getY());
  	mouse_LeftPress();
  	sleep(1);
  	
  	int totalSteps = 3;
  	int x=(int)srcPt.getX();
  	int y=(int)srcPt.getY();
  	
  	while(x!=tarPt.getX() && totalSteps > 0){
  		x += Math.signum(tarPt.getX() - srcPt.getX());
  		mouse_Move(x, y);
  		sleep(0.2);
  		totalSteps--;
  	}
  	
  	while(y!=tarPt.getY() && totalSteps > 0){
  		y += Math.signum(tarPt.getY() - srcPt.getY());
  		mouse_Move(x, y);
  		sleep(0.2);
  		totalSteps--;
  	}
  	  	
  	mouse_Move((int)tarPt.getX(), (int)tarPt.getY());
  	mouse_LeftRelease();  	
  }
  
  // #######################################
  // #######################################
  // TABBED PANE
  // #######################################
  // #######################################
  
  public void tabbedPanel_ActivateForTable(String tableName){
  	Click_TabbedPane(tableName+".TABLE");
  }
  
  public void tabbedPanel_ActivateForTable(){
  	String tableName = getDefaultFocDesc() != null ? getDefaultFocDesc().getStorageName() : "";
  	tabbedPanel_ActivateForTable(tableName);
  }
  
  public void Click_TabbedPane(Component comp) {
    ArrayList<Component> tabbedArray = new ArrayList<Component>();
    if(comp != null){
      Component root = getDefaultRootContainer();
      
      Component parent = comp;
      while(parent != root && parent != null){
        tabbedArray.add(parent);
        parent = parent.getParent();
      }
      
      for(int i = (tabbedArray.size() - 1); i > 0; i--){
        Component c = tabbedArray.get(i);
        if (c instanceof JTabbedPane) {
          JTabbedPane tp = (JTabbedPane) c;
          requestFocus(tp);          
          tp.setSelectedComponent(tabbedArray.get(i - 1));
          sleep(1);
        }
      }
    }
  }

  public void Click_TabbedPane(String paneTitle) {
    Component comp = getChildNamed(paneTitle, DEFAULT_NUMBER_OF_ATTEMPTS, true);
    Click_TabbedPane(comp);
  }
  
  // #######################################
  // #######################################
  // BUTTON
  // #######################################
  // #######################################

  public void button_Click(AbstractButton button){
    preventAlteringIfDisabled(button);
    final AbstractButton usedButton = button;
    
    //focAssertTrue("Focused :" + usedButton.getName(), usedButton.requestFocusInWindow());
    requestFocus(usedButton);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
      	//focAssertTrue("Focused :" + usedButton.getName(), usedButton.requestFocusInWindow());
        usedButton.doClick();
      }
    });
  }
  
  public void button_Click(Container rootContainer, String storageName, String buttonSuffix, int maxAttempts) {
    logBeginGuiAction("JButton", buttonSuffix, "Click");
    AbstractButton button = (AbstractButton) getChildNamed(rootContainer, storageName, buttonSuffix, maxAttempts);
    button_Click(button);
    logEndGuiAction();
  }
  
  public void button_Click(String buttonSuffix, int maxAttempts) {
    assertNotNull("Default FocDesc NULL", getDefaultFocDesc());
    button_Click(null, getDefaultFocDesc().getStorageName(), buttonSuffix, maxAttempts);
  }
  
  public void button_Click(String buttonSuffix) {
    button_Click(buttonSuffix, DEFAULT_NUMBER_OF_ATTEMPTS);
  }

  public void button_ClickStatusApprove(final FocDesc focDesc) {
    sleep(1);
    button_ClickIfModalDialogBoxShows("Yes", new Runnable() {
      public void run() {
      	setDefaultFocDesc(focDesc);
      	button_Click(StatusFieldPanel.getButtonApproveName_Suffix(), 2*DEFAULT_NUMBER_OF_ATTEMPTS);
      }
    });
  }

  public void button_ClickValidateCancel(Container rootContainer, String preButtonName, boolean validate) {
    sleep(0.5);
    logBeginGuiAction("JButton", preButtonName + " " + ((validate) ? "VALIDATE" : "CANCEL"), "Click");
    FGButton validButton = null;

    String buttonFullName = "";
  	if(preButtonName == null){
  		preButtonName = "";
  		if(getDefaultFocDesc() != null){
  			preButtonName = getDefaultFocDesc().getStorageName();
  		}
  	}
    if(validate){
    	buttonFullName = FValidationPanel.getFullValidationButtonName(preButtonName);
    }else{
    	buttonFullName = FValidationPanel.getFullCancellationButtonName(preButtonName);
    }
    
    validButton = (FGButton) getChildNamed(rootContainer, buttonFullName);
    
    preventAlteringIfDisabled(validButton);
    validButton = (FGButton) validButton;
    if(requestFocus(validButton)){
    	Globals.logString("Focus Error Could not focus "+validButton.getName());
    }
    validButton.doClick();
    logEndGuiAction();
  }
  
  public void button_ClickValidate(Container rootContainer, String preButtonName) {
  	button_ClickValidateCancel(rootContainer, preButtonName, true);
  }
  
  public void button_ClickValidate(String preButtonName) {
    button_ClickValidate(null, preButtonName);
  }
  
  public void button_ClickValidate(){
    //button_ClickValidate(null, "");
    button_ClickValidate(null, getDefaultFocDesc() != null ? getDefaultFocDesc().getStorageName() : "");
  }

  public void button_ClickCancel(Container rootContainer, String preButtonName) {
  	button_ClickValidateCancel(rootContainer, preButtonName, false);
  }
  
  public void button_ClickCancel(String preButtonName) {
    button_ClickCancel(null, preButtonName);
  }

  public void button_ClickWithModalDialogBox(final FocDesc desc, final String buttonName) {
    sleep(1);
    button_ClickIfModalDialogBoxShows("Yes", new Runnable() {
      public void run() {
        setDefaultFocDesc(desc);
        button_Click(buttonName);
      }
    });
  }
 
  public boolean button_ClickDialog(String buttonText, int nbrOfAttempts){
    JButton button = null;
    Globals.logString("Start JButton Search");
    for (int i = 0; button == null; i++) {
      sleep(0.2);
      button = (JButton) getChildIndexed(getMainFrame(), "JButton", 0);
      if(button != null){
      int buttonIndex = 0;
        while(!button.getText().equals(buttonText)){
          sleep(0.2);
          button = (JButton) getChildIndexed(getMainFrame(), "JButton", buttonIndex++);
          assertTrue(buttonIndex < 5);
        }
      }
      if(i >= nbrOfAttempts){
        Globals.logString("End JButton Search");
      }
      assertTrue(i < nbrOfAttempts);
    }
    boolean isJOptionPane = false;
    boolean isJDialog = false;
    if(button != null){
      Component comp = (Component)button;
      while ( comp != null){
        comp = comp.getParent();
        if( comp instanceof JOptionPane){
          isJOptionPane = true;
        }else if ( comp instanceof JDialog ){
          isJDialog = true;
        }
      }
    }
    if(isJOptionPane && isJDialog){
      sleep(0.5);
      button.requestFocusInWindow();
      sleep(0.5);
      button.doClick();
      return true;
    }else{
      return false;
    }
  }
  
  public boolean button_ClickIfModalDialogBoxShows(String buttonText, Runnable runnable){
  	return button_ClickIfModalDialogBoxShows(buttonText, 10, runnable);
  }
  
  public boolean button_ClickIfModalDialogBoxShows(String buttonText, int nbrOfAttempts, Runnable runnable){
    
    SwingUtilities.invokeLater(runnable);
        
    return button_ClickDialog(buttonText, nbrOfAttempts);
  }
  
  // #######################################
  // #######################################
  // ASSERT
  // #######################################
  // #######################################
  
  public void focAssertNotSame(String message, Object obj1, Object obj2) {
    failure = true;
    logBeginAssert(message);
    assertNotSame(message, obj1, obj2);
    failure = false;
    logEndAssert();
  }
  
  public void focAssertEquals(String message, Object obj1, Object obj2) {
    failure = true;
    logBeginAssert(message);
    assertEquals(message, obj1, obj2);
    failure = false;
    logEndAssert();
  }
  
  public void focAssertTrue(String message, boolean arg1) {
    failure = true;
    logBeginAssert(message);
    assertTrue(message, arg1);
    failure = false;
    logEndAssert();
  }
  
  public void focAssertNotNull(String message, Object obj) {
    failure = true;
    logBeginAssert(message);
    assertNotNull(message, obj);
    failure = false;
    logEndAssert();
  }
  
  // #######################################
  // #######################################
  // LOGIN - EXIT
  // #######################################
  // #######################################
  
  public void testLogin() throws Exception {
    typeUserLogin(userName);
    Method meth = klass.getMethod("main", new Class[] { String[].class });
    meth.invoke(this, new Object[] { new String[] {"/unitTesting"} });
    Globals.getApp().setFocTestSuite(getFocTestSuite());
  }
  
  public void testExit() throws Exception {
    Globals.getApp().addExitListener(this);
    sleep(50);
    menu_Click("Exit");
    blockUntilExit();
  }

  public void testBlockUntilExit() throws Exception {
    blockUntilExit();
  }

  public void typeUserLogin(final String userName) {
    setUserName(userName);
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        sleep(10);/* When database connection is very slow this is usefull */
        JFrame mainFrame = getMainFrame();
        logBeginGuiAction("TextField", "Login", "Insert " + userName);
        guiComponent_SetValue(mainFrame, FocUser.getFocDesc().getStorageName(), FocUser.FLDNAME_NAME, getUserName(), 3*DEFAULT_NUMBER_OF_ATTEMPTS);
        logEndGuiAction();
        button_ClickValidate(mainFrame, "");
      }
    });
  }
  
  public boolean isEndOfTest() {
    return endOfTest;
  }
  
  public void setEndOfTest(boolean endOfTest) {
    this.endOfTest = endOfTest;
    // sleep(2000);//Should be > blockUntilExit
  }
  
  public void blockUntilExit() {
    while(!isEndOfTest()){
      sleep(0.1);// Should be < setEndOfTest(true);
    }
  }
  
  public void replyToExit() {
    setEndOfTest(true);
  }
  
  // #######################################
  // #######################################
  // LOG
  // #######################################
  // #######################################
  
  public void logBeginAssert(String message) {
    focTestSuite.getLogFile().logBeginAssert(message);
  }
  
  public void logEndAssert() {
    focTestSuite.getLogFile().logEndAssert();
  }
  
  public void logBeginStep(String message) {
    focTestSuite.getLogFile().logBeginStep(message);
  }
  
  public void logEndStep() {
    focTestSuite.getLogFile().logEndStep();
  }
  
  public void logBeginGuiAction(String component, String componentName, String action) {
    focTestSuite.getLogFile().logBeginGuiAction(component, componentName, action);
  }
  
  public void logEndGuiAction() {
    focTestSuite.getLogFile().logEndGuiAction();
  }
  
  public void logStatus(boolean success) {
    focTestSuite.getLogFile().logStatus(success);
  }
  
  public void logBeginTest(String testName) {
    focTestSuite.getLogFile().logBeginTest(testName);
  }
  
  public void logEndTest(String testName) {
    focTestSuite.getLogFile().logEndTest(testName);
  }
  
  public void logSuiteName(String suiteName) {
    focTestSuite.getLogFile().logSuiteName(suiteName);
  }
  
  // #######################################
  // #######################################
  // KEYBOARD
  // #######################################
  // #######################################

  public void keyboard_Press(char key){
  	getRobot().keyPress((int) key);
  }
  
  public void keyboard_Release(char key){
  	getRobot().keyRelease((int) key);
  }
  
  public void keyboard_Type(String str){
  	Robot robot = getRobot();
  	for(int i=0; i<str.length(); i++){
  		char c   = (char) str.charAt(i);
  		int  key = (int) c; 
  		if(Character.isUpperCase(c)){
  			robot.keyPress(KeyEvent.VK_SHIFT);
  		}
  		if(Character.isLowerCase(c)){
  			key = Character.toUpperCase(c);
  		}
  		robot.keyPress(key);
  		robot.keyRelease(key);
  		if(Character.isUpperCase(c)){
  			robot.keyRelease(KeyEvent.VK_SHIFT);
  		}  		
  	}
  }
  
  public void keyboard_TypeEnter(){
  	Robot robot = getRobot();
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
  }

  // #######################################
  // #######################################
  // MOUSE
  // #######################################
  // #######################################
  
  public void mouse_Move(int x, int y){
  	getRobot().mouseMove(x, y);
  }

  public void mouse_LeftPress(){
  	getRobot().mousePress(InputEvent.BUTTON1_MASK);
  }

  public void mouse_LeftRelease(){
  	getRobot().mouseRelease(InputEvent.BUTTON1_MASK);
  }

  public void mouse_LeftClick(){
  	mouse_LeftPress();
  	mouse_LeftRelease();
  }

  public void mouse_RightPress(){
  	getRobot().mousePress(InputEvent.BUTTON3_MASK);
  }

  public void mouse_RightRelease(){
  	getRobot().mouseRelease(InputEvent.BUTTON3_MASK);
  }

  public void mouse_RightClick(){
  	mouse_RightPress();
  	mouse_RightRelease();
  }
  
  // #######################################
  // #######################################
  // FORMULA
  // #######################################
  // #######################################

  public void formula_ApplyFormulaToProperty(AbstractFormulaEnvironment env, FProperty property, String expression){
  	env.applyFormulaToProperty(property, expression);
  }
}

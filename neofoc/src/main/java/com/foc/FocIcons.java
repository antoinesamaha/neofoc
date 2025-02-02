/*
 * Created on 08-Feb-2005
 */
package com.foc;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * @author Administrator
 */
public class FocIcons {
  private static String focIconDirectory = "icons/foc/";
  private static String appIconDirectory = "icons/app/";

  private ImageIcon editIcon = null;
  private ImageIcon saveIcon = null;
  private ImageIcon selectIcon = null;
  private ImageIcon exitIcon = null;
  private ImageIcon deleteIcon = null;
  private ImageIcon insertIcon = null;
  private ImageIcon backIcon = null;
  private ImageIcon nextIcon = null;
  private ImageIcon logoIcon = null;  
  private ImageIcon copyIcon = null;  
  private ImageIcon warningIcon = null;
  private ImageIcon upArrowIcon = null;
  private ImageIcon downArrowIcon = null;  
  private ImageIcon loginIcon = null;  
  private ImageIcon lampOnIcon = null;
  private ImageIcon lampOffIcon = null;
  private ImageIcon filterIcon = null;
  private ImageIcon redirectIcon = null;
  private ImageIcon columnSelectorIcon = null;
  private ImageIcon printIcon = null;
  private ImageIcon expandAllIcon = null;
  private ImageIcon collapseAllIcon = null;
  private ImageIcon equalsIcon = null;
  private ImageIcon moveUpIcon = null;
  private ImageIcon moveDownIcon = null;
  private ImageIcon workflowLogIcon = null;
  private ImageIcon importIcon      = null;
  private ImageIcon exportIcon      = null;
  
  private BufferedImage backgroundImage = null;
  private BufferedImage loginPageImage  = null;
  private BufferedImage clientLogoImage = null;
  
  private Map<String, ImageIcon> appIcons = null;
  
  public final static Dimension CLIENT_LOGO_MAX_DIMENSION = new Dimension(330, 270);
  public final static Point     CLIENT_LOGO_MAX_TOP_LEFT  = new Point(290, 60);
  
  private ImageIcon getAppIcon(String imageName, boolean foc){
  	ImageIcon icon = null;
  	if(appIcons == null){
  		appIcons = new HashMap<String, ImageIcon>();
  	}
		icon = appIcons.get(imageName);
		if(icon == null){
			String appFocDir = foc ? focIconDirectory : appIconDirectory;
			icon = newImageIcon(appFocDir + imageName);
			appIcons.put(imageName, icon);
		}
		return icon;
  }

  public ImageIcon getAppIcon(String imageName){
  	return getAppIcon(imageName, false);
  }

  public ImageIcon getFocIcon(String imageName){
  	return getAppIcon(imageName, true);
  }

  public ImageIcon getEditIcon() {
    if (editIcon == null) {
      //editIcon = new ImageIcon(iconDirectory + "insp_sbook.gif");
      //editIcon = new ImageIcon(focIconDirectory + "address_book3.gif");
      editIcon = newImageIcon(focIconDirectory + "edit.gif");
    }
    return editIcon;
  }

  public ImageIcon getLoginIcon() {
    if (loginIcon == null) {
      loginIcon = newImageIcon(focIconDirectory + "login.gif");
    }
    return loginIcon;
  }
  
  public ImageIcon getWarningIcon() {
    if (warningIcon == null) {
      warningIcon = newImageIcon(focIconDirectory + "cross.gif");
    }
    return warningIcon;
  }
  
  public ImageIcon getCopyIcon() {
    if (copyIcon == null) {
      //copyIcon = new ImageIcon(focIconDirectory + "copy16.gif");
      copyIcon = newImageIcon(focIconDirectory + "duplicate.gif");
    }
    return copyIcon;
  }
  
  public ImageIcon getPrintIcon() {
    if (printIcon == null) {
      printIcon = newImageIcon(focIconDirectory + "print.gif");
    }
    return printIcon;
  }
  
  public ImageIcon getSaveIcon() {
    if (saveIcon == null) {
      saveIcon = newImageIcon(focIconDirectory + "save_edit.gif");
    }
    return saveIcon;
  }

  public ImageIcon getSelectIcon() {
    if (selectIcon == null) {
      selectIcon = newImageIcon(focIconDirectory + "insp_sbook.gif");
    }
    return selectIcon;
  }

  public ImageIcon getExitIcon() {
    if (exitIcon == null) {
      exitIcon = newImageIcon(focIconDirectory + "faillist.gif");
    }
    return exitIcon;
  }

  public ImageIcon getDeleteIcon() {
    if (deleteIcon == null) {
      deleteIcon = newImageIcon(focIconDirectory + "delete_edit.gif");
    }
    return deleteIcon;
  }

  public ImageIcon getInsertIcon() {
    if (insertIcon == null) {
      insertIcon = newImageIcon(focIconDirectory + "add_att.gif");
    }
    return insertIcon;
  }
  
  public ImageIcon getRedirectIcon() {
    if (redirectIcon == null) {
      insertIcon = newImageIcon(focIconDirectory + "redirect.png");
    }
    return insertIcon;
  }

  public ImageIcon getBackIcon() {
    if (backIcon == null) {
      backIcon = newImageIcon(focIconDirectory + "back.png");
    }
    return backIcon;
  }

  public ImageIcon getNextIcon() {
    if (nextIcon == null) {
      nextIcon = newImageIcon(focIconDirectory + "next.png");
    }
    return nextIcon;
  }

  public String getBackgroundFileName() {
  	return focIconDirectory + "desktop_background.png";
  }

  public String getFocIconDirectory() {
  	return focIconDirectory ;
  }

  public String getAppIconDirectory() {
  	return appIconDirectory ;
  }

  public BufferedImage getBackgroundImage(){
  	if(backgroundImage == null){
	  	try{
				new File(getBackgroundFileName());
			  //ClassLoader cl = ClassLoader.getSystemClassLoader();
			  /*backgroundImage = ImageIO.read(cl.getResource(imageFile.getPath()));*/
        backgroundImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource(getBackgroundFileName()));
      }catch(Exception e){
	  		Globals.logException(e);
	  	}
  	}
	  return backgroundImage;
  }
  
  public Point getTopLeftPointCenteredInBackground(Dimension dim){
  	Point pt = null;
  	if(dim != null){
	  	dim.setSize(Math.min(dim.getWidth(), FocIcons.CLIENT_LOGO_MAX_DIMENSION.width), Math.min(dim.getHeight(), FocIcons.CLIENT_LOGO_MAX_DIMENSION.height));
	  	pt = new Point(FocIcons.CLIENT_LOGO_MAX_TOP_LEFT.x + (FocIcons.CLIENT_LOGO_MAX_DIMENSION.width / 2) - (dim.width/2), FocIcons.CLIENT_LOGO_MAX_TOP_LEFT.y + (FocIcons.CLIENT_LOGO_MAX_DIMENSION.height / 2) - (dim.height/2));
  	}
  	return pt;
  }

  public String getLoginPageLogo() {
  	return appIconDirectory + "main_page.png";
  }
  
  public BufferedImage getLoginPageImage(){
  	if(loginPageImage == null){
	  	try{
				new File(getLoginPageLogo());
        URL url = Thread.currentThread().getContextClassLoader().getResource(getLoginPageLogo());
        if( url != null ){
          loginPageImage = ImageIO.read(url);  
        }
	  	}catch(Exception e){
	  		Globals.logException(e);
	  	}
  	}
	  return loginPageImage;
  }

  public String getClientLogo() {
  	return appIconDirectory + "client_logo.png";
  }
  
  public BufferedImage getClientLogoImage(){
  	if(clientLogoImage == null){
	  	try{
				new File(getClientLogo());
        URL url = Thread.currentThread().getContextClassLoader().getResource(getClientLogo());
        if( url != null ){
        	clientLogoImage = ImageIO.read(url);  
        }
	  	}catch(Exception e){
	  		Globals.logException(e);
	  	}
  	}
	  return clientLogoImage;
  }

  /*public ImageIcon newImageIcon(String name){
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    return new ImageIcon(cl.getResource(name));
  }*/
  
  public ImageIcon newImageIcon(String name){
    ImageIcon imageIcon = null;
    URL url = Thread.currentThread().getContextClassLoader().getResource(name);
    if( url != null ){
      imageIcon = new ImageIcon(url);
    }
    return imageIcon;
  }
  
  public ImageIcon getLogoIcon() {
    if (logoIcon == null) {
      logoIcon = getAppIcon("01b-icon.png");
    }
    if (logoIcon == null) {
      logoIcon = newImageIcon(focIconDirectory + "01b-icon.png");
    }
    return logoIcon;
  }

  public ImageIcon getUpArrowIcon() {
    if (upArrowIcon == null) {
      upArrowIcon = newImageIcon(focIconDirectory + "uparrow.gif");
    }
    return upArrowIcon;
  }
  
  public ImageIcon getDownArrowIcon() {
    if (downArrowIcon == null) {
      downArrowIcon = newImageIcon(focIconDirectory + "downarrow.gif");
    }
    return downArrowIcon;
  }

  public ImageIcon getLampOnIcon() {
    if (lampOnIcon == null) {
      lampOnIcon = newImageIcon(focIconDirectory + "lamp_on.gif");
    }
    return lampOnIcon;
  }

  public ImageIcon getLampOffIcon() {
    if (lampOffIcon == null) {
      lampOffIcon = newImageIcon(focIconDirectory + "lamp_off.gif");
    }
    return lampOffIcon;
  }

  public ImageIcon getFilterIcon() {
    if (filterIcon == null) {
      filterIcon = newImageIcon(focIconDirectory + "filter.gif");
    }
    return filterIcon;
  }
  
  public ImageIcon getColumnSelectorIcon() {
    if (columnSelectorIcon == null) {
      columnSelectorIcon = newImageIcon(focIconDirectory + "select_showtsk_tsk.gif");
    }
    return columnSelectorIcon;
  }
  
  public ImageIcon getExpandAllIcon() {
    if (expandAllIcon == null) {
    	expandAllIcon = newImageIcon(focIconDirectory + "expand.png");
    }
    return expandAllIcon;
  }
  
  public ImageIcon getCollapseAllIcon() {
    if (collapseAllIcon == null) {
    	collapseAllIcon = newImageIcon(focIconDirectory + "collapse.png");
    }
    return collapseAllIcon;
  }
  
  public ImageIcon getEqualsIcon() {
    if (equalsIcon == null) {
    	equalsIcon = newImageIcon(focIconDirectory + "equals.GIF");
    }
    return equalsIcon;
  }
  
  public ImageIcon getMoveUpIcon() {
    if (moveUpIcon == null) {
      moveUpIcon = newImageIcon(focIconDirectory + "uparrow.GIF");
    }
    return moveUpIcon;
  }
  
  public ImageIcon getMoveDownIcon() {
    if (moveDownIcon == null) {
      moveDownIcon = newImageIcon(focIconDirectory + "downarrow.GIF");
    }
    return moveDownIcon;
  }
  
  public ImageIcon getWorkflowLogIcon() {
    if (workflowLogIcon == null) {
    	workflowLogIcon = newImageIcon(focIconDirectory + "statusSignatures.gif");
    }
    return workflowLogIcon;
  }
  
  public ImageIcon getImportIcon() {
    if (importIcon == null) {
    	importIcon = newImageIcon(focIconDirectory + "import.png");
    }
    return importIcon;
  }

  public ImageIcon getExportIcon() {
    if (exportIcon == null) {
    	exportIcon = newImageIcon(focIconDirectory + "export.png");
    }
    return exportIcon;
  }
}

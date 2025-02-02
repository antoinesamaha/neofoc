/*
 * Created on Nov 23, 2005
 */
package com.foc.gui.table.cellControler.renderer;

import java.awt.Color;
import java.awt.Component;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.foc.Globals;
import com.foc.gui.DisplayManager;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FColumnHeaderRenderer extends DefaultTableCellRenderer {
  private TableCellRenderer   originalRenderer = null;
  private static JTextPane    textArea         = null;
  private Color               backgroundColor  = null;
 
  public FColumnHeaderRenderer(TableCellRenderer renderer){
    super();
    if(textArea == null){
	    textArea = new JTextPane();//JTextPane
			StyledDocument doc = (StyledDocument) textArea.getDocument();
	 		
			//  Set alignment to be centered for all paragraphs
			MutableAttributeSet standard = new SimpleAttributeSet();
			StyleConstants.setFontFamily(standard, Globals.getDisplayManager().getDefaultFont().getFamily());
			StyleConstants.setFontSize(standard, Globals.getDisplayManager().getDefaultFont().getSize());
			StyleConstants.setBold(standard, false);
			StyleConstants.setAlignment(standard, StyleConstants.ALIGN_CENTER);
			StyleConstants.setForeground(standard, Globals.getDisplayManager().getColumnTitleForeground());
			doc.setParagraphAttributes(0, 0, standard, true);

			Color selFG = textArea.getSelectedTextColor();
			Color selBG = textArea.getSelectionColor();
			textArea.setUI(new javax.swing.plaf.basic.BasicEditorPaneUI());
			textArea.setSelectedTextColor(selFG);
			textArea.setSelectionColor(selBG);
	    
			//  Define a keyword attribute
			/*
			MutableAttributeSet keyWord = new SimpleAttributeSet();
			StyleConstants.setForeground(keyWord, Color.black);
			StyleConstants.setItalic(keyWord, true);
			*/
    }
    
    backgroundColor = Globals.getDisplayManager().getColumnTitleBackground();
    
    this.originalRenderer = renderer;    
  }
  
  public FColumnHeaderRenderer(TableCellRenderer renderer,Color backGroundColor){
  	this(renderer);
  	if(backGroundColor != null){
  		setBackGroundColor(backGroundColor);
  	}
  }
  
  public void dispose(){
    originalRenderer = null;
  }
  
  public void setBackGroundColor(Color backGroundColor){
  	this.backgroundColor = backGroundColor;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component comp = null;
    
    if(value instanceof String){
	    String strValue = (String) value;
	    strValue = strValue.replace("|", "\n");
	    textArea.setText(strValue);
	    comp = textArea;
    }else{
	    if(originalRenderer != null){
	      comp = originalRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    }else{
	      comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);      
	    }
    }
    comp.setFont(Globals.getDisplayManager().getDefaultFont());
    //comp.setBackground(Color.ORANGE);
    comp.setBackground(backgroundColor);
    //comp.setForeground(Color.WHITE);
    JComponent text = (JComponent) comp;
    text.setBorder(BorderFactory.createLineBorder(DisplayManager.TABLE_HEADER_BORDER_COLOR));
    if(text instanceof JLabel) {
      JLabel label = (JLabel) comp;
      label.setHorizontalAlignment(JLabel.CENTER);
    }
    return comp;
  }

  /*
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component comp = null; 
    if(originalRenderer != null){
      comp = originalRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }else{
      comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);      
    }
    comp.setFont(Globals.getDisplayManager().getDefaultFont());
    //comp.setBackground(Color.ORANGE);
    comp.setBackground(backgroundColor);
    JComponent text = (JComponent) comp;
    text.setBorder(BorderFactory.createLineBorder(Color.GRAY));    
    if(text instanceof JLabel) {
      JLabel label = (JLabel) comp;
      label.setHorizontalAlignment(JLabel.CENTER);
    }
    return comp;
  }
  */
}

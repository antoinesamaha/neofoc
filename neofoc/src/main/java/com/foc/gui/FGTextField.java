/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import javax.swing.*;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;

import javax.swing.text.*;

import com.foc.*;
import com.foc.dragNDrop.FocDefaultDropTargetListener;
import com.foc.dragNDrop.FocDragGestureListener;
import com.foc.dragNDrop.FocDragable;
import com.foc.dragNDrop.FocDropTargetListener;
import com.foc.dragNDrop.FocDropable;
import com.foc.dragNDrop.FocTransferable;
import com.foc.dragNDrop.FocTransferableObjectCompleter;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGTextField extends JTextField implements FocusListener, ActionListener, FPropertyListener, FocDragable, FocDropable {

  protected FProperty    objectProperty = null;
  private   TextDocument textDocument = null;
  private   boolean      capital = false;
  private   int          columnsLimit = 0;
  private   boolean      realTimePropertyUpdate = false;
  private   boolean      inUpdatePropertyValue  = false;
  
  private boolean dropable = true;
  private FocTransferableObjectCompleter transferableObjectCompleter = null;
  private DragGestureRecognizer dragGestureRecognizer = null;
  private FocDragGestureListener dragGasturListener = null;
  private FocDropTargetListener dropTargetListener = null;
  
  private void initDrag(){
    disposeDrag();
    DragSource dragSource = DragSource.getDefaultDragSource();
    dragGasturListener = FocDragGestureListener.newFocdragGestureListener();
    dragGestureRecognizer = dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this.dragGasturListener);
  }
  
  private void initDrop(DropTargetListener dropTargetListener){
    disposeDrop();
    this.dropable = true;
    this.dropTargetListener = (FocDropTargetListener)dropTargetListener;
    new DropTarget(this, DnDConstants.ACTION_COPY, this.dropTargetListener, true);
  }
  
  private void disposeDrag(){
    if(dragGasturListener != null){
      dragGasturListener = null;
    }
    if(dragGestureRecognizer != null){
      dragGestureRecognizer = null;
    }
  }
  
  private void disposeDrop(){
    dropTargetListener = null;
  }

  public FGTextField(boolean dropable){
    objectProperty = null;
    if(Globals.getDisplayManager() != null){
	    setFont(Globals.getDisplayManager().getDefaultFont());
	    setDisabledTextColor(Globals.getDisplayManager().getDisabledTextColor());
    }
    addFocusListener(this);
    addActionListener(this);
    initDrag();
    setDropable(dropable);
    
    getPopupMenu();
  }
  
  public FGTextField(){
    this(true);
  }
  
  public void dispose(){
    if(objectProperty != null){
      objectProperty.removeListener(this);
      objectProperty = null;
    }
    removeActionListener(this);
    removeFocusListener(this);
  }
  
  public void setEnabled(boolean b) {
    super.setEditable(b);
    StaticComponent.setEnabled(this, b, objectProperty != null ? objectProperty.isOutput() : false);
  }
  
  public void setProperty(FProperty prop){
    if(prop != objectProperty){
      if(objectProperty != null){
        objectProperty.removeListener(this);
      }
      objectProperty = prop;
      setText(objectProperty.getString());
      objectProperty.addListener(this);
    }
  }
  
  public void setCapital(boolean capital) {
    this.capital = capital;
  }

  public void setColumnsLimit(int columnsLimit) {
    this.columnsLimit = columnsLimit;
  }

  public boolean getCapital() {
    return capital;
  }

  public int getColumnsLimit() {
    return columnsLimit;
  }

  private void updateObjectPropertyValue() {
  	if(!isInUpdatePropertyValue()){
  		setInUpdatePropertyValue(true);
	    try {
	      if (objectProperty != null) {
	        objectProperty.setString(getText());
	      }
	    } catch (Exception exception) {
	      Globals.logException(exception);
	    }
	    setInUpdatePropertyValue(false);
  	}
  }

  // FocusListener
  // -------------
  public void focusGained(FocusEvent e) {
    SwingUtilities.invokeLater(new Runnable(){     
      public void run() {
      	if(!isRealTimePropertyUpdate()){
      		selectAll();
      	}
      }  
    });
  }

  public void focusLost(FocusEvent e) {
    updateObjectPropertyValue();
  }

  // -------------

  // ActionListener
  // --------------
  public void actionPerformed(ActionEvent e) {
    updateObjectPropertyValue();
  }

  // --------------

  // PropertyListener
  // ----------------
  public void propertyModified(FProperty property) {
    if (objectProperty != null) {
      setText(objectProperty.getString());
    }
  }

  // ----------------

  protected Document createDefaultModel() {
    textDocument = new TextDocument(this);
    return textDocument;
  }

	static class TextDocument extends PlainDocument {
    FGTextField fTextFld = null;

    public TextDocument(FGTextField fTextFld) {
      this.fTextFld = fTextFld;
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
    	super.remove(offs, len);
      if(fTextFld.isRealTimePropertyUpdate()){
      	fTextFld.updateObjectPropertyValue();
      }
    }
    
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      if (str != null && fTextFld != null) {
        char[] newStr = str.toCharArray();
        int newOffs = offs;
        int newLength = newStr.length;

        if (fTextFld.getCapital() || (fTextFld.getColumnsLimit() > 0 && fTextFld.getColumnsLimit() < newStr.length + offs)) {
          for (int i = 0; i < newStr.length; i++) {
            if (i + offs >= fTextFld.getColumnsLimit()) {
              newLength--;
            } else if (fTextFld.getCapital()) {
              newStr[i] = Character.toUpperCase(newStr[i]);
            }
          }
        }
        super.insertString(newOffs, new String(newStr, 0, newLength), a);
        if(fTextFld.isRealTimePropertyUpdate()){
        	fTextFld.updateObjectPropertyValue();
        }
      }
    }

		@Override
		protected void insertUpdate(DefaultDocumentEvent arg0, AttributeSet arg1) {
			super.insertUpdate(arg0, arg1);
      if(fTextFld.isRealTimePropertyUpdate()){
      	SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fTextFld.updateObjectPropertyValue();		
					}
				});
      }
		}

		@Override
		protected void removeUpdate(DefaultDocumentEvent arg0) {
			super.removeUpdate(arg0);
      if(fTextFld.isRealTimePropertyUpdate()){
      	SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fTextFld.updateObjectPropertyValue();		
					}
				});
      }
		}
  }

  /**
   * @return
   */
  public FProperty getObjectProperty() {
    return objectProperty;
  }

  /**
   * @param property
   */
  public void setObjectProperty(FProperty property) {
    objectProperty = property;
  }
  
  protected int getColumnWidth(){
    return super.getColumnWidth();
    /*
    int width = super.getColumnWidth();
    Graphics2D g2d = (Graphics2D)getGraphics();
    if(g2d != null){
      Rectangle2D rect2D = getFont().getStringBounds("1", g2d.getFontRenderContext());
      width = (int)rect2D.getWidth();
      Globals.setCharDimensions(width, (int)rect2D.getHeight());
    }
    return width;
    */
  }
  
  /*public Dimension getPreferredSize() {
    Dimension size = super.getPreferredSize();
    String str = "";
    if( getText() != null && getText().length() > 0 ){
      str = getText();
    }else{
      for( int i = 0; i < getColumnsLimit(); i++){
        str+="m";
      }  
    }
    FontMetrics metrics = getFontMetrics(getFont());
    int width = metrics.stringWidth(str);
    size.width = width;
    return size;
  }*/
  
	public boolean isRealTimePropertyUpdate() {
		return realTimePropertyUpdate;
	}

	public void setRealTimePropertyUpdate(boolean realTimePropertyUpdate) {
		this.realTimePropertyUpdate = realTimePropertyUpdate;
	}

  //Dragable
  public void setTransferableObjectCompleter(FocTransferableObjectCompleter transferableObjectCompleter){
    this.transferableObjectCompleter = transferableObjectCompleter;
  }

  public void fillTransferableObject(FocTransferable focTransferable) {
    FGTextField textField = (FGTextField)focTransferable.getSourceComponent();
    FProperty sourceProperty = textField.getObjectProperty();
    focTransferable.setSourceProperty(sourceProperty);
    if(sourceProperty != null){
      focTransferable.setSourceFocObject(sourceProperty.getFocObject());
    }
    if(this.transferableObjectCompleter != null){
      this.transferableObjectCompleter.completeFillingTransferableObject(focTransferable);
    }
  }
  
  //Dropable
  public boolean isDropable(){
    return dropable;
  }
  
  public void setDropable(boolean dropable){
    this.dropable = dropable;
    if(dropable){
      FocDefaultDropTargetListener defaultDropTargetListener = FocDefaultDropTargetListener.getInstance();
      setDropable(defaultDropTargetListener);
    }
  }
  
  public void setDropable(FocDropTargetListener dropTargetListener){
    initDrop(dropTargetListener);
  }
  
  public void fillDropInfo(FocTransferable focTransferable, DropTargetDropEvent dtde){
  	
  }
  
  public boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
  	return true;
	}
  
  public boolean drop(FocTransferable focTransferable, DropTargetDropEvent dtde){
    boolean accepted = false;
    if(isDropable()){
      try{
        /*Transferable transferable = dtde.getTransferable();
        FocTransferable focTransferable = (FocTransferable)transferable.getTransferData(FocTransferable.getFocDataFlavor());*/
        FocDragable sourceComponent = focTransferable.getSourceComponent();
        if(sourceComponent instanceof FGTextField){
          FGTextField sourceTextField = (FGTextField)focTransferable.getSourceComponent();
          if(sourceTextField != this){
            this.setText(sourceTextField.getText());
            accepted = true;
          }
        }else if (sourceComponent instanceof FGNumField){
          FGNumField sourceTextField = (FGNumField)focTransferable.getSourceComponent();
          this.setText(sourceTextField.getText());
          accepted = true;
        }
      }catch(Exception e){
        Globals.logException(e);
      }
    }
    return accepted;
  
    
  }

	/**
	 * @return the inUpdatePropertyValue
	 */
	public boolean isInUpdatePropertyValue() {
		return inUpdatePropertyValue;
	}

	/**
	 * @param inUpdatePropertyValue the inUpdatePropertyValue to set
	 */
	public void setInUpdatePropertyValue(boolean inUpdatePropertyValue) {
		this.inUpdatePropertyValue = inUpdatePropertyValue;
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.JTextComponent#setText(java.lang.String)
	 */
	@Override
	public void setText(String t) {
		super.setText(t);
		/*
		FontMetrics fm = getFontMetrics(getFont()); 
		int textwidth = fm.stringWidth(t);
		if(textwidth != getPreferredSize().width){
			setPreferredSize(new Dimension(textwidth, getPreferredSize().height));
		}
		*/
	}
	
	private FPopupMenu popupMenu = null;
  public FPopupMenu getPopupMenu(){
    if(popupMenu == null){
      popupMenu = new FPopupMenu();
      
      MouseListener mouseListener = new MouseAdapter(){
        public void mousePressed(MouseEvent e) {
          maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
          maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
          if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
          }
        }
      };
      
      addMouseListener(mouseListener);
      
      JMenuItem menuItem = new JMenuItem("Insert ⁰");
      popupMenu.add(menuItem);
      menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FGTextField.this.setText(FGTextField.this.getText() + "⁰");
				}
			});
    }
    return popupMenu;
  }
}

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
package com.foc.gui;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import com.foc.Globals;
import com.foc.dragNDrop.FocDefaultDropTargetListener;
import com.foc.dragNDrop.FocDragGestureListener;
import com.foc.dragNDrop.FocDragable;
import com.foc.dragNDrop.FocDropTargetListener;
import com.foc.dragNDrop.FocDropable;
import com.foc.dragNDrop.FocTransferable;
import com.foc.dragNDrop.FocTransferableObjectCompleter;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

@SuppressWarnings("serial")
public class FGTextArea extends JTextArea implements FocusListener, FPropertyListener, FocDragable, FocDropable {
  
  private FProperty    objectProperty = null;
  private TextDocument textDocument = null;
  private boolean      capital = false;
  private int          columnsLimit = 0;
  
  private boolean dropable = true;
  private FocTransferableObjectCompleter transferableObjectCompleter = null;
  private DragGestureRecognizer  dragGestureRecognizer = null;
  private FocDragGestureListener dragGasturListener = null;
  private FocDropTargetListener  dropTargetListener = null;
  
  public FGTextArea(){
    this(true);
  }

  public FGTextArea(boolean dropable){
    objectProperty = null;
    if(Globals.getDisplayManager() != null){
	    setFont(Globals.getDisplayManager().getDefaultFont());
	    setDisabledTextColor(Globals.getDisplayManager().getDisabledTextColor());
    }
    addFocusListener(this);
    //addActionListener(this);
    initDrag();
    setDropable(dropable);
    //setBackground(Color.YELLOW);
  }
  
  public void dispose(){
    if(objectProperty != null){
      objectProperty.removeListener(this);
      objectProperty = null;
    }
    //removeActionListener(this);
    removeFocusListener(this);
    disposeDrop();
    disposeDrag();
  }
  
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
  
  public void setEnabled(boolean b) {
    super.setEditable(b);
    StaticComponent.setEnabled(this, b, objectProperty != null ? objectProperty.isOutput() : false);
  }
  
  public void setText(String t) {
  	super.setText(t);
  }
  
  public void setProperty(FProperty prop){
    if(prop != objectProperty){
      if(objectProperty != null){
        objectProperty.removeListener(this);
      }
      objectProperty = prop;
      if(objectProperty != null){
	      setText(objectProperty.getString());
	      objectProperty.addListener(this);
	      if(prop != null){
	        setEnabled(!prop.isValueLocked());
	      }
      }
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

  public void updateObjectPropertyValue() {
    try {
      if (objectProperty != null) {
        objectProperty.setString(getText());
      }
    } catch (Exception exception) {
      Globals.logException(exception);
    }
  }

  // FocusListener
  // -------------
  public void focusGained(FocusEvent e) {
  }

  public void focusLost(FocusEvent e) {
    updateObjectPropertyValue();
  }

  // -------------

  // ActionListener
  // --------------
  /*public void actionPerformed(ActionEvent e) {
    updateObjectPropertyValue();
  }*/

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
    FGTextArea fTextArea = null;

    public TextDocument(FGTextArea fTextArea) {
      this.fTextArea = fTextArea;
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      if (str != null && fTextArea != null) {
        char[] newStr = str.toCharArray();
        int newOffs = offs;
        int newLength = newStr.length;

        if (fTextArea.getCapital() || (fTextArea.getColumnsLimit() > 0 && fTextArea.getColumnsLimit() < newStr.length + offs)) {
          for (int i = 0; i < newStr.length; i++) {
            if (i + offs >= fTextArea.getColumnsLimit()) {
              newLength--;
            } else if (fTextArea.getCapital()) {
              newStr[i] = Character.toUpperCase(newStr[i]);
            }
          }
        }
        super.insertString(newOffs, new String(newStr, 0, newLength), a);
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
  }
  
  //Dragable
  public void setTransferableObjectCompleter(FocTransferableObjectCompleter transferableObjectCompleter){
    this.transferableObjectCompleter = transferableObjectCompleter;
  }

  public void fillTransferableObject(FocTransferable focTransferable) {
    FProperty sourceProperty = null;
    if( focTransferable.getSourceComponent() instanceof FGTextField ){
      FGTextField textField = (FGTextField)focTransferable.getSourceComponent();
      sourceProperty = textField.getObjectProperty();
    }else if(focTransferable.getSourceComponent() instanceof FGTextArea){
      FGTextArea textArea = (FGTextArea)focTransferable.getSourceComponent();
      sourceProperty = textArea.getObjectProperty();
    }
    
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
        FocDragable sourceComponent = focTransferable.getSourceComponent();
        
        if(sourceComponent instanceof FGTextArea){
          FGTextArea sourceTextArea = (FGTextArea)focTransferable.getSourceComponent();
          if(sourceTextArea != this){
            this.setText(sourceTextArea.getText());
            accepted = true;
          }
        }else if(sourceComponent instanceof FGTextField){
          FGTextField sourceTextField = (FGTextField)focTransferable.getSourceComponent();
          this.setText(sourceTextField.getText());
          accepted = true;
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
  
}

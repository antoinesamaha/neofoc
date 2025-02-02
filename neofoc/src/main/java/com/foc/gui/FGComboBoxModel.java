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

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

public class FGComboBoxModel extends DefaultComboBoxModel {
  Vector fullObjects;

  /**
   * Constructs an empty DefaultComboBoxModel object.
   */
  public FGComboBoxModel() {
      super();
  }

  /**
   * Constructs a DefaultComboBoxModel object initialized with
   * an array of objects.
   *
   * @param items  an array of Object objects
   */
  public FGComboBoxModel(final Object items[]) {
  	super(items);
  }

  /**
   * Constructs a DefaultComboBoxModel object initialized with
   * a vector.
   *
   * @param v  a Vector object ...
   */
  
//  public DefaultComboBoxModel(Vector<?> v) {
//      objects = v;
//
//      if ( getSize() > 0 ) {
//          selectedObject = getElementAt( 0 );
//      }
//  }

  // implements javax.swing.ComboBoxModel
  /**
   * Set the value of the selected item. The selected item may be null.
   * <p>
   * @param anObject The combo box value or null for no selection.
   */
//  public void setSelectedItem(Object anObject) {
//      if ((selectedObject != null && !selectedObject.equals( anObject )) ||
//    selectedObject == null && anObject != null) {
//    selectedObject = anObject;
//    fireContentsChanged(this, -1, -1);
//      }
//  }

  // implements javax.swing.ComboBoxModel
//  public Object getSelectedItem() {
//      return selectedObject;
//  }

  // implements javax.swing.ListModel
//  public int getSize() {
//      return objects.size();
//  }

  // implements javax.swing.ListModel
//  public Object getElementAt(int index) {
//      if ( index >= 0 && index < objects.size() )
//          return objects.elementAt(index);
//      else
//          return null;
//  }

  /**
   * Returns the index-position of the specified object in the list.
   *
   * @param anObject  
   * @return an int representing the index position, where 0 is 
   *         the first position
   */
//  public int getIndexOf(Object anObject) {
//      return objects.indexOf(anObject);
//  }
//
//  // implements javax.swing.MutableComboBoxModel
//  public void addElement(Object anObject) {
//      objects.addElement(anObject);
//      fireIntervalAdded(this,objects.size()-1, objects.size()-1);
//      if ( objects.size() == 1 && selectedObject == null && anObject != null ) {
//          setSelectedItem( anObject );
//      }
//  }

//  // implements javax.swing.MutableComboBoxModel
//  public void insertElementAt(Object anObject,int index) {
//      objects.insertElementAt(anObject,index);
//      fireIntervalAdded(this, index, index);
//  }
//
//  // implements javax.swing.MutableComboBoxModel
//  public void removeElementAt(int index) {
//      if ( getElementAt( index ) == selectedObject ) {
//          if ( index == 0 ) {
//              setSelectedItem( getSize() == 1 ? null : getElementAt( index + 1 ) );
//          }
//          else {
//              setSelectedItem( getElementAt( index - 1 ) );
//          }
//      }
//
//      objects.removeElementAt(index);
//
//      fireIntervalRemoved(this, index, index);
//  }
//
//  // implements javax.swing.MutableComboBoxModel
//  public void removeElement(Object anObject) {
//      int index = objects.indexOf(anObject);
//      if ( index != -1 ) {
//          removeElementAt(index);
//      }
//  }
//
//  /**
//   * Empties the list.
//   */
//  public void removeAllElements() {
//  	super.remo 
//      if ( objects.size() > 0 ) {
//          int firstIndex = 0;
//          int lastIndex = objects.size() - 1;
//          objects.removeAllElements();
//    selectedObject = null;
//          fireIntervalRemoved(this, firstIndex, lastIndex);
//      } else {
//    selectedObject = null;
//      }
//  }

}

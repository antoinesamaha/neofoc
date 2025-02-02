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
package com.foc.gui.tree;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

public abstract class FAbstractTreeModel implements TreeModel{
  private ArrayList<TreeModelListener> vector = new ArrayList<TreeModelListener>();

  public void addTreeModelListener( TreeModelListener listener ) {
     if ( listener != null && !vector.contains( listener ) ) {
        vector.add( listener );
     }
  }

  public void removeTreeModelListener( TreeModelListener listener ) {
     if ( listener != null ) {
        vector.remove( listener );
     }
  }

  public void fireTreeNodesChanged( TreeModelEvent e ) {
     Iterator listeners = (Iterator) vector.iterator();
     while ( listeners.hasNext()) {
        TreeModelListener listener = (TreeModelListener)listeners.next();
        listener.treeNodesChanged( e );
     }
  }

  public void fireTreeNodesInserted( TreeModelEvent e ) {
     Iterator listeners = vector.iterator();
     while ( listeners.hasNext() ) {
        TreeModelListener listener = (TreeModelListener)listeners.next();
        listener.treeNodesInserted( e );
     }
  }

  public void fireTreeNodesRemoved( TreeModelEvent e ) {
     Iterator listeners = vector.iterator();
     while ( listeners.hasNext() ) {
        TreeModelListener listener = (TreeModelListener)listeners.next();
        listener.treeNodesRemoved( e );
     }
  }

  public void fireTreeStructureChanged( TreeModelEvent e ) {
     Iterator listeners = vector.iterator();
     while ( listeners.hasNext() ) {
        TreeModelListener listener = (TreeModelListener)listeners.next();
        listener.treeStructureChanged( e );
     }
  }
}

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
package com.foc.gui.table;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class MyMouseInputHandler implements MouseInputListener {

  protected int mouseXOffset;
  protected Cursor otherCursor;
  protected int columnOffset;
  protected int width;
  private JTableHeader header = null;

  public MyMouseInputHandler(JTableHeader header) {
    super();
    this.header = header;
    //otherCursor = BasicColumnGroupTableHeaderUI.resizeCursor;
    columnOffset = 0;
    width = 0;
  }
  
  public void mouseClicked(MouseEvent mouseevent) {
  }

//  protected boolean canResize(TableColumn column) {
//    return column != null && header.getResizingAllowed() && column.getResizable();
//  }
//
//  protected TableColumn getResizingColumn(Point p) {
//    return getResizingColumn(p, header.columnAtPoint(p));
//  }
/*
  protected TableColumn getResizingColumn(Point p, int column) {
    if (column == -1)
      return null;
    Rectangle r = header.getHeaderRect(column);
    r.grow(-3, 0);
    if (r.contains(p))
      return null;
    int midPoint = r.x + r.width / 2;
    int columnIndex;
    if (header.getComponentOrientation().isLeftToRight())
      columnIndex = p.x >= midPoint ? column : column - 1;
    else
      columnIndex = p.x >= midPoint ? column - 1 : column;
    if (columnIndex == -1)
      return null;
    else
      return header.getColumnModel().getColumn(columnIndex);
  }
*/
/*  protected void swapCursor() {
    Cursor tmp = header.getCursor();
    header.setCursor(otherCursor);
    otherCursor = tmp;
  }*/

  public void mouseMoved(MouseEvent e) {
/*  
    if (canResize(getResizingColumn(e.getPoint())) != (header.getCursor() == BasicColumnGroupTableHeaderUI.resizeCursor))
      swapCursor();
*/      
  }

  
  public void mousePressed(MouseEvent e) {
    header.setDraggedColumn(null);
    header.setResizingColumn(null);
    header.setDraggedDistance(0);
    Point p = e.getPoint();
    TableColumnModel columnModel = header.getColumnModel();
    int index = header.columnAtPoint(p);
    if (index != -1) {
//      TableColumn resizingColumn = header.getResizingColumn(p, index);
//      if (canResize(resizingColumn)) {
//        header.setResizingColumn(resizingColumn);
//        if (header.getComponentOrientation().isLeftToRight())
//          mouseXOffset = p.x - resizingColumn.getWidth();
//        else
//          mouseXOffset = p.x + resizingColumn.getWidth();
//      } else if (header.getReorderingAllowed()) {
//        TableColumn hitColumn = columnModel.getColumn(index);
//        header.setDraggedColumn(hitColumn);
//        mouseXOffset = p.x;
//      }
    }
  }

  public void mouseDragged(MouseEvent e) {
/*    int mouseX = e.getX();
    boolean canMove = false;
    TableColumn resizingColumn = header.getResizingColumn();
    TableColumn draggedColumn = header.getDraggedColumn();
    boolean headerLeftToRight = header.getComponentOrientation().isLeftToRight();
    if (resizingColumn != null) {
      int oldWidth = resizingColumn.getWidth();
      int newWidth;
      if (headerLeftToRight)
        newWidth = mouseX - mouseXOffset;
      else
        newWidth = mouseXOffset - mouseX;
      resizingColumn.setWidth(newWidth);
      Container container;
      if (header.getParent() == null || (container = header.getParent().getParent()) == null || !(container instanceof JScrollPane))
        return;
      if (!container.getComponentOrientation().isLeftToRight() && !headerLeftToRight) {
        JTable table = header.getTable();
        if (table != null) {
          JViewport viewport = ((JScrollPane) container).getViewport();
          int viewportWidth = viewport.getWidth();
          int diff = newWidth - oldWidth;
          int newHeaderWidth = table.getWidth() + diff;
          Dimension tableSize = table.getSize();
          tableSize.width += diff;
          table.setSize(tableSize);
          if (newHeaderWidth >= viewportWidth && table.getAutoResizeMode() == 0) {
            Point p = viewport.getViewPosition();
            p.x = Math.max(0, Math.min(newHeaderWidth - viewportWidth, p.x + diff));
            viewport.setViewPosition(p);
            mouseXOffset += diff;
          }
        }
      }
    } else if (draggedColumn != null) {
      TableColumnModel cm = header.getColumnModel();
      int draggedDistance = mouseX - mouseXOffset;
      int direction = draggedDistance >= 0 ? 1 : -1;
      int columnIndex = viewIndexForColumn(draggedColumn);
      int newColumnIndex = columnIndex + (headerLeftToRight ? direction : -direction) + columnOffset;
      if (0 <= newColumnIndex && newColumnIndex < cm.getColumnCount()) {
        TableColumn evalColumn = cm.getColumn(newColumnIndex);
        int evalWidth = evalColumn.getWidth();
        if (Math.abs(draggedDistance) > (width + evalWidth) / 2) {
          width += evalWidth;
          if (newColumnIndex < cm.getColumnCount() - 1)
            columnOffset = columnOffset + direction;
          java.util.List draggedGroupList = ((ColumnGroupTableHeader) header).getGroupsForColumn(draggedColumn);
          java.util.List evalGroupList = ((ColumnGroupTableHeader) header).getGroupsForColumn(evalColumn);
          if (draggedGroupList.containsAll(evalGroupList) && evalGroupList.containsAll(draggedGroupList)) {
            mouseXOffset = mouseXOffset + direction * width;
            header.setDraggedDistance(draggedDistance - direction * width);
            cm.moveColumn(columnIndex, newColumnIndex);
            columnOffset = 0;
            width = 0;
          }
          return;
        }
      }
      setDraggedDistance(draggedDistance, columnIndex);
    }
*/  }

  public void mouseReleased(MouseEvent e) {
/*    setDraggedDistance(0, viewIndexForColumn(header.getDraggedColumn()));
    columnOffset = 0;
    width = 0;
    header.setResizingColumn(null);
    header.setDraggedColumn(null);
*/  }

  public void mouseEntered(MouseEvent mouseevent) {
  }

  public void mouseExited(MouseEvent mouseevent) {
  }

  private void setDraggedDistance(int draggedDistance, int column) {
/*    header.setDraggedDistance(draggedDistance);
    if (column != -1)
      header.getColumnModel().moveColumn(column, column);
*/  }
}


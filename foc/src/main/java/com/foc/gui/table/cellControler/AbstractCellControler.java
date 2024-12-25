/*
 * Created on 24-Apr-2005
 */
package com.foc.gui.table.cellControler;

import javax.swing.table.*;

import com.foc.gui.table.*;

import java.text.*;

/**
 * @author 01Barmaja
 */
public abstract class AbstractCellControler {
  public abstract TableCellEditor getEditor();
  public abstract TableCellRenderer getRenderer();
  public abstract TableCellRenderer getColumnHeaderRenderer();
  public abstract int getRendererSupplementSize();
  public abstract void editRequested(FTable table, int row, int col);
  public abstract void dispose();
  
  private Format format = null;
  
  public Format getFormat(){
    return format;
  }
  
  public void setFormat(Format format){
    this.format = format != null ? (Format) format.clone() : null;
  }  
}

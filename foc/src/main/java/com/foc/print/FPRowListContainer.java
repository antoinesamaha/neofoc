/*
 * Created on 29-Apr-2005
 */
package com.foc.print;

import java.awt.*;
import java.util.*;

/**
 * @author 01Barmaja
 */
public class FPRowListContainer extends FPComponent{
  private ArrayList rows = null;
  private int ySpacing = 2; 
  private int xSpacing = 2;
  private boolean rowFill = false;
  
  private BorderSetup defaultBorderSetup = null;
  
  public FPRowListContainer(){
    rows = new ArrayList();
  }
  
  public Iterator getRows(){
    return rows.iterator();
  }
  
  public FPRow newRow(){
    FPRow row = new FPRow();
    row.setXSpacing(xSpacing);
    row.setRowFill(rowFill);
    row.setDefaultBorderSetup(defaultBorderSetup);
    return row;
  }
  
  public FPRow newAddedRow(){
    FPRow row = newRow();
    rows.add(row);
    return row;
  }
  
  protected void addRowDimensions(FPDimension dim, FPRow row, PrintingData data){
    int totalWidth = dim.getWidth();
    int totalHeight = dim.getHeight();
    
    if(row != null){
      row.setXSpacing(xSpacing);
      FPDimension rDim = row.getDimension(data);

      if(rDim.getWidth() > totalWidth) totalWidth = rDim.getWidth();    
      totalHeight += rDim.getHeight() + 2 * ySpacing;
    }
    dim.setWidth(totalWidth);
    dim.setHeight(totalHeight);
  }
  
  //FPComponent
  public FPDimension getDimension(PrintingData data) {
    FPDimension dim = new FPDimension();    
    
    Iterator rIter = (Iterator) getRows();
    while(rIter != null && rIter.hasNext()){
      FPRow row = (FPRow) rIter.next();
      if(row != null){
        addRowDimensions(dim, row, data);
      }
    }
    
    return dim;
  }
  
  protected ComponentPrintInfo printRow(Graphics2D g2, PrintingData data, int pageIndex, FPRow row){
    ComponentPrintInfo compInfo = new ComponentPrintInfo();
    FPDimension rDim = row.getDimension(data);
    translate(g2, 0, getYSpacing());
    data.setTopBorderY(-getYSpacing());
    data.setBottomBorderY(getYSpacing() + rDim.getHeight());
    ComponentPrintInfo cInfo = row.print(g2, data, pageIndex, getXLimit(), getYLimit());
    data.setTopBorderY(0);
    data.setBottomBorderY(0);
    translate(g2, 0, rDim.getHeight() + getYSpacing());
    if(!cInfo.isNeedsMorePages()){
      compInfo.addHeight(getYSpacing() + rDim.getHeight() + getYSpacing());
      compInfo.setWidth(cInfo.getWidth());
    }
    compInfo.setNeedsMorePages(cInfo.isNeedsMorePages());
    return compInfo;
  }
  
  //FPComponent
  public ComponentPrintInfo printInt(Graphics graphics, PrintingData data, int pageIndex, int xLimit, int yLimit) {
    Graphics2D g2 = (Graphics2D) graphics;
    ComponentPrintInfo compInfo = new ComponentPrintInfo();
    
    setXLimit(xLimit);
    setYLimit(yLimit);
    
    Iterator rIter = (Iterator) getRows();
    while(rIter != null && rIter.hasNext()){
      FPRow row = (FPRow) rIter.next();
      if(row != null){
        if(!row.pageExcluded(pageIndex)){
          ComponentPrintInfo cInfo = printRow(g2, data, pageIndex, row);
          compInfo.setNeedsMorePages(cInfo.isNeedsMorePages());
          compInfo.setWidth(cInfo.getWidth());
          compInfo.addHeight(cInfo.getHeight());
          if(compInfo.isNeedsMorePages()){
            break;
          }else{
            includePage(pageIndex);
          }
        }
      }
    }
        
    this.resetTranslation(g2);
    return compInfo;
  }
  
  /**
   * @return Returns the xSpacing.
   */
  public int getXSpacing() {
    return xSpacing;
  }
  
  /**
   * @param spacing The xSpacing to set.
   */
  public void setXSpacing(int spacing) {
    xSpacing = spacing;
  }
  
  /**
   * @return Returns the ySpacing.
   */
  public int getYSpacing() {
    return ySpacing;
  }
  
  /**
   * @param spacing The ySpacing to set.
   */
  public void setYSpacing(int spacing) {
    ySpacing = spacing;
  }
  /**
   * @return Returns the rowFill.
   */
  public boolean isRowFill() {
    return rowFill;
  }
  /**
   * @param rowFill The rowFill to set.
   */
  public void setRowFill(boolean rowFill) {
    this.rowFill = rowFill;
  }
  /**
   * @return Returns the defaultBorderSetup.
   */
  public BorderSetup getDefaultBorderSetup() {
    return defaultBorderSetup;
  }
  /**
   * @param defaultBorderSetup The defaultBorderSetup to set.
   */
  public void setDefaultBorderSetup(BorderSetup defaultBorderSetup) {
    this.defaultBorderSetup = defaultBorderSetup;
  }
}
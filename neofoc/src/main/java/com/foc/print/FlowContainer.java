/*
 * Created on 29-Apr-2005
 */
package com.foc.print;

import java.awt.*;
import java.util.*;

/**
 * @author 01Barmaja
 */
public class FlowContainer extends FPComponent{
  private ArrayList compList = null;
  private int ySpacing = 2; 
  
  public FlowContainer(){
    compList = new ArrayList();
  }
  
  public Iterator getComponents(){
    return compList.iterator();
  }

  public void addComponent(FPComponent comp){
    compList.add(comp);
  }
  
  protected void addCompDimensions(FPDimension dim, FPComponent comp, PrintingData data){
    int totalWidth = dim.getWidth();
    int totalHeight = dim.getHeight();
    
    if(comp != null){
      FPDimension rDim = comp.getDimension(data);

      if(rDim.getWidth() > totalWidth) totalWidth = rDim.getWidth();    
      totalHeight += rDim.getHeight() + 2 * ySpacing;
    }
    dim.setWidth(totalWidth);
    dim.setHeight(totalHeight);
  }

  //FPComponent
  public FPDimension getDimension(PrintingData data) {
    FPDimension dim = new FPDimension();    
    
    Iterator rIter = (Iterator) getComponents();
    while(rIter != null && rIter.hasNext()){
      FPComponent row = (FPComponent) rIter.next();
      if(row != null){
        addCompDimensions(dim, row, data);
      }
    }
    
    return dim;
  }  
  
  //FPComponent
  public ComponentPrintInfo printInt(Graphics graphics, PrintingData data, int pageIndex, int xLimit, int yLimit) {
    Graphics2D g2 = (Graphics2D) graphics;
    ComponentPrintInfo compInfo = new ComponentPrintInfo();
    
    setXLimit(xLimit);
    setYLimit(yLimit);
    
    Iterator rIter = (Iterator) getComponents();
    while(rIter != null && rIter.hasNext()){
      FPComponent comp = (FPComponent) rIter.next();
      if(comp != null){
        if(!comp.pageExcluded(pageIndex)){
          
          int yTranslation1 = getYSpacing();
          translate(g2, 0, yTranslation1);
          
          ComponentPrintInfo cInfo = comp.print(g2, data, pageIndex, getXLimit(), getYLimit());
          
          int yTranslation2 = cInfo.getHeight() + getYSpacing();
          translate(g2, 0, yTranslation2);
          
          //Update the ComponentPrintInfo
          compInfo.addHeight(yTranslation1);
          compInfo.addHeight(cInfo.getHeight());
          if(cInfo.getWidth() > compInfo.getWidth()){
            compInfo.setWidth(cInfo.getWidth());
          }
          compInfo.addHeight(yTranslation2);
          compInfo.setNeedsMorePages(cInfo.isNeedsMorePages());          
         
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
}
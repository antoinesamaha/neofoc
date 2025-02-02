/*
 * Created on 29-Apr-2005
 */
package com.foc.print;

import java.awt.*;
import java.util.*;

/**
 * @author 01Barmaja
 */
public class FPTableContainer extends FPRowListContainer{
  private FPRow titles = null;
  private ArrayList minimumColumnsWidths = null;
  
  public FPTableContainer(){
    super();
  }
  
  public FPRow getTitleRow(){
    if(titles == null){
      titles = newRow();
    }
    return titles;
  }
  
  private void getMinForRow(FPRow row, PrintingData data){
    int i = 0;
    Iterator iter = row.getIterator();      
    while(iter != null && iter.hasNext()){
      FPComponent comp = (FPComponent) iter.next();
      
      if(minimumColumnsWidths.size() == i){
      	minimumColumnsWidths.add(Integer.valueOf(0));
      }
      if(comp != null){
        FPDimension dim = comp.getDimension(data);
        int maxWidth = ((Integer)minimumColumnsWidths.get(i)).intValue();
        if(maxWidth < dim.getWidth()){
          maxWidth = dim.getWidth();
        }
        minimumColumnsWidths.set(i, Integer.valueOf(maxWidth));
      }
      i++;
    }
  }
  
  private ArrayList getMinimumColumnsWidths(PrintingData data){
    if(minimumColumnsWidths == null){
      if(titles != null){
        minimumColumnsWidths = new ArrayList(titles.getCount());
        
        getMinForRow(titles, data);
      }
      
      Iterator iter = getRows();      
      while(iter != null && iter.hasNext()){
        FPRow row = (FPRow) iter.next();
        getMinForRow(row, data);      
      }
    }
    return minimumColumnsWidths;
  }
  
  public int getColumnWidth(PrintingData data, int col){
  	int w = 0;
  	if(getMinimumColumnsWidths(data) != null){
  		w = (Integer) minimumColumnsWidths.get(col);
  	}
  	return w;
  }
  
  //FPComponent
  public FPDimension getDimension(PrintingData data) {
    FPDimension dim = super.getDimension(data);
    addRowDimensions(dim, titles, data);
    
    ArrayList minWidths = getMinimumColumnsWidths(data);
    
    int newWidth = 0;
    for(int i=0; i<minWidths.size(); i++){
      newWidth += ((Integer)minWidths.get(i)).intValue() + 2 * getXSpacing();
    }
    dim.setWidth(newWidth);
    return dim;
  }
  
  //FPComponent
  public ComponentPrintInfo printInt(Graphics graphics, PrintingData data, int pageIndex, int xLimit, int yLimit) {
    Graphics2D g2 = (Graphics2D) graphics;
    ComponentPrintInfo compInfo = new ComponentPrintInfo();

    setXLimit(xLimit);
    setYLimit(yLimit);    
    ArrayList minWidths = getMinimumColumnsWidths(data);

    boolean firstRowOnThisPage = true;
    
    Iterator rIter = (Iterator) getRows();
    while(rIter != null && rIter.hasNext()){
      FPRow row = (FPRow) rIter.next();
      if(row != null){
        if(!row.pageExcluded(pageIndex)){
          if(firstRowOnThisPage){
            //To make sure it gets printed on this all pages
            //We need to include this page in the titles row and all the labels conponents
            titles.includePage(pageIndex);
            Iterator titleCells = titles.getIterator();
            while(titleCells != null && titleCells.hasNext()){
              FPComponent titleCell = (FPComponent)titleCells.next();
              if(titleCell != null){
                titleCell.includePage(pageIndex);
              }
            }
            //End of inclusion of all titles
                          
            titles.setMinimumColumnsWidths(minWidths);
            ComponentPrintInfo cInfo = printRow(g2, data, pageIndex, titles);
            compInfo.setWidth(cInfo.getWidth());
            compInfo.addHeight(cInfo.getHeight());
            
            firstRowOnThisPage = false;
          }
          row.setMinimumColumnsWidths(minWidths);        
          ComponentPrintInfo cInfo = printRow(g2, data, pageIndex, row);
          compInfo.setWidth(cInfo.getWidth());
          compInfo.addHeight(cInfo.getHeight());
          compInfo.setNeedsMorePages(cInfo.isNeedsMorePages());
        }
        if(compInfo.isNeedsMorePages()){
          break;
        }else{
          includePage(pageIndex);
        }
      }
    }
  
    this.resetTranslation(g2);
    return compInfo;
  }
 
}
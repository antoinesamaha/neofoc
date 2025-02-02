/*
 * Created on 29-Apr-2005
 */
package com.foc.print;

import java.util.*;
import java.awt.*;

/**
 * @author 01Barmaja
 */
public class FPRow extends FPComponent {
  private ArrayList columns = null;
  private int xSpacing = 0;
  private int rejectedPage = -9;
  private boolean rowFill = false;
  public int debugNbr = 0;
  
  private BorderSetup defaultBorderSetup = null;
  
  //This is only used in a Table container
  private ArrayList minimumColumnsWidths = null;
  
  public FPRow(){
    columns = new ArrayList();
  }

  public void setXSpacing(int sp){
    xSpacing = sp;
  }
  
  public Iterator getIterator(){
    return columns.iterator();
  }
  
  public int getCount(){
    return columns.size();
  }
  
  public void add(FPInterface comp){
    columns.add(comp);
  }
  
  //FPComponent
  public FPDimension getDimension(PrintingData data) {
    FPDimension dim = new FPDimension();

    int rowWidth = 0;
    int rowHeight = 0;
    
    Iterator cIter = getIterator();
    while(cIter != null && cIter.hasNext()){
      FPComponent comp = (FPComponent) cIter.next();
      if(comp != null){
        FPDimension cDim = comp.getDimension(data);

        rowWidth += cDim.getWidth() + 2 * xSpacing;
        if(rowHeight < cDim.getHeight()){
          rowHeight = cDim.getHeight();          
        }
      }
    }
    
    dim.setWidth(rowWidth); 
    dim.setHeight(rowHeight);      

    return dim;
  }
  
  private void addMinColumnWidthsToDimension(FPDimension dim){
    //Pour tenir en compte l'allignement s'il existe, il faut faire ce qui suit:
    //On scan les width alligne on les somme et on ajoute les xSpaces
    ArrayList minWidths = getMinimumColumnsWidths();
    if(minWidths != null){
      int newRowWidth = 0;
      for(int i=0; i<minWidths.size(); i++){
        int w = ((Integer)minWidths.get(i)).intValue();
        newRowWidth += w + 2 * xSpacing;
      }
      dim.setWidth(newRowWidth);
    }
  }
  
  //FPComponent
  public ComponentPrintInfo printInt(Graphics graphics, PrintingData data, int pageIndex, int xLimit, int yLimit) {
    Graphics2D g2 = (Graphics2D) graphics;
    boolean needsMorePages = false;
    FPDimension dim = getDimension(data);//Cette fonction ne tient pas en compte l'allignement des colonnes
    
    //Pour tenir en compte l'allignement s'il existe, il faut faire ce qui suit:
    //On scan les width alligne on les somme et on ajoute les xSpaces
    addMinColumnWidthsToDimension(dim);
    
    if(dim.getWidth() > xLimit){
      //Globals.logString("Report row exceeds x limits! Row will be truncated");
    }
    if(dim.getHeight() > yLimit){     
      if((rejectedPage < 0 || rejectedPage == pageIndex)){
        needsMorePages = true;
        rejectedPage = pageIndex;
      }else{
        //If it is the seccond rejection, this means that the row does not hold on one whole page        
        //Globals.logString("Report row exceeds y limits! Row will not be printed");
      }
    }else{      
      int commonHalfCellExtension = 0;
      if(rowFill && xLimit > dim.getWidth()){
        commonHalfCellExtension = (xLimit - dim.getWidth()) / getCount();
        commonHalfCellExtension = commonHalfCellExtension / 2;
      }
      
      int col = 0;
      Iterator cIter = getIterator();
      while(cIter != null && cIter.hasNext()){
        FPComponent comp = (FPComponent) cIter.next();
        if(comp != null){
          FPDimension cDim = comp.getDimension(data);
          int cellWidth = cDim.getWidth();
          int halfCellExtension = commonHalfCellExtension;
          int correction = 0;
          
          ArrayList minWidths = getMinimumColumnsWidths();
          if(minWidths != null){
            cellWidth = ((Integer)minWidths.get(col)).intValue();
            int ex = (cellWidth - cDim.getWidth()) / 2;
            correction = (cellWidth - cDim.getWidth()) - 2 * ex;
            halfCellExtension += ex; 
          }
          
          int lMargin = 0;
          int rMargin = 0;          
 
          switch(comp.getXAllignment()){
          case FPComponent.CENTER:
            lMargin = halfCellExtension;
            rMargin = halfCellExtension+correction;
            break;
          case FPComponent.LEFT:
            lMargin = 0;
            rMargin = 2 * halfCellExtension + correction;
            break;
          case FPComponent.RIGHT:
            lMargin = 2 * halfCellExtension + correction;
            rMargin = 0;
            break;
          }
          
          int lineWidth = 2 * xSpacing + 2 * commonHalfCellExtension + cellWidth;
          
          if(!data.isDoNotPrint()){
            Color backColor = comp.getBackgroundColor();
            if(backColor != null){
              Color colorBackup = g2.getColor();
              g2.setColor(backColor);
              //g2.fillRect(1, data.getTopBorderY()+1, lineWidth-1, data.getBottomBorderY() - data.getTopBorderY() -1);
              g2.fillRect(0, data.getTopBorderY(), lineWidth, data.getBottomBorderY() - data.getTopBorderY());
              g2.setColor(colorBackup);
            }
          }
          
          BorderSetup borderSetup = comp.getBorderSetup();
          if(borderSetup == null){
            borderSetup = getBorderSetup();
          }          
          if(borderSetup == null){
            borderSetup = getDefaultBorderSetup();
          }
          if(borderSetup != null){
            if(!data.isDoNotPrint()){
              if(borderSetup.isUp()){
                g2.drawLine(0, data.getTopBorderY(), lineWidth, data.getTopBorderY());
              }
              if(borderSetup.isDown()){
                g2.drawLine(0, data.getBottomBorderY(), lineWidth, data.getBottomBorderY());
              }
              if(borderSetup.isLeft()){
                g2.drawLine(0, data.getBottomBorderY(), 0, data.getTopBorderY());
              }
              if(borderSetup.isRight()){
                g2.drawLine(lineWidth, data.getBottomBorderY(), lineWidth, data.getTopBorderY());
              }
            }
          }
         
          translate(g2, xSpacing + lMargin, 0);          
          ComponentPrintInfo compInfo = comp.print(graphics, data, pageIndex, xLimit, yLimit);
          translate(g2, cDim.getWidth() + xSpacing + rMargin, 0);
          needsMorePages = compInfo.isNeedsMorePages();
          if(!needsMorePages) includePage(pageIndex);
        }
        col++;
      }
      this.resetTranslation(g2);
    }
    return new ComponentPrintInfo(needsMorePages, !needsMorePages ? dim : new FPDimension(0, 0));
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
   * @return Returns the minimumColumnsWidths.
   */
  public ArrayList getMinimumColumnsWidths() {
    return minimumColumnsWidths;
  }
  
  /**
   * @param minimumColumnsWidths The minimumColumnsWidths to set.
   */
  public void setMinimumColumnsWidths(ArrayList minimumColumnsWidths) {
    this.minimumColumnsWidths = minimumColumnsWidths;
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

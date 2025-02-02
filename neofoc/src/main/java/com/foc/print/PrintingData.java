/*
 * Created on 29-Apr-2005
 */
package com.foc.print;

import java.awt.*;

/**
 * @author 01Barmaja
 */
public class PrintingData {
  private Graphics2D g2 = null;
  private int topBorderY = 0; 
  private int bottomBorderY = 0;
  private boolean doNotPrint = false;
  
  public PrintingData(){
  }
  
  public Graphics2D getGraphics(){
    return g2;
  }

  public void setGraphics(Graphics2D g2){
    this.g2 = g2;
  }
  
  /**
   * @return Returns the bottomBorderY.
   */
  public int getBottomBorderY() {
    return bottomBorderY;
  }
  
  /**
   * @param bottomBorderY The bottomBorderY to set.
   */
  public void setBottomBorderY(int bottomBorderY) {
    this.bottomBorderY = bottomBorderY;
  }
  
  /**
   * @return Returns the topBorderY.
   */
  public int getTopBorderY() {
    return topBorderY;
  }
  
  /**
   * @param topBorderY The topBorderY to set.
   */
  public void setTopBorderY(int topBorderY) {
    this.topBorderY = topBorderY;
  }
  
  public boolean isDoNotPrint() {
    return doNotPrint;
  }
  
  public void setDoNotPrint(boolean doNotPrint) {
    this.doNotPrint = doNotPrint;
  }
}

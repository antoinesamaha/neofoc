/*
 * Created on 28-Apr-2005
 */
package com.foc.print;

/**
 * @author 01Barmaja
 */
public class FPDimension {
  private int width = 0;
  private int height = 0;
  
  public FPDimension(){
    width = 0;
    height = 0;
  }
  
  public FPDimension(int width, int height){
    this.width = width;
    this.height = height;
  }
  
  /**
   * @return Returns the height.
   */
  public int getHeight() {
    return height;
  }
  /**
   * @param height The height to set.
   */
  public void setHeight(int height) {
    this.height = height;
  }
  /**
   * @return Returns the width.
   */
  public int getWidth() {
    return width;
  }
  /**
   * @param width The width to set.
   */
  public void setWidth(int width) {
    this.width = width;
  }
}

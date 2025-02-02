/*
 * Created on 28-Apr-2005
 */
package com.foc.print;

/**
 * @author 01Barmaja
 */
public class FPLocation {
  private int line;
  private int column;
  private int allignment;
  
  public final static int LEFT = 1;
  public final static int RIGHT = 2;  
  public final static int CENTER = 2;
  
  public FPLocation(int line, int column, int allignment){
    this.line = line;
    this.column = column;
    this.allignment = allignment;
  }
  
  /**
   * @return Returns the line.
   */
  public int getLine() {
    return line;
  }

  /**
   * @return Returns the column.
   */
  public int getColumn() {
    return column;
  }
  
  /**
   * @return Returns the allignment.
   */
  public int getAllignment() {
    return allignment;
  }
}

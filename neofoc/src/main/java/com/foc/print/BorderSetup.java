/*
 * Created on 30-Apr-2005
 */
package com.foc.print;

/**
 * @author 01Barmaja
 */
public class BorderSetup {
  private boolean up = false;
  private boolean down = false;  
  private boolean left = false;
  private boolean right = false;

  public BorderSetup(){
  }
  
  public BorderSetup(boolean up, boolean down, boolean left, boolean right){
    this.up = up;
    this.down = down;  
    this.left = left;
    this.right = right;    
  }
  
  /**
   * @return Returns the down.
   */
  public boolean isDown() {
    return down;
  }
  /**
   * @param down The down to set.
   */
  public void setDown(boolean down) {
    this.down = down;
  }
  /**
   * @return Returns the left.
   */
  public boolean isLeft() {
    return left;
  }
  /**
   * @param left The left to set.
   */
  public void setLeft(boolean left) {
    this.left = left;
  }
  /**
   * @return Returns the right.
   */
  public boolean isRight() {
    return right;
  }
  /**
   * @param right The right to set.
   */
  public void setRight(boolean right) {
    this.right = right;
  }
  /**
   * @return Returns the up.
   */
  public boolean isUp() {
    return up;
  }
  /**
   * @param up The up to set.
   */
  public void setUp(boolean up) {
    this.up = up;
  }
}

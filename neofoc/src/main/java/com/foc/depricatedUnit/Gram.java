/*
 * Created on 01-Feb-2005
 */
package com.foc.depricatedUnit;

/**
 * @author 01Barmaja
 */
public class Gram implements Unit {

  public String getTitle() {
    return "Gram";
  }

  public String getName() {
    return "Gr";
  }

  public int getID() {
    return Unit.GRAM;
  }
}
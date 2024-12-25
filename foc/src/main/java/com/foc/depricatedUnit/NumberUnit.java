/*
 * Created on 01-Feb-2005
 */
package com.foc.depricatedUnit;

/**
 * @author 01Barmaja
 */
public class NumberUnit implements Unit {

  public String getTitle() {
    return "Number";
  }

  public String getName() {
    return "Nb";
  }

  public int getID() {
    return Unit.NB;
  }
}